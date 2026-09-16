# Contributing to OlcLash

Read [AGENTS.md](AGENTS.md) before changing code; its security and architecture rules apply to every contribution.

## Workflow

1. Branch from `main` using `feat/<slug>`, `fix/<slug>`, `docs/<slug>`, `chore/<slug>`, or `refactor/<slug>`.
2. Keep one logical change per branch and use conventional commit messages.
3. Build `assembleAlphaDebug` and describe verification in the pull request.
4. Open the pull request against `main`.

Do not force-push shared branches or combine core updates with unrelated app changes.

## Build

```bash
git submodule update --init --recursive
./gradlew assembleAlphaDebug
```

Use JDK 21, the compatible Android SDK/NDK, and the committed Gradle wrapper. On Windows use `.\gradlew.bat assembleAlphaDebug`.

## Code and UI

- Match surrounding Kotlin/Go style; avoid unrelated reformatting.
- Keep code packages under `com.github.kr328.clash.*`.
- Use XML/Material 3, not Compose.
- Update English, Russian, and Simplified Chinese for user-visible text.
- Explain new permissions, endpoints, and trust-boundary changes.
- Preserve loopback-only listeners, VPN loop prevention, subscription sanitization, and network allowlists.

Never commit real subscription data, credentials, room IDs, HWIDs, signing material, or unredacted logs. Report vulnerabilities through [SECURITY.md](SECURITY.md).

## Upstream and license

The application upstream is [ClashFest](https://github.com/Nemu-x/ClashFest), branch `feat/init-clashfest`. Syncs arrive as reviewable PRs and are never auto-merged. olcbox/olcRTC are pinned runtime sources, not Git history remotes.

Contributions are released under GPL-3.0. Preserve all notices and third-party licenses.
