package net.glasslauncher.repo

import net.glasslauncher.repo.data.mod.ModRepo
import java.io.File

class Repo {

    companion object RepoConsts {
        val modRepoPath = File("repo/mods")
        val avatarsPath = File("repo/avatars")
        val cdnPath = File("repo/cdn")
        val classMapsPath = File("repo/classMaps")
        val mcCachePath = File("repo/minecraftCache")
        val mcMetaPath = File("repo/minecraftMeta")
        val usersPath = File("users")

        val modRepo: ModRepo = ModRepo()

        init {
            modRepoPath.mkdirs()
            avatarsPath.mkdirs()
            cdnPath.mkdirs()
            classMapsPath.mkdirs()
            mcCachePath.mkdirs()
            mcMetaPath.mkdirs()
            usersPath.mkdirs()
        }
    }
}