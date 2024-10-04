package net.glasslauncher.repo.data

import glasslauncher.net.repo.JsonReader.LegacyBooleanSerializer
import kotlinx.serialization.Serializable

@Serializable(with = LegacyBooleanSerializer::class)
class LegacyBoolean(var value: Boolean) {
    companion object {
        fun createTrue(): LegacyBoolean {
            return LegacyBoolean(true)
        }
        fun createFalse(): LegacyBoolean {
            return LegacyBoolean(false)
        }
    }
}