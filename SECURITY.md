# Security Policy

## Supported versions

Security fixes target the newest OlcLash pre-release. Older CI artifacts and releases may not receive fixes.

## Reporting a vulnerability

Do not open a public issue. Use the repository's **Security → Report a vulnerability** private advisory form. Include the affected OlcLash and Android versions, profile type, redacted reproduction steps, impact, and whether the behavior comes from ClashFest, Mihomo, or olcRTC.

Never disclose subscription URLs or bodies, credentials, room identifiers, HWIDs, device identifiers, signing material, or unredacted logs.

If private advisories are unavailable, contact the repository owner through their GitHub profile and disclose only enough to establish a private channel.

## Scope

Relevant reports include credential exposure, traffic escaping the intended VPN policy, externally reachable local listeners, unsafe subscription processing, VPN-loop/protection failures, and release or dependency-integrity problems.

Please allow reasonable time for investigation and a release before public disclosure.
