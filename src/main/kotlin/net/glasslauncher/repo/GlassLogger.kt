package net.glasslauncher.repo

import org.slf4j.LoggerFactory

class GlassLogger {
    companion object {
        val INSTANCE = LoggerFactory.getLogger("GlassRepo")
    }
}