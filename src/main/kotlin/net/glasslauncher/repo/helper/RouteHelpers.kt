package net.glasslauncher.repo.helper

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.util.pipeline.*

fun PipelineContext<*, ApplicationCall>.queryParameters(): Parameters {
    return call.request.queryParameters
}

inline fun <reified T> Parameters.getOrDefault(key: String, default: T): T {
    val returnParam = get(key)
    return if (returnParam == null || returnParam !is T) default else returnParam
}