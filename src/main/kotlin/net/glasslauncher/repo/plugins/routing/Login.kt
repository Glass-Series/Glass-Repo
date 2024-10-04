package net.glasslauncher.repo.plugins.routing

import net.glasslauncher.repo.data.DiscordAuth
import net.glasslauncher.repo.data.SiteConfig
import net.glasslauncher.repo.plugins.CookieData
import net.glasslauncher.repo.plugins.USER_LOGIN_COOKIE
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Application.configureLoginRouting() {
    val localHosts = listOf("localhost", "127.0.0.1")
    log.info("Adding login routes")

    routing {
        get("/login") {
            var host = call.request.headers["Host"] ?: "glass-repo.net" // If host header doesn't exist, we have pretty big problems, but that's not for the program to worry about :)
            host = if (host.split(":")[0] in localHosts) {
                "http://${host}"
            } else {
                "https://${host}"
            }

            val authCode = call.request.queryParameters["code"]
            if (authCode != null) {
                val returned = DiscordAuth(authCode, host).signIn(this)
                host = SiteConfig.config.getDiscordOauthURL(host)

                returned.isLeft {
                    respond(call, mapOf("oauth_url" to host, "error" to it), "login")
                    return@get
                }

                returned.isRight {
                    Thread {
                        it.save() // TODO: Figure out a way to do this without spawning a new thread, this is fucking awful.
                    }
                    call.sessions.set(CookieData(it.id))
                    call.respondRedirect("/")
                    return@get
                }
            }

            host = SiteConfig.config.getDiscordOauthURL(host)

            val error = call.request.queryParameters["error"]
            if (error != null) {
                respond(call, mapOf("oauth_url" to host, "error" to error), "login")
                return@get
            }

            respond(call, mapOf("oauth_url" to host), "login")
        }

        get("/logout") {
            if (call.sessions.get(USER_LOGIN_COOKIE) == null) {
                call.respond(HttpStatusCode.Unauthorized)
                return@get
            }

            call.sessions.clear(USER_LOGIN_COOKIE)
            call.respondRedirect("/")
        }
    }
}