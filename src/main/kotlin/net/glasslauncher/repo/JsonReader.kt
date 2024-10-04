package net.glasslauncher.repo

import net.glasslauncher.repo.data.LegacyBoolean
import net.glasslauncher.repo.data.ModReference
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import java.net.URL
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class JsonReader {
    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        val INSTANCE = Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
            allowComments = true
        }

        inline fun <reified T> fromJson(value: String): T {
            return INSTANCE.decodeFromString(value)
        }

        inline fun <reified T> toJson(obj: T): String {
            return INSTANCE.encodeToString(obj)
        }
    }

    object ModReferenceSerializer : KSerializer<ModReference> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ModReference", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): ModReference {
            val parts = decoder.decodeString().split("|")
            return ModReference(if (parts.size > 1) parts[1] else parts[0], parts[0])
        }

        override fun serialize(encoder: Encoder, value: ModReference) {
            encoder.encodeString("${value.url}|${value.name}")
        }
    }

    object LegacyBooleanSerializer : KSerializer<LegacyBoolean> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LegacyBoolean", PrimitiveKind.BOOLEAN)

        override fun deserialize(decoder: Decoder): LegacyBoolean {
            var boolean: Boolean
            try {
                boolean = decoder.decodeBoolean()
            } catch(_: SerializationException) {
                boolean = true // The python website saved this as "on" for some fucking reason
            }
            return LegacyBoolean(boolean)
        }

        override fun serialize(encoder: Encoder, value: LegacyBoolean) {
            encoder.encodeBoolean(value.value)
        }
    }

    object URLSerializer : KSerializer<URL> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("URL", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): URL {
            return URL(decoder.decodeString())
        }

        override fun serialize(encoder: Encoder, value: URL) {
            encoder.encodeString(value.toString())
        }
    }

    object DateSerializer : KSerializer<Long> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Date", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): Long {
            return Instant.parse(decoder.decodeString()).epochSecond
        }

        override fun serialize(encoder: Encoder, value: Long) {
            encoder.encodeString(DateTimeFormatter.ISO_DATE_TIME.format(ZonedDateTime.ofInstant(Instant.ofEpochSecond(value), ZoneOffset.UTC)))
        }
    }
}