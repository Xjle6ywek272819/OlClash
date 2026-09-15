package com.github.kr328.clash.service.util

import com.github.kr328.clash.common.util.OlcSubscription
import com.github.kr328.clash.common.util.OlcSubscriptionParser
import java.io.File

object OlcProfile {
    const val SUBSCRIPTION_FILE = "olc-subscription.txt"
    const val SOCKS_PORT = 17777

    fun isOlc(profileDir: File): Boolean = profileDir.resolve(SUBSCRIPTION_FILE).isFile

    fun read(profileDir: File): OlcSubscription =
        OlcSubscriptionParser.parse(profileDir.resolve(SUBSCRIPTION_FILE).readText())

    /**
     * Recovers an olcWave body left in config.yaml after Mihomo correctly rejects it as YAML.
     * The replacement configuration is validated by the real Mihomo engine at the caller.
     */
    fun convertDownloadedBody(profileDir: File): Boolean {
        val config = profileDir.resolve("config.yaml")
        if (!config.isFile) return false
        val body = runCatching { config.readText() }.getOrNull() ?: return false
        if (!OlcSubscriptionParser.isOlcSubscription(body)) return false
        OlcSubscriptionParser.parse(body) // validate before persisting secrets
        profileDir.resolve(SUBSCRIPTION_FILE).writeText(body)
        config.writeText(frontendYaml())
        return true
    }

    fun frontendYaml(): String =
        """
        mode: rule
        log-level: info
        ipv6: true
        allow-lan: false
        dns:
          enable: true
          enhanced-mode: fake-ip
          fake-ip-range: 198.18.0.1/16
          nameserver:
            - 1.1.1.1
        proxies:
          - name: OLCWave
            type: socks5
            server: 127.0.0.1
            port: $SOCKS_PORT
            udp: false
        proxy-groups:
          - name: OlcLash
            type: select
            proxies:
              - OLCWave
        rules:
          - MATCH,OlcLash
        """.trimIndent() + "\n"
}
