package net.glasslauncher.repo

import java.text.SimpleDateFormat
import java.util.*

fun Double.toTime(): String {
    val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    val date = Date(toLong() * 1000)
    return "${formatter.format(date)} UTC"
}