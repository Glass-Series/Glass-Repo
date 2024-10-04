package net.glasslauncher.repo.data

import arrow.core.Either
import glasslauncher.net.repo.GlassLogger
import glasslauncher.net.repo.HttpClient
import glasslauncher.net.repo.data.user.User
import glasslauncher.net.repo.data.user.Users
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.collections.set

data class DiscordAuth(
    val code: String,
    val redirectURI: String
) {

    suspend fun signIn(call: PipelineContext<Unit, ApplicationCall>): Either<String, User> {
        var response = HttpClient.client.submitForm(url = "https://discord.com/api/oauth2/token", formParameters = parameters {
            append("code", code)
            append("redirect_uri", "${redirectURI}/login")
            append("grant_type", "authorization_code")
            append("scope", "identify email")
        }) {
            basicAuth(SiteConfig.config.discordID, SiteConfig.config.discordSecret)
        }

        if (response.status.value != 200) {
            val body = response.body<DiscordErrorResponse>()
            GlassLogger.INSTANCE.error(body.message ?: body.error)
            return Either.Left(body.message ?: body.error ?: "unknown error")
        }

        val token = response.body<DiscordAccessToken>()

        if (!token.scope.contains("identify") || !token.scope.contains("email")) {
            return Either.Left("Invalid scope, don't mess with the auth URL.")
        }

        response = HttpClient.client.get("https://discord.com/api/oauth2/@me") {
            bearerAuth(token.accessToken)
        }

        val user = response.body<DiscordAccessResponse>().user

        user.email = SiteConfig.encrypt(user.email)
        user.setupAvatar()

        user.site = Users.users[user.id]?.site
        Users.users[user.id] = user

        return Either.Right(user)
    }
}

@Serializable
class DiscordAccessToken(@SerialName("access_token") val accessToken: String, val scope: String)

@Serializable
class DiscordAccessResponse(val user: User)

@Serializable
class DiscordErrorResponse(val message: String? = null, val error: String? = null)