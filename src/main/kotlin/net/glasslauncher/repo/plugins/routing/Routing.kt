package net.glasslauncher.repo.plugins.routing

import net.glasslauncher.repo.Repo
import net.glasslauncher.repo.data.SiteConfig
import net.glasslauncher.repo.data.user.Users
import net.glasslauncher.repo.helper.collectRoutes
import net.glasslauncher.repo.plugins.CookieData
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.pebble.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.reflect.*
import kotlin.collections.set

fun Application.configureRouting() {
    println("Click: http://127.0.0.1:8080")

    install(Resources)
    install(IgnoreTrailingSlash)
    install(StatusPages) {

        status(HttpStatusCode.NotFound) { call, status ->
            respond(call, override = "errors/404", errorCode = status)
        }

        status(HttpStatusCode.Forbidden) { call, status ->
            respond(call, override = "errors/403", errorCode = status)
        }

        status(HttpStatusCode.Unauthorized) { call, status ->
            respond(call, override = "errors/401", errorCode = status)
        }

        exception<AuthenticationException> { call, _ ->
            call.respond(HttpStatusCode.Unauthorized)
        }
        exception<AuthorizationException> { call, _ ->
            call.respond(HttpStatusCode.Forbidden)
        }
    }
    

    routing {
        staticResources("static", "net.glasslauncher.repo.static")

        get("/") {
            respond(call, mapOf("mod" to Repo.modRepo.cache.get(Repo.modRepo.cache.keys.random())!!))
        }

        get("/terms") {
            respond(call)
        }

        get("/contact") {
            respond(call)
        }

        get("/data") {
            respond(call)
        }

        get("/info") {
            respond(call)
        }

        get("/repo") {
            Repo.modRepo.reloadRepo()
            respond(call)
        }

    }
    configureModRepoRouting()
    configureModpackRepoRouting()
    configureApiRouting()
    configureAvatarsRouting()
    configureLoginRouting()
    configureProfileRouting()

    println(collectRoutes())
}

suspend fun respond(call: ApplicationCall, vars: Map<String, Any> = HashMap(), override: String? = null, errorCode: HttpStatusCode? = null) {
    val vars = HashMap(vars)
    vars["site_version"] = SiteConfig.siteVersion
    val cookie = call.sessions.get<CookieData>()
    if (cookie != null) {
        vars["usersession"] = Users.users[cookie.userId]
    }
    if (call.instanceOf(RoutingApplicationCall::class)) {
        var path: String = (call as RoutingApplicationCall).route.parent.toString()
        if (override != null) {
            path = override
        }
        else if (path == "/") {
            path += "index"
        }
        else if (path == "/repo" || path == "/repo/") {
            path += "/index"
        }
        call.respond(PebbleContent("$path.peb", vars))
    }
    else if (call.instanceOf(NettyApplicationCall::class)) {
        if (errorCode != null) {
            call.respond(errorCode, PebbleContent("errors/${errorCode.value}.peb", vars))
        }
        call.respond("Uh oh, an error that gave no error code! Something went very wrong! Error code: $errorCode")
    }
}

class AuthenticationException : RuntimeException()
class AuthorizationException : RuntimeException()
