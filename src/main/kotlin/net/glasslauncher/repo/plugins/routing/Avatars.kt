package net.glasslauncher.repo.plugins.routing

import net.glasslauncher.repo.plugins.routing.objs.Avatars
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Application.configureAvatarsRouting() {
    log.info("Adding avatars routes")

    routing {
        get<Avatars> {

            val fileExt = File("repo/avatars/${it.id}.meta").readText()
            val file = File("repo/avatars/${it.id}${fileExt}")
            call.response.header(HttpHeaders.ContentDisposition, "attachment; filename=\"${file.name}\"")
            call.respondFile(file)
        }
    }
}