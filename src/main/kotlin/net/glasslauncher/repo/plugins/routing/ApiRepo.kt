package net.glasslauncher.repo.plugins.routing

import glasslauncher.net.repo.JsonReader
import glasslauncher.net.repo.Repo
import glasslauncher.net.repo.data.ValidModValues
import glasslauncher.net.repo.plugins.routing.objs.ApiRoute
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString

fun Application.configureApiRouting() {
    log.info("Adding API routes")

    routing {
        get<ApiRoute.ValidModValues> {
            call.response.header("Content-Type", "application/json")
            call.respond(JsonReader.INSTANCE.encodeToString(ValidModValues.Companion))
        }

        get<ApiRoute.Mod> { params ->
            call.response.header("Content-Type", "application/json")
            call.respond(JsonReader.INSTANCE.encodeToString(Repo.modRepo.cache[params.id]))
        }
    }
}