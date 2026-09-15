package com.github.kr328.clash.service.util

import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OlcProfileTest {
    @Test
    fun convertsDownloadedBodyToMihomoFrontend() {
        val dir = Files.createTempDirectory("olc-profile").toFile()
        try {
            dir.resolve("config.yaml").writeText(
                "olcrtc://telemost?vp8channel<vp8-batch=64&vp8-fps=30>@room#${"a".repeat(64)}${'$'}"
            )
            assertTrue(OlcProfile.convertDownloadedBody(dir))
            assertTrue(OlcProfile.isOlc(dir))
            assertTrue(dir.resolve("config.yaml").readText().contains("MATCH,OlcLash"))
            assertEquals("room", OlcProfile.read(dir).endpoints.single().room)
        } finally {
            dir.deleteRecursively()
        }
    }
}
