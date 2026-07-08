package dev.luisdelatorre.ankaraspa

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform