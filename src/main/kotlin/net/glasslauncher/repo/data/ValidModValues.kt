package net.glasslauncher.repo.data

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
class ValidModValues {
    companion object {
        val types = listOf(
            "ModLoader",
            "ModLoaderMP",
            "Forge",
            "hMod",
            "Texturepack",
            "Base Edit",
            "StationAPI",
            "Ornithe",
            "Legacy Fabric",
            "Fabric",
            "Nil Loader",
            "Bukkit Mod",
            "Bukkit Plugin",
            "Project Poseidon Mod",
            "Project Poseidon Plugin",
        )

        @SerialName("version_types")
        val versionTypes = listOf(
            "Mod Folder",
            "Base Edit",
            "Core Mod",
        )

        val categories = listOf(
            "Singleplayer",
            "Multiplayer",
            "Block",
            "Item",
            "Mob",
            "Dimension",
            "Overhaul",
            "Mechanic",
            "Library",
            "Bugfix",
            "Admin Tool",
        )
    }
}