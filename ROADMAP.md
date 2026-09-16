# OlcLash Roadmap

This is direction, not a delivery promise.

## Next

- Harden OLCWave reconnection and network-change handling.
- Make traffic counters and latency distinguish the local bridge from remote transport.
- Improve OLC-specific status and diagnostics.
- Test more Android versions, vendors, IPv6 paths, network transitions, and per-app combinations.
- Re-enable shrinking only after automated gomobile compatibility checks prove it safe.

## Maintenance

- Track ClashFest through reviewable upstream-sync PRs.
- Review pinned olcbox/olcRTC updates separately, including checksum, provenance, license, and functional tests.
- Preserve upstream Mihomo/Remnawave behavior unless the OlcLash transport requires a documented difference.
- Publish signed ARM64 pre-releases with checksums and changelog notes.

## Out of scope

- Automatically merging upstream changes.
- Operating subscription, VPN, relay, or telemetry services.
- Silently changing provider-side routing policy.
