package dev.luisdelatorre.ankaraspa.ui

object Routes {
    const val HOME = "home"
    const val SERVICE = "service/{serviceId}"
    const val BOOKING = "booking/{serviceId}"
    const val SUCCESS = "success/{code}"
    const val MY_BOOKINGS = "my-bookings"

    const val ADMIN = "admin"

    fun service(id: String) = "service/$id"
    fun booking(id: String) = "booking/$id"
    fun success(code: String) = "success/$code"
}