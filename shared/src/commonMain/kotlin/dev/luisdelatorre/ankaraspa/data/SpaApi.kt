package dev.luisdelatorre.ankaraspa.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val BASE_URL = "https://us-central1-ankara-spa-demo.cloudfunctions.net"

class SpaApi {
    private val http = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun catalog(): Catalog = http.get("$BASE_URL/getCatalog").body()
}