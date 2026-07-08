import { onRequest } from 'firebase-functions/v2/https';
import { initializeApp, getApps } from 'firebase-admin/app';
import { getFirestore } from 'firebase-admin/firestore';

if (!getApps().length) initializeApp();
const db = getFirestore();

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