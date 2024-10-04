package net.glasslauncher.repo.plugins

import io.ktor.server.application.*
import io.ktor.server.pebble.*
import io.pebbletemplates.pebble.loader.ClasspathLoader
import io.pebbletemplates.pebble.loader.FileLoader

fun Application.configureTemplating() {

    install(Pebble) {
        loader(
            if (this@configureTemplating.developmentMode) {
                FileLoader().apply {
                    prefix = "src/main/resources/net/glasslauncher/repo/templates"
                    cacheActive(false)
                }
            } else {
                ClasspathLoader().apply {
                    prefix = "net/glasslauncher/repo/templates"
                }
            }
        )
    }

}
