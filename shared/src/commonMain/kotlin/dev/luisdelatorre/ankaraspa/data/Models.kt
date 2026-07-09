package dev.luisdelatorre.ankaraspa.data
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class LocalizedText(val es: String, val en: String)

@Serializable
data class Service(
    val id: String,
    val order: Int,
    val durationMin: Int,
    val price: Int,
    val name: LocalizedText,
    val description: LocalizedText,
)

@Serializable
data class Staff(val id: String, val name: String, val services: List<String>)

@Serializable
data class Catalog(val services: List<Service>, val staff: List<Staff>)

@Serializable
data class Slot(val time: Int, val staffIds: List<String>)

@Serializable
data class Availability(val date: String, val serviceId: String, val slots: List<Slot>)

@Serializable
data class Booking(
    val id: String,
    val code: String,
    val serviceId: String,
    val staffName: String,
    val date: String,
    val time: Int,
    val status: String,
)

@Serializable
data class BookingResponse(val booking: Booking)

@Serializable
data class CreateBookingRequest(
    val serviceId: String,
    val date: String,
    val time: Int,
    val clientUid: String,
    val clientName: String?,
)

@Serializable
data class MyBookingsResponse(val bookings: List<Booking>)

@Serializable
data class CancelRequest(val bookingId: String, val clientUid: String)

@Serializable
data class LoginRequest(val email: String, val password: String, val returnSecureToken: Boolean = true)

@Serializable
data class LoginResponse(val idToken: String, val refreshToken: String)

@Serializable
data class TokenExchangeResponse(@SerialName("id_token") val idToken: String)

@Serializable
data class AgendaBooking(
    val id: String,
    val code: String,
    val serviceId: String,
    val staffName: String,
    val time: Int,
    val status: String,
    val clientName: String? = null,
)

@Serializable
data class AgendaResponse(val date: String, val bookings: List<AgendaBooking>)

@Serializable
data class UpdateStatusRequest(val bookingId: String, val status: String)