package com.github.kr328.clash.service

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.github.kr328.clash.common.compat.startForegroundCompat
import com.github.kr328.clash.common.log.Log
import com.github.kr328.clash.service.clash.module.StaticNotificationModule
import com.github.kr328.clash.service.store.ServiceStore
import com.github.kr328.clash.service.util.OlcProfile
import com.github.kr328.clash.service.util.importedDir
import go.Seq
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import mobile.LogWriter
import mobile.Mobile
import mobile.SocketProtector
import java.security.SecureRandom

/** Runs olcRTC outside Mihomo's Go/JNI process and exposes its loopback SOCKS endpoint. */
class OlcTransportService : Service() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val runtimeLock = Any()
    private var startJob: Job? = null
    @Volatile private var runtime: mobile.Runtime? = null
    @Volatile private var startedProfileId: String? = null
    private val connectivityManager by lazy {
        getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    override fun onCreate() {
        super.onCreate()
        StaticNotificationModule.createNotificationChannel(this)
        val notification = NotificationCompat.Builder(this, StaticNotificationModule.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_logo_service)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setContentTitle(getString(R.string.olc_transport_starting))
            .build()
        startForegroundCompat(NOTIFICATION_ID, notification)
        Seq.setContext(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopTransport()
            stopSelf()
            return START_NOT_STICKY
        }
        val profileId = ServiceStore(this).activeProfile?.toString()
            ?: return START_NOT_STICKY.also { stopSelf() }
        synchronized(runtimeLock) {
            // startForegroundService may deliver the same request more than once. JNI waitReady()
            // is blocking and cannot be cancelled with Job.cancel(), so starting another Runtime
            // would race for the same loopback SOCKS port and tear down the healthy session.
            if (startedProfileId == profileId && (startJob?.isActive == true || runtime != null)) {
                return START_REDELIVER_INTENT
            }
            startedProfileId = profileId
            startJob = scope.launch { startSelectedProfile(profileId) }
        }
        return START_REDELIVER_INTENT
    }

    private fun startSelectedProfile(profileId: String) {
        val profile = importedDir.resolve(profileId)
            .takeIf(OlcProfile::isOlc)
            ?: return stopSelf()
        val endpoint = runCatching { OlcProfile.read(profile).endpoints.first() }
            .getOrElse {
                Log.e("Invalid active olcWave profile", it)
                return stopSelf()
            }

        stopTransport()
        val upstream = findUpstreamNetwork()
        if (upstream == null) {
            Log.e("olcRTC transport failed: no non-VPN upstream network")
            return stopSelf()
        }
        if (!connectivityManager.bindProcessToNetwork(upstream)) {
            Log.e("olcRTC transport failed: could not bind transport process to upstream network")
            return stopSelf()
        }

        val next = Mobile.new_()
        synchronized(runtimeLock) {
            runtime = next
            startedProfileId = profileId
        }
        next.setProtector(object : SocketProtector {
            // OlcLash's own UID is excluded from the OLC-mode VPN builder.
            override fun protect(fd: Long): Boolean = true
        })
        next.setLogWriter(object : LogWriter {
            override fun writeLog(msg: String) {
                msg.lineSequence().filter { it.isNotBlank() }.forEach { Log.w("olcRTC: $it") }
            }
        })
        next.setProvider(endpoint.provider)
        next.setTransport(endpoint.transport)
        next.setRoom(endpoint.room)
        next.setKey(endpoint.key)
        next.setDeviceID(endpoint.clientId ?: persistentDeviceId())
        next.setDNS("1.1.1.1:53")
        next.setSocksListenHost("127.0.0.1")
        next.setSocksPort(OlcProfile.SOCKS_PORT.toLong())
        next.setSocksCredentials("", "")
        next.setVP8Options(endpoint.vp8Fps.toLong(), endpoint.vp8Batch.toLong())

        try {
            next.start()
            next.waitReady(READY_TIMEOUT_MS)
            Log.i("olcRTC ready on 127.0.0.1:${OlcProfile.SOCKS_PORT}")
            val notification = NotificationCompat.Builder(this, StaticNotificationModule.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_logo_service)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setShowWhen(false)
                .setContentTitle(getString(R.string.running))
                .build()
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
                .notify(NOTIFICATION_ID, notification)
        } catch (e: Exception) {
            Log.e("olcRTC transport failed", e)
            // A stale waitReady() may finish after a newer Runtime has already replaced it.
            // It must never stop the replacement or the service itself.
            if (stopTransport(next)) stopSelf()
        }
    }

    private fun persistentDeviceId(): String {
        val preferences = getSharedPreferences("olcrtc_identity", MODE_PRIVATE)
        preferences.getString("device_id", null)?.takeIf { it.isNotBlank() }?.let { stored ->
            if (stored.startsWith("install-")) return stored
            return "install-$stored".also {
                preferences.edit().putString("device_id", it).commit()
            }
        }
        val bytes = ByteArray(16).also(SecureRandom()::nextBytes)
        return ("install-" + bytes.joinToString("") { "%02x".format(it) }).also {
            preferences.edit().putString("device_id", it).commit()
        }
    }

    private fun stopTransport(expected: mobile.Runtime? = null): Boolean {
        val active = synchronized(runtimeLock) {
            if (expected != null && runtime !== expected) return false
            runtime.also { runtime = null }
        }
        active?.let { current -> runCatching { current.stop(STOP_TIMEOUT_MS) } }
        synchronized(runtimeLock) {
            if (runtime == null) {
                startedProfileId = null
                runCatching { connectivityManager.bindProcessToNetwork(null) }
            }
        }
        return true
    }

    private fun findUpstreamNetwork(): Network? {
        val active = connectivityManager.activeNetwork
        return connectivityManager.allNetworks
            .mapNotNull { network ->
                val capabilities = connectivityManager.getNetworkCapabilities(network)
                    ?: return@mapNotNull null
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) ||
                    !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                ) return@mapNotNull null
                val score = (if (network == active) 2 else 0) +
                    (if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) 1 else 0)
                network to score
            }
            .maxByOrNull { it.second }
            ?.first
    }

    override fun onDestroy() {
        startJob?.cancel()
        stopTransport()
        scope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_START = "com.github.kr328.clash.action.START_OLC_TRANSPORT"
        const val ACTION_STOP = "com.github.kr328.clash.action.STOP_OLC_TRANSPORT"
        private const val NOTIFICATION_ID = 2227
        private const val READY_TIMEOUT_MS = 60_000L
        private const val STOP_TIMEOUT_MS = 5_000L
    }
}
