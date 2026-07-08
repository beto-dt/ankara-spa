package dev.luisdelatorre.ankaraspa.data

import com.russhwolf.settings.Settings
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
    private val settings = Settings()

    /** Anonymous device identity: created once, survives app restarts. */
    val clientUid: String by lazy {
        settings.getStringOrNull("clientUid")
            ?: Uuid.random().toString().also { settings.putString("clientUid", it) }
    }
}

// Fixed offset instead of "America/Guayaquil": Ecuador has no DST, and named
// zones require shipping the multi-MB IANA database to JS/Wasm targets.
val SPA_TZ = TimeZone.of("UTC-05:00")

@OptIn(ExperimentalTime::class)
fun nextDays(n: Int): List<LocalDate> {
    val today = Clock.System.todayIn(SPA_TZ)
    return (0 until n).map { today.plus(it, DateTimeUnit.DAY) }
}