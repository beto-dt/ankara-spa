import admin from 'firebase-admin';

admin.initializeApp({
  credential: admin.credential.applicationDefault(),
  projectId: 'ankara-spa-demo',
});
const db = admin.firestore();

const services = [
  { id: 'sauna-finlandes', order: 1, durationMin: 60, price: 25,
    name: { es: 'Sauna finlandés', en: 'Finnish sauna' },
    description: { es: 'Calor seco a 85°C, descanso profundo y recuperación muscular.', en: 'Dry heat at 85°C, deep rest and muscle recovery.' } },
  { id: 'bano-vapor', order: 2, durationMin: 60, price: 22,
    name: { es: 'Baño de vapor', en: 'Steam bath' },
    description: { es: 'Vapor con eucalipto que abre la respiración y relaja la piel.', en: 'Eucalyptus steam that opens breathing and relaxes the skin.' } },
  { id: 'masaje-relajante', order: 3, durationMin: 60, price: 35,
    name: { es: 'Masaje relajante', en: 'Relaxing massage' },
    description: { es: 'Presión suave y ritmo lento: tensión fuera, calma dentro.', en: 'Gentle pressure and slow rhythm: tension out, calm in.' } },
  { id: 'masaje-deportivo', order: 4, durationMin: 60, price: 40,
    name: { es: 'Masaje deportivo', en: 'Sports massage' },
    description: { es: 'Trabajo profundo para entrenamientos exigentes y contracturas.', en: 'Deep work for demanding training and muscle knots.' } },
  { id: 'piedras-calientes', order: 5, durationMin: 75, price: 45,
    name: { es: 'Piedras calientes', en: 'Hot stones' },
    description: { es: 'El clásico que nunca falla: calor puntual sobre la espalda.', en: 'The classic that never fails: targeted heat along the back.' } },
  { id: 'ritual-andino', order: 6, durationMin: 90, price: 55,
    name: { es: 'Ritual andino', en: 'Andean ritual' },
    description: { es: 'Exfoliación, envoltura y masaje con aceites de la sierra.', en: 'Exfoliation, wrap and massage with highland oils.' } },
];

const staff = [
  { id: 'valentina', name: 'Valentina R.', services: ['masaje-relajante', 'masaje-deportivo', 'piedras-calientes', 'ritual-andino'], workHours: { start: 10, end: 18 } },
  { id: 'mateo', name: 'Mateo C.', services: ['sauna-finlandes', 'bano-vapor', 'masaje-deportivo'], workHours: { start: 12, end: 20 } },
  { id: 'amara', name: 'Amara Q.', services: ['masaje-relajante', 'piedras-calientes', 'ritual-andino', 'sauna-finlandes', 'bano-vapor'], workHours: { start: 10, end: 20 } },
];

async function main() {
  const batch = db.batch();
  for (const { id, ...data } of services) batch.set(db.doc(`services/${id}`), data);
  for (const { id, ...data } of staff) batch.set(db.doc(`staff/${id}`), data);
  await batch.commit();
  console.log(`Seeded ${services.length} services, ${staff.length} staff`);
}

main().then(() => process.exit(0)).catch((e) => { console.error(e); process.exit(1); });