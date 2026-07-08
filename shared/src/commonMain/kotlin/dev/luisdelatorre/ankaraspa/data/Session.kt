package dev.luisdelatorre.ankaraspa.data

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
object Session {
    // TODO(6C): persistir con multiplatform-settings; por ahora vive por sesión.
    val clientUid: String = Uuid.random().toString()
}

val SPA_TZ = TimeZone.of("UTC-05:00")

@OptIn(ExperimentalTime::class)
fun nextDays(n: Int): List<LocalDate> {
    val today = Clock.System.todayIn(SPA_TZ)
    return (0 until n).map { today.plus(it, DateTimeUnit.DAY) }
}