package net.glasslauncher.repo.plugins.routing

import net.glasslauncher.repo.JsonReader
import net.glasslauncher.repo.Repo
import net.glasslauncher.repo.data.ValidModValues
import net.glasslauncher.repo.plugins.routing.objs.ApiRoute
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