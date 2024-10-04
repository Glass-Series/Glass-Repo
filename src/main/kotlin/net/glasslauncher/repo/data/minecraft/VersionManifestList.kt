package net.glasslauncher.repo.data.minecraft

import glasslauncher.net.repo.GlassLogger
import glasslauncher.net.repo.JsonReader
import glasslauncher.net.repo.data.SiteConfig
import glasslauncher.net.repo.readJson
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.io.File
import java.net.URL
import java.nio.channels.Channels
import java.nio.charset.StandardCharsets
import java.util.concurrent.*
import java.util.zip.ZipFile
import kotlin.io.path.Path
import kotlin.io.path.createFile
import kotlin.io.path.exists
import kotlin.io.path.writeText

@Serializable
class VersionManifestList(
    @SerialName("\$schema")
    val schema: String,
    val latest: LatestVersionsEntry,
    val versions: Array<VersionManifestListEntry>,
) {
    companion object {
        val clientClassMaps = ConcurrentHashMap<String, Map<String, Boolean>>()
        val serverClassMaps = ConcurrentHashMap<String, Map<String, Boolean>>()
        val allVersions = ConcurrentHashMap<String, VersionDetails>()
        val allVersionsKeys = ArrayList<String>()
        var headerToVersions = LinkedHashMap<String, ArrayList<VersionDetails>>()

        val versionRegex = Regex("(^[a-z ]*?[0-9]?)\\.([0-9]*)\\.?-?(.*)?", RegexOption.IGNORE_CASE)

        // TODO: Do this better. Low priority.
        fun addToClassMap(id: String, map: Map<String, Boolean>, details: VersionDetails, side: String) {
            if (details.isFromCache) {
                allVersions[id] = details
                if (side == "client") {
                    clientClassMaps[id] = map
                }
                else {
                    serverClassMaps[id] = map
                }
                return
            }

            var workingId = id
            var justMajorForHeader = false

            if (id.startsWith("server-")) {
                workingId = id.substring(7)
            }
            else if (id.startsWith("in-")) {
                workingId = id.replace("-", ".").replace("in.", "Indev.")
                justMajorForHeader = true
            }
            else if (id.startsWith("inf-")) {
                workingId = id.replace("-", ".").replace("inf.", "Infdev.")
                justMajorForHeader = true
            }
            else if (id.startsWith("combat-")) { // On glass repo prod, this block should never fire. Mostly exists for those who want it listed on their instance.
                val parts = id.split("-")
                workingId = "Combat Test.${parts[1][0]}${if (parts[1].length > 1) ".${parts[1][1]}" else ""}"
                justMajorForHeader = true
            }
            else if (id.startsWith("rd-")) {
                workingId = "Rubydung.${id.replace("-launcher", "").replace("rd-", "")}"
                justMajorForHeader = true
            }

            val parsedVersion = versionRegex.find(workingId)
            if (parsedVersion?.groups == null || parsedVersion.groups.size < 3) { // Not in a supported version format.
                GlassLogger.INSTANCE.warn("Couldn't index ${id} due to having an unsupported version ID.")
                return
            }

            val major = parsedVersion.groups[1]!!.value
            val minor = parsedVersion.groups[2]!!.value

            val bugfix = parsedVersion.groups[3]?.value

            details.majorVersion = major
            details.minorVersion = minor
            details.bugfixVersion = bugfix
            details.useJustMajorAsHeader = justMajorForHeader
            allVersions[id] = details
            if (side == "client") {
                clientClassMaps[id] = map
            }
            else {
                serverClassMaps[id] = map
            }
            File("repo/minecraftMeta/${id}.json").writeText(JsonReader.toJson(details))
        }
    }

    fun process() {
        val executor = Executors.newFixedThreadPool(20) as ThreadPoolExecutor

        versions.forEach {
            executor.submit(it::process)
        }
        executor.shutdown()
        if (!executor.awaitTermination(3L, TimeUnit.HOURS)) { // If you can't process the data in this time, you *really* need more bandwidth.
            executor.shutdownNow()
            throw TimeoutException("Timed out while generating minecraft classmaps (${executor.completedTaskCount}/${executor.taskCount} completed.) If you have an extremely slow server, or extremely slow internet, I warn against trying to host a public instance of this website.")
        }
    }
}

