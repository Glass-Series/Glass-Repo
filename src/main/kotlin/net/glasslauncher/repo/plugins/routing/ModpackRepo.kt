package net.glasslauncher.repo.plugins.routing

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureModpackRepoRouting() {
    log.info("Adding modpack repo routes")

    routing {
        get("/repo/modpacks") {

            respond(call, override = "repo/modpacks")
        }
    }
}