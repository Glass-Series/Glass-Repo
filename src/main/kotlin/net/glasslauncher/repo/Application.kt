package net.glasslauncher.repo

import glasslauncher.net.repo.data.minecraft.VersionDetails
import glasslauncher.net.repo.data.minecraft.VersionManifestList
import glasslauncher.net.repo.plugins.configureHTTP
import glasslauncher.net.repo.plugins.configureSessions
import glasslauncher.net.repo.plugins.configureSockets
import glasslauncher.net.repo.plugins.configureTemplating
import glasslauncher.net.repo.plugins.routing.configureRouting
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.doublereceive.*
import kotlinx.serialization.json.Json
import java.io.File
import java.net.URL
import java.nio.charset.StandardCharsets

fun main() {
    System.getProperties().setProperty("io.ktor.development", "true")

    val cacheFile = File("repo/minecraftMeta/index.json")
    var json: VersionManifestList
    try {
        json = net.glasslauncher.repo.JsonReader.Companion.fromJson(
            URL("https://skyrising.github.io/mc-versions/version_manifest.json").openStream().readAllBytes()
                .toString(StandardCharsets.UTF_8)
        )
        cacheFile.writeText(net.glasslauncher.repo.JsonReader.Companion.toJson(json))
    } catch (e: Exception) {
        net.glasslauncher.repo.GlassLogger.Companion.INSTANCE.error("Unable to get remote manifests, using last successfully cached file.")
        json = net.glasslauncher.repo.JsonReader.Companion.fromJson(cacheFile.readText())
    }
    json.process()
    VersionManifestList.allVersions.entries.stream().sorted(Comparator.comparing<MutableMap.MutableEntry<String, VersionDetails>?, Long?> { it.value.releaseTime }.reversed()).forEach {
        val header = if (it.value.useJustMajorAsHeader) it.value.majorVersion else "${it.value.majorVersion}.${it.value.minorVersion}"
        VersionManifestList.allVersionsKeys.add(it.key)
        VersionManifestList.headerToVersions.putIfAbsent(header, ArrayList())
        VersionManifestList.headerToVersions[header]!!.add(it.value)
    }

    net.glasslauncher.repo.GlassLogger.Companion.INSTANCE.info("Indexed ${VersionManifestList.allVersions.size} MC versions.")

    embeddedServer(Netty, port = 8080, host = "0.0.0.0", watchPaths = listOf("classes")) {
        install(ContentNegotiation) {
            json(Json {
                isLenient = true
            })
        }
        install(DoubleReceive)
        configureSessions()
        configureRouting()
        configureSockets()
        configureTemplating()
        configureHTTP()
    }.start(wait = true)
}
