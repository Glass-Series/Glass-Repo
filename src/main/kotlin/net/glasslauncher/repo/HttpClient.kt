package net.glasslauncher.repo

import io.ktor.client.*
import io.ktor.client.engine.apache5.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class HttpClient {
    companion object {
        val client = HttpClient(Apache5) {
//            install(Logging) {
//                level = LogLevel.INFO // I don't need this to be verbose 99% of the time
//            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }
}