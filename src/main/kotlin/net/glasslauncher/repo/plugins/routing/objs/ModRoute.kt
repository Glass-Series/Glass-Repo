package net.glasslauncher.repo.plugins.routing.objs

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/repo/mod")
class ModRoute {

    @Resource("{id}")
    class View(val parent: ModRoute = ModRoute(), val id: String) {

        @Resource("versions")
        class Versions(val parent: ModRoute.View, val id: String) {
            @Resource("{version}")
            class View(val parent: Versions, val id: String, val version: String) {
                @Resource("download")
                class Download(val parent: Versions.View, val id: String, val version: String)
            }
        }

        @Resource("edit")
        class Edit(val parent: View)
    }
}