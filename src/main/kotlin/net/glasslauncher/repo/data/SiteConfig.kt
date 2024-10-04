package net.glasslauncher.repo.data

import com.macasaet.fernet.Key
import com.macasaet.fernet.StringValidator
import com.macasaet.fernet.Token
import glasslauncher.net.repo.data.minecraft.VersionDetails
import glasslauncher.net.repo.readJson
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.nio.file.Path

@Serializable
class SiteConfig {
    @SerialName("dc_id")
    val discordID: String = ""
    @SerialName("dc_scrt")
    val discordSecret: String = ""
    @SerialName("dc_mwhurl")
    val discordWebhookURL: String = ""

    /*
    Getter and not a const so dev testing isn't a nightmare.
     */
    fun getDiscordOauthURL(redirectURL: String?): String {
        return "https://discordapp.com/api/oauth2/authorize?client_id=%s&redirect_uri=%s/login&response_type=code&scope=identify email".format(discordID, redirectURL ?: "https://glass-repo.net/login")
    }

    @SerialName("fernet")
    var fernetKey: String = ""

    @Transient
    var fernet: Key = Key(fernetKey)

    @Transient
    var fernetValidator = Validator()

    @SerialName("mail_pass")
    var mailPassword: String = ""

    companion object {
        val config = Path.of("config.json5").readJson<SiteConfig>()

        val siteVersion: String = SiteConfig.javaClass.getResourceAsStream("/net/glasslauncher/repo/version").readAllBytes().decodeToString()

        val minecraftVersions: HashMap<String, VersionDetails> = HashMap()

        fun decrypt(thing: String): String {
            return Token.fromString(thing).validateAndDecrypt(config.fernet, config.fernetValidator)
        }

        fun encrypt(thing: String): String {
            return Token.generate(Key.generateKey(), thing).serialise()
        }
    }

    class Validator : StringValidator
}