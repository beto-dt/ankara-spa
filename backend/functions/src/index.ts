import { onRequest } from 'firebase-functions/v2/https';
import { onSchedule } from 'firebase-functions/v2/scheduler';
import { initializeApp, getApps } from 'firebase-admin/app';
import { getFirestore, Timestamp } from 'firebase-admin/firestore';
import { getAuth } from 'firebase-admin/auth';

if (!getApps().length) initializeApp();
const db = getFirestore();

const OPEN_HOUR = 10;
const CLOSE_HOUR = 20; // last slot starts at 19:00
const DAYS_AHEAD = 7;
const ADMIN_EMAIL = 'luis.atorred24@gmail.com';

class SlotTakenError extends Error {}

/** Booking code like ANK-3F7K (no 0/O/1/I to avoid confusion). */
function genCode(): string {
  const chars = '23456789ABCDEFGHJKMNPQRSTUVWXYZ';
  let s = '';
  for (let i = 0; i < 4; i++) s += chars[Math.floor(Math.random() * chars.length)];
  return `ANK-${s}`;
}

/** 'YYYY-MM-DD' for today in spa's timezone. */
function todayKey(): string {
  return new Date().toLocaleDateString('sv-SE', { timeZone: 'America/Guayaquil' });
}

function isValidDate(date: string): boolean {
  if (!/^\d{4}-\d{2}-\d{2}$/.test(date)) return false;
  const today = todayKey();
  const max = new Date(Date.now() + DAYS_AHEAD * 86400000).toLocaleDateString('sv-SE', { timeZone: 'America/Guayaquil' });
  return date >= today && date <= max;
}

/** Hours a booking occupies: 60min→[t], 75/90min→[t, t+1]. */
function occupiedHours(start: number, durationMin: number): number[] {
  const n = Math.ceil(durationMin / 60);
  return Array.from({ length: n }, (_, i) => start + i);
}

type Staff = { services: string[]; workHours: { start: number; end: number } };

async function bookedHoursByStaff(date: string): Promise<Map<string, Set<number>>> {
  const snap = await db.collection('bookings')
    .where('date', '==', date).where('status', '==', 'confirmed').get();
  const map = new Map<string, Set<number>>();
  for (const d of snap.docs) {
    const b = d.data();
    const set = map.get(b.staffId) ?? new Set<number>();
    (b.hours as number[]).forEach((h) => set.add(h));
    map.set(b.staffId, set);
  }
  return map;
}

async function verifyAdmin(authHeader: string | undefined): Promise<{ email: string | null; reason: string }> {
  if (!authHeader?.startsWith('Bearer ')) return { email: null, reason: 'no_header' };
  try {
    const decoded = await getAuth().verifyIdToken(authHeader.slice(7));
    if (decoded.email !== ADMIN_EMAIL) return { email: null, reason: `email_mismatch:${decoded.email ?? 'none'}` };
    return { email: decoded.email, reason: 'ok' };
  } catch (e) {
    return { email: null, reason: `verify_failed:${(e as Error).message.slice(0, 300)}` };
  }
}

export const getAgenda = onRequest({ region: 'us-central1', cors: true }, async (req, res) => {
  const auth = await verifyAdmin(req.headers.authorization);
  if (!auth.email) {
    res.status(401).json({ error: 'unauthorized', debug: auth.reason });
    return;
  }
  const date = typeof req.query.date === 'string' && /^\d{4}-\d{2}-\d{2}$/.test(req.query.date)
    ? req.query.date
    : todayKey();
  const snap = await db.collection('bookings')
    .where('date', '==', date)
    .get();
  type AgendaDoc = { id: string; time: number; status: string } & Record<string, unknown>;
  const bookings = snap.docs
    .map((d): AgendaDoc => ({ id: d.id, ...d.data() } as AgendaDoc))
    .filter((b) => b.status !== 'cancelled')
    .sort((a, b) => a.time - b.time);
  res.json({ date, bookings });
});

