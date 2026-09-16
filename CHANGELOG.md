# Changelog

## [Unreleased]

- Improve OLCWave reconnection, traffic accounting, and latency presentation.
- Expand Android device and network-transition testing.
- Review ClashFest updates through dedicated pull requests.

## [1.1.0-alpha.2] - 2026-09-16

### Added

- Mihomo/Remnawave and OLCWave subscription recognition.
- Pinned olcRTC runtime with provenance and checksum verification.
- TUN → Mihomo gVisor → loopback SOCKS5 → olcRTC transport.
- Server-side OLCWave routing while preserving Mihomo routing and per-app settings.
- Signed ARM64 release workflow with APK, native runtime, gomobile symbol, and SHA-256 checks.

### Fixed

- Protected olcRTC carrier sockets from the Android VPN loop.
- Preserved required gomobile callback classes in release builds.
- Disabled shrinking for the first public alpha until safe rules are verified.

[Unreleased]: https://github.com/Xjle6ywek272819/OlClash/compare/v1.1.0-alpha.2...HEAD
[1.1.0-alpha.2]: https://github.com/Xjle6ywek272819/OlClash/releases/tag/v1.1.0-alpha.2
