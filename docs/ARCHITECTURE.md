# OlcLash transport architecture

OlcLash selects its runtime path from the active imported profile. The original
subscription body is retained so profile type detection does not depend on the
generated frontend configuration.

## Mihomo and Remnawave profiles

```text
Android applications → Android VpnService / tun0 → Mihomo TUN stack
    → subscription rules and proxy groups → selected proxy server
```

The subscription remains the routing authority. OlcLash composes it with the
user layer and applies the saved per-app VPN policy. The stack selected by the
user or subscription is respected.

## OLCWave profiles

```text
Android applications → Android VpnService / tun0 → Mihomo gVisor adapter
    → 127.0.0.1:17777 → olcRTC carrier process → OLC server-side routing
```

The OLC subscription is stored in `olc-subscription.txt`. Mihomo receives a
minimal generated frontend configuration containing one loopback SOCKS5 proxy
and a final `MATCH` rule. Local user routing overlays are deliberately not
applied because routing is performed by the OLC server.

OLC mode forces a full IPv4 VPN route and ignores the stored application
allow/block-list without modifying it. The OlcLash package UID is excluded from
the Android VPN so the carrier cannot feed itself back into `tun0`.

The olcRTC runtime runs in `:olcrtc`, separate from Mihomo and `TunService` in
`:background`. Its process is bound to a selected non-VPN upstream network. The
SOCKS listener is loopback-only.

## Profile switching invariants

1. Selecting an OLCWave profile starts olcRTC and generates only the internal adapter configuration.
2. Selecting a Mihomo profile stops olcRTC and restores normal profile loading.
3. Switching modes never rewrites the saved per-app selection or Mihomo user overlay.
4. Carrier sockets must never be routed back through the VPN.
5. The OLC runtime binary and Java bindings must be updated as one compatible set and pinned by digest.

## Known limitations

- Only `arm64-v8a` is currently packaged for OLCWave.
- Latency and traffic presentation still need OLC-specific UI semantics.
- The current OLC adapter uses Mihomo gVisor. A future implementation may integrate
  olcbox's `hev-socks5-tunnel` directly.
- Automatic upstream synchronization with ClashFest is not configured yet.

