## 26.1.2.4

### Changes

- Update compatibility with ArrowPlus `26.1.2.104` and its modular arrow system.
- Preserve head, stick, and feather components in JEI tipping and explosive recipes.
- Add JEI recipe lookups for custom arrow combinations and distinguish explosive variants.
- Keep ArrowPlus optional in both loader builds.
- Respect disabled arrow heads and require a compatible ArrowPlus version when installed.
- Fix duplicate JEI tipped-arrow registration and preserve potion and explosive subtype identity.

### NeoForge

- Update NeoForge to `26.1.2.122` to match the supported ArrowPlus release.
- Update ModDevGradle to `2.0.147` for the matching NeoForge toolchain.

### Fabric

- Update Fabric Loom to `1.17.20` and Gradle to `9.5.0` for the 26.1.2 build.
- Update Fabric API to `0.155.3+26.1.2` and Fabric Loader to `0.19.4`.
