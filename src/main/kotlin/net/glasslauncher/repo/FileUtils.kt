package net.glasslauncher.repo

import java.io.File
import java.nio.file.Path
import kotlin.io.path.readText

inline fun <reified T> Path.readJson(): T {
    return JsonReader.fromJson<T>(readText())
}

fun File.cd(vararg child: String): File {
    return File(this, child.joinToString(File.separator))
}