@Serializable
class VersionManifestListEntry(
    val id: String,
    @Serializable(JsonReader.URLSerializer::class)
    val url: URL,
    @Serializable(JsonReader.URLSerializer::class)
    val details: URL,
    @SerialName("type")
    val releaseType: String,
) {
    fun process() {
        // Versions we don't want are filtered here.
        if (releaseType == "snapshot") {
            return
        }
        if (id.startsWith("1.") || id.startsWith("combat")) { // Remove this to allow all release versions.
            return
        }
        GlassLogger.INSTANCE.info("Processing minecraft version ${id}")
        try {
            val detailsCacheFile = File("repo/minecraftMeta/${id}.json")
            var detailsObj: VersionDetails
            if (!detailsCacheFile.exists()) {
                detailsObj = JsonReader.fromJson<VersionDetails>(details.openStream().readAllBytes().toString(StandardCharsets.UTF_8))
                detailsCacheFile.writeText(JsonReader.toJson(detailsObj))
            }
            else {
                try {
                    detailsObj = JsonReader.fromJson(detailsCacheFile.readText())
                    detailsObj.isFromCache = true
                } catch (e: Exception) {
                    GlassLogger.INSTANCE.error("An exception occurred while trying to read cached version details for ${id}!")
                    detailsObj = JsonReader.fromJson<VersionDetails>(details.openStream().readAllBytes().toString(StandardCharsets.UTF_8))
                }
            }
            generateOrLoadClassMap(detailsObj.downloads.client, detailsObj)
            generateOrLoadClassMap(detailsObj.downloads.server, detailsObj)
            SiteConfig.minecraftVersions[id] = detailsObj
        } catch (e: Exception) {
            GlassLogger.INSTANCE.error("An exception occurred while trying to process the classmaps for ${id}!", e)
        }
    }

    fun generateOrLoadClassMap(download: VersionDownload?, details: VersionDetails) {
        if(download == null) {
            return
        }
        val cacheFile = Path("repo/classMaps/${id}_${download.side}.json")

        if (cacheFile.exists()) {
            try {
                VersionManifestList.addToClassMap(id, cacheFile.readJson<HashMap<String, Boolean>>(), details, download.side)
                return
            } catch (e: Exception) {
                GlassLogger.INSTANCE.error("Error occurred while reading a classmap json. Rebuilding.", e)
            }
        }

        val minecraftCachePath = File("repo/minecraftCache/${id}-${download.side}.jar")

        if (!minecraftCachePath.exists()) {
            minecraftCachePath.createNewFile()
            minecraftCachePath.outputStream().channel.transferFrom(Channels.newChannel(download.url.openStream()), 0, Long.MAX_VALUE)
        }

        var zipFile: ZipFile
        try {
            zipFile = ZipFile(minecraftCachePath)
        } catch (e: Exception) {
            GlassLogger.INSTANCE.error("Error occurred while reading a jar file for classmapping. Redownloading.", e)
            minecraftCachePath.delete()
            minecraftCachePath.createNewFile()
            minecraftCachePath.outputStream().channel.transferFrom(Channels.newChannel(download.url.openStream()), 0, Long.MAX_VALUE)
            zipFile = ZipFile(minecraftCachePath) // Crash and burn if it don't work
        }

        val map = HashMap<String, Boolean>()
        zipFile.entries().asIterator().forEachRemaining {
            if (it.name.endsWith(".class")) {
                map[it.name] = true
            }
        }

        cacheFile.createFile()
        cacheFile.writeText(JsonReader.toJson(map))
        VersionManifestList.addToClassMap(id, map, details, download.side)
    }

}

@Serializable
class LatestVersionsEntry(
    @SerialName("old_alpha")
    val oldAlpha: String,
    @SerialName("classic_server")
    val classicServer: String,
    @SerialName("alpha_server")
    val alphaServer: String,
    @SerialName("old_beta")
    val oldBeta: String,
    val snapshot: String,
    val release: String,
    val pending: String,
)

@Serializable
class VersionDetails(
    val id: String,
    val downloads: VersionDownloads,
    val type: String? = null,
    @Serializable(JsonReader.DateSerializer::class)
    val releaseTime: Long,
) {
    @Transient
    var isFromCache = false
    var majorVersion: String = ""
    var minorVersion: String = ""
    var bugfixVersion: String? = null
    var useJustMajorAsHeader = false

}

@Serializable
class VersionDownloads(
    val client: ClientVersionDownload? = null,
    val server: ServerVersionDownload? = null,
)

@Serializable
abstract class VersionDownload(
    open val sha1: String,
    open val size: Long,
    @Serializable(JsonReader.URLSerializer::class)
    open val url: URL,
) {
    abstract val side: String;
}

@Serializable
class ServerVersionDownload: VersionDownload {
    constructor(
        sha1: String,
        size: Long,
        url: URL,
    ) : super(sha1, size, url)

    override val side = "server"
}

@Serializable
class ClientVersionDownload: VersionDownload {
    constructor(
        sha1: String,
        size: Long,
        url: URL,
    ) : super(sha1, size, url)

    override val side = "client"
}
