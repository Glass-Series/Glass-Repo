package net.glasslauncher.repo.data.mod

import net.glasslauncher.repo.data.ModReference
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
class ModSkeleton {
    var authors: List<String>? = null
    var categories: List<String>? = null
    var dependencies: List<ModReference>? = null
    var description: String? = null
    @SerialName("edited_classes")
    var editedClasses: List<String>? = null
    @SerialName("incompatible_mods")
    var incompatibleMods: List<ModReference>? = null
    var id: String? = null
    var name: String? = null
    @SerialName("optional_dependencies")
    var optionalDependencies: List<ModReference>? = null
    @SerialName("short_description")
    var shortDescription: String? = null
    var tags: List<String>? = null
    var types: List<String>? = null
    @SerialName("minecraft_versions")
    var minecraftVersions: List<String>? = null
}