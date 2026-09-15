package com.github.kr328.clash.common.util

/** Parsed olcWave subscription and its first usable olcRTC endpoint. */
data class OlcSubscription(
    val name: String,
    val refreshMillis: Long?,
    val endpoints: List<OlcEndpoint>,
)

data class OlcEndpoint(
    val provider: String,
    val transport: String,
    val room: String,
    val key: String,
    val clientId: String?,
    val displayName: String?,
    val vp8Batch: Int,
    val vp8Fps: Int,
)

object OlcSubscriptionParser {
    private const val PREFIX = "olcrtc://"
    private val keyPattern = Regex("^[0-9a-fA-F]{64}$")

    fun isOlcSubscription(text: String): Boolean =
        text.lineSequence().any { it.trimStart('\uFEFF', ' ', '\t').startsWith(PREFIX, true) }

    fun parse(text: String): OlcSubscription {
        val metadata = linkedMapOf<String, String>()
        val endpoints = mutableListOf<OlcEndpoint>()

        text.lineSequence().forEach { rawLine ->
            val line = rawLine.trimStart('\uFEFF').trim()
            when {
                line.startsWith(PREFIX, true) -> endpoints += parseEndpoint(line)
                line.startsWith("#") && !line.startsWith("##") -> {
                    val separator = line.indexOf(':')
                    if (separator > 1) {
                        metadata[line.substring(1, separator).trim().lowercase()] =
                            line.substring(separator + 1).trim()
                    }
                }
            }
        }

        require(endpoints.isNotEmpty()) { "olcWave subscription contains no olcRTC endpoints" }
        return OlcSubscription(
            name = metadata["name"].orEmpty().ifBlank { "OLCWave" },
            refreshMillis = parseDurationMillis(metadata["refresh"]),
            endpoints = endpoints,
        )
    }

    private fun parseEndpoint(line: String): OlcEndpoint {
        val body = line.substring(PREFIX.length)
        val question = body.indexOf('?')
        val at = body.indexOf('@', startIndex = question + 1)
        val hash = body.indexOf('#', startIndex = at + 1)
        require(question > 0 && at > question + 1 && hash > at + 1) { "Invalid olcRTC endpoint" }

        val provider = body.substring(0, question).trim().lowercase()
        val transportToken = body.substring(question + 1, at).trim()
        val room = body.substring(at + 1, hash).trim()
        val secretAndName = body.substring(hash + 1)
        val dollar = secretAndName.indexOf('$')
        val secret = (if (dollar >= 0) secretAndName.substring(0, dollar) else secretAndName).trim()
        val displayName = if (dollar >= 0) secretAndName.substring(dollar + 1).trim().ifBlank { null } else null
        val percent = secret.indexOf('%')
        val key = (if (percent >= 0) secret.substring(0, percent) else secret).trim()
        val clientId = if (percent >= 0) secret.substring(percent + 1).trim().ifBlank { null } else null
        require(keyPattern.matches(key)) { "Invalid olcRTC key" }
        require(room.isNotBlank()) { "Invalid olcRTC room" }

        val optionsStart = transportToken.indexOf('<')
        val optionsEnd = transportToken.lastIndexOf('>')
        val transport = (if (optionsStart >= 0) transportToken.substring(0, optionsStart) else transportToken)
            .trim().lowercase()
        val options = if (optionsStart >= 0 && optionsEnd > optionsStart) {
            transportToken.substring(optionsStart + 1, optionsEnd)
                .split('&')
                .mapNotNull { token ->
                    val separator = token.indexOf('=')
                    if (separator <= 0) null
                    else token.substring(0, separator).trim().lowercase() to
                        token.substring(separator + 1).trim().toIntOrNull()
                }
                .filter { it.second != null }
                .associate { it.first to it.second!! }
        } else {
            emptyMap()
        }

        return OlcEndpoint(
            provider = provider,
            transport = transport,
            room = room,
            key = key.lowercase(),
            clientId = clientId,
            displayName = displayName,
            vp8Batch = options["vp8-batch"]?.coerceIn(1, 1024) ?: 64,
            vp8Fps = options["vp8-fps"]?.coerceIn(1, 120) ?: 30,
        )
    }

    private fun parseDurationMillis(raw: String?): Long? {
        val value = raw?.trim()?.lowercase() ?: return null
        val match = Regex("^(\\d+)([smhd])$").matchEntire(value) ?: return null
        val amount = match.groupValues[1].toLongOrNull() ?: return null
        val multiplier = when (match.groupValues[2]) {
            "s" -> 1_000L
            "m" -> 60_000L
            "h" -> 3_600_000L
            "d" -> 86_400_000L
            else -> return null
        }
        return amount.times(multiplier).takeIf { it > 0L }
    }
}
