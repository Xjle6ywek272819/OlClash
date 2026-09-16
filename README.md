<p align="center"><img src="design/branding/olclash-icon.svg" width="180" alt="OlcLash icon" /></p>
<h1 align="center">OlcLash</h1>

OlcLash is an experimental Android VPN client supporting Mihomo YAML subscriptions (including Remnawave) and OLCWave `olcrtc://` subscriptions in one application. It is based on [ClashFest](https://github.com/Nemu-x/ClashFest) and is not affiliated with its upstream projects or any VPN provider.

## Download

Download the current signed APK and checksum from [GitHub Releases](https://github.com/Xjle6ywek272819/OlClash/releases). Public builds currently support `arm64-v8a` only. CI/debug builds use a different certificate and must be removed before installing a release build. Package: `com.vibecode.olclash`.

## Profile behavior

| Profile | Data path | Routing | Per-app VPN |
| --- | --- | --- | --- |
| Mihomo / Remnawave | TUN → Mihomo → selected proxy | Subscription plus local overlay | Saved allow/block list |
| OLCWave | TUN → Mihomo gVisor → loopback SOCKS5 → olcRTC → server | Server-side; local adapter uses `MATCH` | All apps enter VPN; OlcLash is excluded to avoid a loop |

The selected stack applies to normal Mihomo profiles. OLCWave uses gVisor internally because Android `system` stack is unreliable for this bridge. Switching types does not overwrite saved Mihomo routing or per-app settings. See [Architecture](docs/ARCHITECTURE.md).

## Status

Both subscription families import and connect. OLCWave remains experimental; reconnection, counters, latency display, and OLC-specific UI need more testing.

## Build

Use JDK 21, a compatible Android SDK/NDK, and initialized submodules:

```bash
git clone --recursive https://github.com/Xjle6ywek272819/OlClash.git
cd OlClash
./gradlew assembleAlphaDebug
```

On Windows use `.\gradlew.bat assembleAlphaDebug`. The build fetches a pinned official olcbox APK, verifies its SHA-256, and extracts the ARM64 olcRTC runtime. See [provenance](docs/OLCRTC_PROVENANCE.md).

## Security, privacy, and contributing

The olcRTC SOCKS endpoint is loopback-only and carrier sockets run outside the VPN loop on a physical network. Never publish subscription data, credentials, room IDs, HWIDs, device IDs, or unredacted logs.

See [SECURITY.md](SECURITY.md), [PRIVACY_POLICY.md](PRIVACY_POLICY.md), [CONTRIBUTING.md](CONTRIBUTING.md), [CHANGELOG.md](CHANGELOG.md), and [ROADMAP.md](ROADMAP.md).

## License

OlcLash is GPL-3.0. Upstream and runtime notices are retained in [NOTICE](NOTICE) and [licenses/](licenses/). This software is provided without warranty.
