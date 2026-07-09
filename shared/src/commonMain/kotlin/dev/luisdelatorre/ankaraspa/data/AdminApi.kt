package dev.luisdelatorre.ankaraspa.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val BASE_URL = "https://us-central1-ankara-spa-demo.cloudfunctions.net"
private const val AUTH_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword"
private const val TOKEN_URL = "https://securetoken.googleapis.com/v1/token"
private const val WEB_API_KEY = "AIzaSyC4w1MbycQvc4ayOyzdeNfh_3WF8RhrYeY"

class LoginFailedException : Exception()

class AdminApi {
    private val http = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true; encodeDefaults = true })
        }
    }

    /**
     * Email/password sign-in against Firebase Auth REST. The sign-in response
     * carries an Identity-Platform-flavored token, so we exchange the refresh
     * token at the securetoken endpoint: those ID tokens carry the
     * securetoken.google.com issuer that the Admin SDK verifies server-side.
     */
    suspend fun login(email: String, password: String): String {
        val res = http.post("$AUTH_URL?key=$WEB_API_KEY") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(email, password))
        }
        if (!res.status.isSuccess()) throw LoginFailedException()
        return res.body<LoginResponse>().idToken
    }

    suspend fun agenda(token: String, date: String? = null): AgendaResponse =
        http.get("$BASE_URL/getAgenda") {
            header(HttpHeaders.Authorization, "Bearer $token")
            if (date != null) parameter("date", date)
        }.body()

    suspend fun metrics(token: String): MetricsResponse =
        http.get("$BASE_URL/getMetrics") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()

    suspend fun updateStatus(token: String, bookingId: String, status: String) {
        val res = http.post("$BASE_URL/updateBookingStatus") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(UpdateStatusRequest(bookingId, status))
        }
        if (!res.status.isSuccess()) throw Exception("updateStatus ${res.status}")
    }
}