package net.glasslauncher.repo.data.mod

import net.glasslauncher.repo.*
import net.glasslauncher.repo.data.ModReference
import net.glasslauncher.repo.data.user.User
import net.glasslauncher.repo.data.user.Users
import kotlinx.serialization.SerialName
import kotlinx.serialization.Transient
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.PathMatcher
import java.time.Instant
import java.util.stream.Collectors
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties

@kotlinx.serialization.Serializable
class Mod {
    val authors: List<String> = listOf()
    val categories: List<String> = listOf()
    val dependencies: List<ModReference> = listOf()
    val description: String = ""
    @SerialName("edited_classes")
    val editedClasses: List<String> = listOf()
    @SerialName("incompatible_mods")
    val incompatibleMods: List<ModReference> = listOf()
    val id: String = ""
    val name: String = ""
    @SerialName("optional_dependencies")
    val optionalDependencies: List<ModReference> = listOf()
    @SerialName("short_description")
    val shortDescription: String = ""
    val tags: List<String> = listOf()
    val types: List<String> = listOf()
    @SerialName("minecraft_versions")
    val minecraftVersions: List<String> = listOf()

    val timestamp: Double = Instant.EPOCH.epochSecond.toDouble()

    @Transient
    var time: String = timestamp.toTime()
    @Transient
    lateinit var versions: List<ModVersion>
    @Transient
    lateinit var versionsMap: HashMap<String, ModVersion>
    @Transient
    var downloads = 0L
    @Transient
    var renderedDescription = description.renderToHtml()
    @Transient
    var authorObjects = authors.map { Users.users[it] }.toList()

    companion object {
        val fieldMap = HashMap<String, KProperty1<out Mod, *>>()
        init {
            Mod::class.declaredMemberProperties.forEach {
                fieldMap[it.name] = it
            }
        }
    }

    init {
        reloadVersions()
    }

    private fun reloadVersions() {
        downloads = 0
        versionsMap = HashMap()
        val matcher: PathMatcher = FileSystems.getDefault().getPathMatcher("glob:**/*.json")

        var versions: List<Path>? = null
        val path = Repo.modRepoPath.cd(id, "versions").toPath()
        if (path.exists()) {
            versions = Files.walk(path, 1).filter { !it.isDirectory() && matcher.matches(it) }.collect(Collectors.toList())
        }

        this.versions = if (versions != null) ArrayList(versions.map {
            val version = it.readJson<ModVersion>()
            version.parentMod = this
            downloads += version.downloadCount
            versionsMap[version.version] = version
            if (version.minecraftVersions.isEmpty()) {
                version.minecraftVersions = minecraftVersions
            }
            version
        }).sortedBy { it.timestamp }.reversed() else ArrayList()
    }

    fun getLatestVersion(): ModVersion? {
        return if (versions.isNotEmpty()) versions[0] else null
    }

    fun getMainAuthor(): User {
        return authorObjects[0]!!
    }

    fun isAuthor(userId: String?): Boolean {
        if (userId == null) {
            return false;
        }

        return userId in authors
    }

    // Lazy hack moment
    fun <T> getField(field: String): T {
        @Suppress("UNCHECKED_CAST") // Burn
        return fieldMap[field]!!.getter.call(this) as T
    }
}