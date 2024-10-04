package net.glasslauncher.repo.data

import net.glasslauncher.repo.JsonReader
import kotlinx.serialization.Serializable

@Serializable(with = JsonReader.ModReferenceSerializer::class)
class ModReference(
    val name: String,
    val url: String,
)