package net.glasslauncher.repo.data.mod

import glasslauncher.net.repo.GlassLogger
import glasslauncher.net.repo.Repo
import glasslauncher.net.repo.readJson
import io.ktor.http.*
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.PathMatcher
import java.util.stream.Collectors
import java.util.stream.Stream
import kotlin.io.path.isDirectory
import kotlin.math.max
import kotlin.math.min

class ModRepo {
    val cache = HashMap<String, Mod>()
    val cacheArray = ArrayList<Mod>()

    fun reloadRepo() {
        cache.clear()
        cacheArray.clear()
        val matcher: PathMatcher = FileSystems.getDefault().getPathMatcher("glob:**/modpack.json")

        val mods: List<Path> = Files.walk(Repo.modRepoPath.absoluteFile.toPath(), 2).filter { !it.isDirectory() && matcher.matches(it) }.collect(Collectors.toList())

        GlassLogger.INSTANCE.info("Found ${mods.size} mods.")

        mods.forEach {
            val mod = it.readJson<Mod>()
            cache[mod.id] = mod
            cacheArray.add(mod)
        }
    }

    fun reloadMod(id: String): Mod {
        return Path.of(Repo.modRepoPath.absolutePath, "${id}/modpack.json").readJson()
    }

    fun reloadVersion(id: String, version: String): Mod {
        return Path.of(Repo.modRepoPath.absolutePath, "${id}/version/${version}.json").readJson()
    }

    fun filterMods(parameters: Parameters): List<Mod> {
        var modStream = cacheArray.stream()

        val relevancy = HashMap<Mod, Int>()

        val stringParam = parameters["searchText"]
        if (stringParam?.isNotEmpty() == true) {
            modStream = modStream.filter {
                var matches = false
                if (stringParam.contains(it.id, true)) {
                    relevancy[it] = 10 // If id matches, then the user is *probably* looking for this.
                    matches = true
                }
                if (stringParam.contains(it.name, true)) {
                    relevancy[it] = (relevancy[it] ?: 0) + 5 // Make name matches fairly important.
                    matches = true
                }
                val matchStream = stringParam.split(" ").filter { splitParam -> it.description.contains(splitParam, true) }
                if (matchStream.isNotEmpty()) {
                    relevancy[it] = (relevancy[it] ?: 0) + matchStream.size // Vulnerable to keyword stuffing, but it should be obvious and easy to moderate.
                    matches = true
                }
                return@filter matches
            }
        }

        modStream = matchStringLists("types", parameters, modStream)
        modStream = matchStringLists("categories", parameters, modStream)
        modStream = matchStringLists("mcVersions", parameters, modStream)
        modStream = matchStringLists("tags", parameters, modStream)

        var mods = modStream.toList()
        val amount = (parameters["amount"]?.toIntOrNull() ?: 20).coerceIn(min(10, mods.size - 1) .. min(50, mods.size - 1))
        val page = parameters["page"]?.toIntOrNull() ?: 0
        val startIndex = amount * page

        mods = mods.slice(startIndex .. startIndex + amount)

        when(parameters["searchMode"]) {
            "relevancy" -> mods.sortByDescending { relevancy[it] }
            "created" -> mods.sortByDescending { it.timestamp.toInt() }
            "createdI" -> mods.sortByDescending { it.timestamp.toInt() }
            "alphabetical" -> mods.sortByDescending { it.name }
            "alphabeticalI" -> mods.sortBy { it.name }
            "updatedI" -> mods.sortBy { it.getLatestVersion()?.timestamp ?: 0.0 }
            else -> mods.sortByDescending { it.getLatestVersion()?.timestamp ?: 0.0 }
        }

        return mods
    }

    private fun matchStringLists(id: String, parameters: Parameters, modStream: Stream<Mod>): Stream<Mod> {
        val arrayParam = parameters.getAll(id)
        if (arrayParam?.isNotEmpty() == true) {
            val typesAll = parameters["${id}All"] == "true"
            return if (typesAll) {
                modStream.filter {
                    it.getField<List<String>>(id).containsAll(arrayParam)
                }
            } else {
                modStream.filter {
                    it.getField<List<String>>(id).intersect(arrayParam).isNotEmpty()
                }
            }
        }
        return modStream
    }
}