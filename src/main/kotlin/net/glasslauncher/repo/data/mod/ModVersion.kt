package net.glasslauncher.repo.data.mod

import net.glasslauncher.repo.Repo
import net.glasslauncher.repo.cd
import net.glasslauncher.repo.data.LegacyBoolean
import net.glasslauncher.repo.renderToHtml
import net.glasslauncher.repo.toTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Transient
import java.io.File

@kotlinx.serialization.Serializable
class ModVersion {
    @SerialName("data_version")
    var dataVersion = 1
    var version: String = ""
    var description: String = ""
    var timestamp: Double = 0.0
    var type: String = ""
    var size: Long = 0
    @SerialName("download_count")
    var downloadCount: Long = 0
    @SerialName("is_client_server")
    var isClientServer: LegacyBoolean = LegacyBoolean(false)
    @SerialName("has_client")
    var hasClient: LegacyBoolean = LegacyBoolean(false)
    @SerialName("has_server")
    var hasServer: LegacyBoolean = LegacyBoolean(false)
    @SerialName("has_bukkit")
    var hasBukkit: LegacyBoolean = LegacyBoolean(false)
    var filetype: String = ""

    @SerialName("minecraft_versions")
    var minecraftVersions: List<String> = listOf()

    @Transient
    lateinit var parentMod: Mod
    @Transient
    var time = timestamp.toTime()
    @Transient
    var renderedDescription = description.renderToHtml()

    fun getFile(type: String, side: String): File {
        return Repo.modRepoPath.cd(parentMod.id, "versions", "${parentMod.id}_${version}~${side}.${filetype}")
    }
}