export const updateBookingStatus = onRequest({ region: 'us-central1', cors: true }, async (req, res) => {
  if (req.method !== 'POST') {
    res.status(405).json({ error: 'method_not_allowed' });
    return;
  }
  const auth = await verifyAdmin(req.headers.authorization);
  if (!auth.email) {
    res.status(401).json({ error: 'unauthorized', debug: auth.reason });
    return;
  }
  const { bookingId, status } = (req.body ?? {}) as Record<string, unknown>;
  if (typeof bookingId !== 'string' || (status !== 'completed' && status !== 'no_show' && status !== 'confirmed')) {
    res.status(400).json({ error: 'bad_request' });
    return;
  }
  const ref = db.doc(`bookings/${bookingId}`);
  if (!(await ref.get()).exists) {
    res.status(404).json({ error: 'not_found' });
    return;
  }
  await ref.update({ status });
  res.json({ ok: true });
});

export const getCatalog = onRequest({ region: 'us-central1', cors: true }, async (req, res) => {
  const [services, staff] = await Promise.all([
    db.collection('services').orderBy('order').get(),
    db.collection('staff').get(),
  ]);
  res.set('Cache-Control', 'public, max-age=300');
  res.json({
    services: services.docs.map((d) => ({ id: d.id, ...d.data() })),
    staff: staff.docs.map((d) => ({ id: d.id, ...d.data() })),
  });
});

export const getAvailability = onRequest({ region: 'us-central1', cors: true }, async (req, res) => {
  const serviceId = String(req.query.serviceId ?? '');
  const date = String(req.query.date ?? '');
  if (!serviceId || !isValidDate(date)) {
    res.status(400).json({ error: 'bad_request' });
    return;
  }
  const serviceSnap = await db.doc(`services/${serviceId}`).get();
  if (!serviceSnap.exists) {
    res.status(404).json({ error: 'service_not_found' });
    return;
  }
  const durationMin = serviceSnap.data()!.durationMin as number;
  const staffSnap = await db.collection('staff').where('services', 'array-contains', serviceId).get();
  const booked = await bookedHoursByStaff(date);

  const slots: { time: number; staffIds: string[] }[] = [];
  for (let t = OPEN_HOUR; t < CLOSE_HOUR; t++) {
    const hours = occupiedHours(t, durationMin);
    if (hours[hours.length - 1] >= CLOSE_HOUR) continue;
    const free = staffSnap.docs.filter((d) => {
      const s = d.data() as Staff;
      const mine = booked.get(d.id) ?? new Set<number>();
      return hours.every((h) => h >= s.workHours.start && h < s.workHours.end && !mine.has(h));
    }).map((d) => d.id);
    if (free.length) slots.push({ time: t, staffIds: free });
  }
  res.json({ date, serviceId, slots });
});

