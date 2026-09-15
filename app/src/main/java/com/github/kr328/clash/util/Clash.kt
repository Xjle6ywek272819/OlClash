package com.github.kr328.clash.util

import android.content.Context
import android.content.Intent
import android.net.VpnService
import com.github.kr328.clash.common.compat.startForegroundServiceCompat
import com.github.kr328.clash.common.constants.Intents
import com.github.kr328.clash.common.util.intent
import com.github.kr328.clash.design.store.UiStore
import com.github.kr328.clash.service.ClashService
import com.github.kr328.clash.service.OlcTransportService
import com.github.kr328.clash.service.TunService
import com.github.kr328.clash.service.store.ServiceStore
import com.github.kr328.clash.service.util.OlcProfile
import com.github.kr328.clash.service.util.importedDir
import com.github.kr328.clash.service.util.sendBroadcastSelf

fun Context.startClashService(): Intent? {
    val startTun = UiStore(this).enableVpn
    val olcMode = ServiceStore(this).activeProfile
        ?.let { OlcProfile.isOlc(importedDir.resolve(it.toString())) }
        ?: false

    if (startTun) {
        val vpnRequest = VpnService.prepare(this)
        if (vpnRequest != null)
            return vpnRequest

        if (olcMode) {
            startForegroundServiceCompat(
                OlcTransportService::class.intent.setAction(OlcTransportService.ACTION_START)
            )
        } else {
            stopService(OlcTransportService::class.intent)
        }
        startForegroundServiceCompat(TunService::class.intent)
    } else {
        startForegroundServiceCompat(ClashService::class.intent)
    }

    return null
}

fun Context.stopClashService() {
    sendBroadcastSelf(Intent(Intents.ACTION_CLASH_REQUEST_STOP))
    stopService(OlcTransportService::class.intent)
}
