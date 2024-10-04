package net.glasslauncher.repo.data.user

import glasslauncher.net.repo.HttpClient
import glasslauncher.net.repo.JsonReader
import glasslauncher.net.repo.data.SiteConfig
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
class User {
    val avatar: String? = ""
    var email: String = ""
    val username: String = ""
    val id: String = ""
    val discriminator: String = ""
    var site: String? = ""

    fun getDecryptedEmail(): String {
        return SiteConfig.decrypt(email)
    }

    suspend fun setupAvatar() {
        if (avatar == null) {
            return
        }
        val extension = if (avatar.startsWith("a_")) ".gif" else ".png"

        val metaFile = File("repo/avatars/${id}.meta")
        val avatarFile = File("repo/avatars/${id}${extension}")

        if (metaFile.exists()) {
            File("repo/avatars/${id}${metaFile.readText()}").delete()
            metaFile.delete()
        }

        val response = HttpClient.client.get("https://cdn.discordapp.com/avatars/${id}/${avatar}${extension}")

        metaFile.writeBytes(extension.toByteArray())
        avatarFile.writeBytes(response.readBytes())
    }

    fun save() {
        val userFile = File("repo/users/${id}.json")
        userFile.delete()
        userFile.writeText(JsonReader.toJson(this))
    }
}