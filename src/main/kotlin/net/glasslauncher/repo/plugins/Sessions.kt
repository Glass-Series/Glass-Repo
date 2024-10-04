package net.glasslauncher.repo.plugins

import io.ktor.server.application.*
import io.ktor.server.sessions.*
import java.io.File

const val USER_LOGIN_COOKIE = "user_login"

fun Application.configureSessions() {
    install(Sessions) {
        cookie<CookieData>(USER_LOGIN_COOKIE, directorySessionStorage(File("sessions"))) {
            cookie.maxAgeInSeconds = 1 * 60 * 60 * 24 * 7 // 1 week.
        }
    }

}

data class CookieData(val userId: String)