export const createBooking = onRequest({ region: 'us-central1', cors: true }, async (req, res) => {
  if (req.method !== 'POST') {
    res.status(405).json({ error: 'method_not_allowed' });
    return;
  }
  const { serviceId, date, time, clientUid, clientName } = (req.body ?? {}) as Record<string, unknown>;
  if (
    typeof serviceId !== 'string' || typeof date !== 'string' || !isValidDate(date) ||
    typeof time !== 'number' || !Number.isInteger(time) ||
    typeof clientUid !== 'string' || clientUid.length < 8 || clientUid.length > 64
  ) {
    res.status(400).json({ error: 'bad_request' });
    return;
  }
  const serviceSnap = await db.doc(`services/${serviceId}`).get();
  if (!serviceSnap.exists) {
    res.status(404).json({ error: 'service_not_found' });
    return;
  }
  const durationMin = serviceSnap.data()!.durationMin as number;
  const hours = occupiedHours(time, durationMin);
  if (hours[0] < OPEN_HOUR || hours[hours.length - 1] >= CLOSE_HOUR) {
    res.status(400).json({ error: 'bad_time' });
    return;
  }
  const staffSnap = await db.collection('staff').where('services', 'array-contains', serviceId).get();

  try {
    const booking = await db.runTransaction(async (tx) => {
      // Re-read bookings INSIDE the transaction: this is the anti-double-booking lock.
      const daySnap = await tx.get(
        db.collection('bookings').where('date', '==', date).where('status', '==', 'confirmed'),
      );
      const busy = new Map<string, Set<number>>();
      for (const d of daySnap.docs) {
        const b = d.data();
        const set = busy.get(b.staffId) ?? new Set<number>();
        (b.hours as number[]).forEach((h) => set.add(h));
        busy.set(b.staffId, set);
      }
      const staffDoc = staffSnap.docs.find((d) => {
        const s = d.data() as Staff;
        const mine = busy.get(d.id) ?? new Set<number>();
        return hours.every((h) => h >= s.workHours.start && h < s.workHours.end && !mine.has(h));
      });
      if (!staffDoc) throw new SlotTakenError();

      const ref = db.collection('bookings').doc();
      const data = {
        code: genCode(),
        serviceId,
        staffId: staffDoc.id,
        staffName: staffDoc.data().name as string,
        date,
        time,
        hours,
        clientUid,
        clientName: typeof clientName === 'string' ? clientName.trim().slice(0, 60) : null,
        status: 'confirmed',
        createdAt: Timestamp.now(),
      };
      tx.create(ref, data);
      return { id: ref.id, ...data };
    });
    res.json({ booking });
  } catch (e) {
    if (e instanceof SlotTakenError) {
      res.status(409).json({ error: 'slot_taken' });
      return;
    }
    console.error('createBooking failed', e);
    res.status(500).json({ error: 'internal' });
  }
});

export const cancelBooking = onRequest({ region: 'us-central1', cors: true }, async (req, res) => {
  if (req.method !== 'POST') {
    res.status(405).json({ error: 'method_not_allowed' });
    return;
  }
  const { bookingId, clientUid } = (req.body ?? {}) as Record<string, unknown>;
  if (typeof bookingId !== 'string' || typeof clientUid !== 'string') {
    res.status(400).json({ error: 'bad_request' });
    return;
  }
  const ref = db.doc(`bookings/${bookingId}`);
  const snap = await ref.get();
  if (!snap.exists || snap.data()!.clientUid !== clientUid) {
    res.status(404).json({ error: 'not_found' });
    return;
  }
  await ref.update({ status: 'cancelled' });
  res.json({ ok: true });
});

/** Demo sandbox: every night, drop past bookings. */
export const resetDemo = onSchedule(
  { schedule: '0 5 * * *', timeZone: 'America/Guayaquil', region: 'us-central1' },
  async () => {
    const snap = await db.collection('bookings').where('date', '<', todayKey()).get();
    const batch = db.batch();
    snap.docs.forEach((d) => batch.delete(d.ref));
    await batch.commit();
    console.log(`resetDemo: deleted ${snap.size} past bookings`);
  },
);

export const getMyBookings = onRequest({ region: 'us-central1', cors: true }, async (req, res) => {
  const clientUid = String(req.query.clientUid ?? '');
  if (clientUid.length < 8 || clientUid.length > 64) {
    res.status(400).json({ error: 'bad_request' });
    return;
  }
  const snap = await db.collection('bookings')
    .where('clientUid', '==', clientUid)
    .where('status', '==', 'confirmed')
    .get();
  type BookingDoc = { id: string; date: string; time: number } & Record<string, unknown>;
  const bookings = snap.docs
    .map((d): BookingDoc => ({ id: d.id, ...d.data() } as BookingDoc))
    .filter((b) => b.date >= todayKey())
    .sort((a, b) => a.date.localeCompare(b.date) || a.time - b.time);
  res.json({ bookings });
});