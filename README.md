# Ankara Spa 🌿

Booking app for a fictional sauna & massage studio — built to showcase a
complete **Kotlin Multiplatform** product: one shared codebase, a real
backend, running in production.

**Live demo:** [spa.luisdelatorre.dev](https://spa.luisdelatorre.dev) · *bookings reset nightly*

## What it does

- Browse services, pick a day and a time slot with **real availability**
- Book in one tap — no sign-up (anonymous device identity), get a code like `ANK-3F7K`
- **My bookings** with cancellation, persisted across restarts
- **Admin panel** (email/password): today's agenda per therapist, mark completed / no-show

## Architecture

```
Compose Multiplatform UI  (Android · Web/Wasm · iOS-ready)
        │
        │  Ktor Client + kotlinx.serialization  (shared commonMain)
        ▼
Cloud Functions (TypeScript)  ── REST, the only door to the data
        ▼
Firestore  ── rules locked down: no client access at all
```

### Decisions worth reading

- **No Firebase SDK in the client.** All data access goes through HTTP
  endpoints with Ktor — auth included (Firebase Auth REST on the client,
  Admin SDK token verification on the server). Firestore rules are
  `allow read, write: if false`.
- **Anti-double-booking transaction.** `createBooking` re-reads the day's
  bookings *inside* a Firestore transaction — two clients racing for the same
  slot: one gets the booking, the other gets a clean `409 slot_taken`.
- **Fixed UTC-05:00 timezone** instead of the IANA database: Ecuador has no
  DST, and named zones would ship a multi-MB tzdb to the Wasm target.
- **Sandbox by design.** A scheduled function wipes past bookings nightly,
  so the demo always feels fresh.

## Stack

Kotlin Multiplatform · Compose Multiplatform · Ktor · kotlinx.serialization ·
Navigation Compose · multiplatform-settings · Firebase (Cloud Functions/TS,
Firestore, Auth, Hosting)

## Project layout

```
shared/      Compose UI, navigation, data layer (commonMain does ~all the work)
androidApp/  Android entry point
webApp/      Web (Wasm) entry point
iosApp/      iOS entry point (phase 3)
backend/     Cloud Functions (TypeScript) + Firestore seed scripts
```

## Run it

```bash
# Web (Wasm)
./gradlew :webApp:wasmJsBrowserDevelopmentRun

# Android: open the project in Android Studio and run androidApp

# Backend seed (needs your own Firebase project + service account)
GOOGLE_APPLICATION_CREDENTIALS=./service-account.json npm --prefix backend run seed
```

---

Built by [Luis Alberto De La Torre](https://luisdelatorre.dev) — Senior
Full-Stack & Mobile developer · [more projects](https://luisdelatorre.dev/proyectos)