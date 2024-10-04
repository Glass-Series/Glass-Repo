package net.glasslauncher.repo.data

import glasslauncher.net.repo.JsonReader
import kotlinx.serialization.Serializable

@Serializable(with = JsonReader.ModReferenceSerializer::class)
class ModReference(
    val name: String,
    val url: String,
)