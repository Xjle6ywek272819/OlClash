# olcRTC Android runtime provenance

OlcLash uses the `mobile` bindings and `libgojni.so` built by the official
olcbox Android release pipeline.

- Upstream: <https://github.com/alananisimov/olcbox>
- Release: `1.0.129-nightly`
- Upstream commit: `5fbec63b94951673593bb94d4bd81d72dc253e1e`
- Asset: `Olcbox-1.0.129-android-arm64-v8a-release.apk`
- Asset SHA-256: `8dc21b18112805d0a0103fb734f0640a0a5974b9770970f3e22acc7a55fc552d`
- Pinned olcRTC revision declared upstream: `08843d6accd7f43a1d04aa0ac7a3d5ea90e27efa`

The generated Java bindings under `service/src/main/java/mobile` and
`service/src/main/java/go` must be updated together with the native library.
They were recovered from that exact APK because the stale `olcrtc-sources.jar`
currently present in the olcbox repository exposes the older static API and is
binary-incompatible with the release runtime.
