package net.glasslauncher.repo.plugins.routing

import net.glasslauncher.repo.GlassLogger
import net.glasslauncher.repo.JsonReader
import net.glasslauncher.repo.Repo
import net.glasslauncher.repo.data.ModReference
import net.glasslauncher.repo.data.ValidModValues
import net.glasslauncher.repo.data.minecraft.VersionManifestList
import net.glasslauncher.repo.data.mod.ModSkeleton
import net.glasslauncher.repo.helper.getOrDefault
import net.glasslauncher.repo.helper.queryParameters
import net.glasslauncher.repo.plugins.CookieData
import net.glasslauncher.repo.plugins.routing.objs.ModRoute
import net.glasslauncher.repo.plugins.routing.objs.ModsRoute
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.declaredMemberProperties

fun Application.configureModRepoRouting() {
    GlassLogger.INSTANCE.info("Loading mod repo...")
    Repo.modRepo.reloadRepo()
    GlassLogger.INSTANCE.info("Mod repo loaded.")
    GlassLogger.INSTANCE.info("Adding mod repo routes")
    routing {
        get<ModsRoute> {
            respond(call, mapOf(
                "modpack_list" to Repo.modRepo.filterMods(call.request.queryParameters),
                "didSearch" to (call.request.queryParameters["searchText"]?.isNotEmpty() == true),
                "types" to ValidModValues.types,
                "categories" to ValidModValues.categories,
                "minecraftVersions" to VersionManifestList.allVersionsKeys,
            ))
        }

        get<ModRoute.View> { params ->
            val mod = Repo.modRepo.cache[params.id]
            if (mod == null) {
                call.respond(HttpStatusCode(404, "no mod exists with the id ${params.id}"))
                return@get
            }
            val latestVersion = mod.getLatestVersion()
            val modDesc = mod.renderedDescription
            var map = mapOf("mod_obj" to mod, "mod_desc" to modDesc, "is_author" to mod.isAuthor(call.sessions.get<CookieData>()?.userId))
            if (latestVersion != null) {
                map = map.plus("latest_version" to latestVersion)
            }
            respond(call, map, "repo/mod_display")
        }

        get<ModRoute.View.Versions.View> { params ->
            val mod = Repo.modRepo.cache[params.id]
            if (mod == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }
            val version = mod.versionsMap[params.version]
            if (version == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }
            respond(call, mapOf("version_obj" to version, "mod_obj" to mod, "is_author" to mod.isAuthor(call.sessions.get<CookieData>()?.userId)), "repo/mod_version_display")
        }

        get<ModRoute.View.Versions.View.Download> { params ->
            val mod = Repo.modRepo.cache[params.id]!!
            val version = mod.versionsMap[params.version]!!

            val file = version.getFile(queryParameters().getOrDefault("type", "zip"), queryParameters().getOrDefault("side", "client"));
            call.response.header(HttpHeaders.ContentDisposition, "attachment; filename=\"${file.name}\"")
            call.respondFile(file)
        }

        get<ModRoute.View.Edit> { params ->
            respond(call, mapOf(
                "mod" to Repo.modRepo.cache[params.parent.id]!!,
                "minecraft_versions" to VersionManifestList.headerToVersions,
                "categories" to ValidModValues.categories,
                "types" to ValidModValues.types,
            ), "repo/edit_mod")
        }

        post<ModRoute.View.Edit> {
            try {
                val form = call.receiveParameters()
                val modSkeleton = ModSkeleton()
                modSkeleton.id = it.parent.id

                for (property in ModSkeleton::class.declaredMemberProperties) {
                    // TODO: Figure out the arcane and poorly documented bullshit that is kotlin's type system.
                    if (property.name in listOf("dependencies", "incompatibleMods", "optionalDependencies")) {
                        val newList = form.getAll("${property.name}_name")?.zip(form.getAll("${property.name}_url")!!)?.map { ModReference(it.first, it.second) }?.toList()
                        (property as? KMutableProperty<*>)?.setter?.call(modSkeleton, newList)
                        GlassLogger.INSTANCE.info(form.toString())
                        GlassLogger.INSTANCE.info(JsonReader.toJson(modSkeleton))
                        continue
                    }

                    // There's probably a better way to do this, but I'm at my wit's end.
                    @Suppress("IMPLICIT_CAST_TO_ANY")
                    val value =
                        when (property.returnType.classifier) {
                            List::class -> {
                                form.getAll(property.name)
                            }
                            Map::class -> {
                                form.getAll("${property.name}_keys")?.zip(form.getAll("${property.name}_values")!!)?.toMap()
                            }
                            else -> {
                                form[property.name]
                            }
                        }

                    (property as? KMutableProperty<*>)?.setter?.call(modSkeleton, value)
                }

                GlassLogger.INSTANCE.info(form.toString())
                GlassLogger.INSTANCE.info(JsonReader.toJson(modSkeleton))
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            call.respondRedirect("/repo/mod/${it.parent.id}?editSuccess=true", false)
        }
    }
}