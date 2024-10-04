package net.glasslauncher.repo.data.user

import glasslauncher.net.repo.readJson
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.PathMatcher

class Users {
    companion object {
        val users = HashMap<String, User>()

        init {
            val matcher: PathMatcher = FileSystems.getDefault().getPathMatcher("glob:**/*.json")

            for (path: Path in Files.walk(Path.of("users"), 1).filter(matcher::matches)) {
                val user = path.readJson<User>()
                users[user.id] = user
            }
        }
    }
}