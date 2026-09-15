package com.github.kr328.clash.common.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OlcSubscriptionParserTest {
    @Test
    fun parsesOlcWaveResponse() {
        val key = "a".repeat(64)
        val result = OlcSubscriptionParser.parse(
            """
            #name: OLCWave
            #refresh: 1h

            olcrtc://telemost?vp8channel<vp8-batch=64&vp8-fps=30>@room-01#$key${'$'}
            ##name:
            """.trimIndent(),
        )

        assertEquals("OLCWave", result.name)
        assertEquals(3_600_000L, result.refreshMillis)
        assertEquals(1, result.endpoints.size)
        assertEquals("telemost", result.endpoints.single().provider)
        assertEquals("vp8channel", result.endpoints.single().transport)
        assertEquals(64, result.endpoints.single().vp8Batch)
        assertEquals(30, result.endpoints.single().vp8Fps)
    }

    @Test
    fun detectsOnlyOlcRtcBodies() {
        assertTrue(OlcSubscriptionParser.isOlcSubscription("\uFEFFolcrtc://telemost?vp8channel@r#${"b".repeat(64)}"))
        assertFalse(OlcSubscriptionParser.isOlcSubscription("proxies: []"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsShortEncryptionKey() {
        OlcSubscriptionParser.parse("olcrtc://telemost?vp8channel@room#abcd")
    }
}
