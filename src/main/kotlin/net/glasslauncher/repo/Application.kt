package net.glasslauncher.repo

import net.glasslauncher.repo.data.minecraft.VersionDetails
import net.glasslauncher.repo.data.minecraft.VersionManifestList
import net.glasslauncher.repo.plugins.configureHTTP
import net.glasslauncher.repo.plugins.configureSessions
import net.glasslauncher.repo.plugins.configureSockets
import net.glasslauncher.repo.plugins.configureTemplating
import net.glasslauncher.repo.plugins.routing.configureRouting
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
import kotlin.system.exitProcess

fun main() {
    if (!File("config.json5").exists()) {
        System.err.println("Rename the example config to \"config.json5\" and fill it out.")
        exitProcess(1)
    }

    System.getProperties().setProperty("io.ktor.development", "true")

    val cacheFile = Repo.mcMetaPath.cd("index.json")
    var json: VersionManifestList
    try {
        json = JsonReader.fromJson(
            URL("https://skyrising.github.io/mc-versions/version_manifest.json").openStream().readAllBytes()
                .toString(StandardCharsets.UTF_8)
        )
        cacheFile.writeText(JsonReader.toJson(json))
    } catch (e: Exception) {
        GlassLogger.INSTANCE.error("Unable to get remote manifests, using last successfully cached file.")
        json = JsonReader.fromJson(cacheFile.readText())
    }
    json.process()
    VersionManifestList.allVersions.entries.stream().sorted(Comparator.comparing<MutableMap.MutableEntry<String, VersionDetails>?, Long?> { it.value.releaseTime }.reversed()).forEach {
        val header = if (it.value.useJustMajorAsHeader) it.value.majorVersion else "${it.value.majorVersion}.${it.value.minorVersion}"
        VersionManifestList.allVersionsKeys.add(it.key)
        VersionManifestList.headerToVersions.putIfAbsent(header, ArrayList())
        VersionManifestList.headerToVersions[header]!!.add(it.value)
    }

    GlassLogger.INSTANCE.info("Indexed ${VersionManifestList.allVersions.size} MC versions.")

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
