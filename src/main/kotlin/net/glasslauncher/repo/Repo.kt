package net.glasslauncher.repo

import glasslauncher.net.repo.data.mod.ModRepo
import java.io.File

class Repo {

    companion object RepoConsts {
        val modRepoPath: File = File("repo/mods")

        val modRepo: ModRepo = ModRepo()
    }
}