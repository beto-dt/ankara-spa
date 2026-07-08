package dev.luisdelatorre.ankaraspa.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import io.ktor.client.request.parameter
import io.ktor.http.contentType
import io.ktor.http.isSuccess

private const val BASE_URL = "https://us-central1-ankara-spa-demo.cloudfunctions.net"

class SlotTakenException : Exception()

class SpaApi {
    private val http = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun catalog(): Catalog = http.get("$BASE_URL/getCatalog").body()

    suspend fun availability(serviceId: String, date: String): Availability =
        http.get("$BASE_URL/getAvailability") {
            parameter("serviceId", serviceId)
            parameter("date", date)
        }.body()

    suspend fun createBooking(req: CreateBookingRequest): Booking {
        val res = http.post("$BASE_URL/createBooking") {
            contentType(ContentType.Application.Json)
            setBody(req)
        }
        if (res.status == HttpStatusCode.Conflict) throw SlotTakenException()
        if (!res.status.isSuccess()) throw Exception("createBooking ${res.status}")
        return res.body<BookingResponse>().booking
    }
}