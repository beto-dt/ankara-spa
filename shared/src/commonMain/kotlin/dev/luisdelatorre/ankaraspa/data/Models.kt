package dev.luisdelatorre.ankaraspa.data

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