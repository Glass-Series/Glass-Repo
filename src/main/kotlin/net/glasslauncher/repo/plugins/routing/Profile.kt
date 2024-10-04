package net.glasslauncher.repo.plugins.routing

import net.glasslauncher.repo.plugins.CookieData
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Application.configureProfileRouting() {
    log.info("Adding profile routes")

    routing {
        get("/profile") {
            val cookieData = call.sessions.get<CookieData>()
            if (cookieData == null) {
                call.respond(HttpStatusCode.Unauthorized)
            }

            respond(call, mapOf(), "editprofile")
        }
    }
}