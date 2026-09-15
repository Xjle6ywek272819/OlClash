#!/usr/bin/env bash
set -euo pipefail

readonly release_url="https://github.com/alananisimov/olcbox/releases/download/nightly/Olcbox-1.0.129-android-arm64-v8a-release.apk"
readonly expected_sha256="8dc21b18112805d0a0103fb734f0640a0a5974b9770970f3e22acc7a55fc552d"
readonly destination="service/src/main/jniLibs/arm64-v8a/libgojni.so"

temp_dir="$(mktemp -d)"
trap 'rm -rf "$temp_dir"' EXIT

curl --fail --location --retry 3 --output "$temp_dir/olcbox.apk" "$release_url"
printf '%s  %s\n' "$expected_sha256" "$temp_dir/olcbox.apk" | sha256sum --check --strict

mkdir -p "$(dirname "$destination")"
unzip -p "$temp_dir/olcbox.apk" lib/arm64-v8a/libgojni.so > "$destination"
test -s "$destination"
echo "Pinned olcRTC Android runtime prepared at $destination"
