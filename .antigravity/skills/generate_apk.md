name: generate-apk
description: Builds a single debug APK for Dragon Launcher to sideload on a device, copies it to the repo root with a clear filename, and avoids slow multi-flavor debug builds. Use when the user wants generate-apk, a test APK, phone install, sideload, debug APK, preview on device, or quick device testing without release signing.
---

# Generate test APK (Dragon Launcher)

## Goal

Produce **one** debug APK suitable for manual install on a physical device, placed at the **repository root** with an obvious name.

## Critical constraint (speed)

This app defines product flavors `stable`, `beta`, and `fdroid` on `:app`. The task `assembleDebug` builds **all** debug variants and forces redundant work across the graph.

**Default:** run exactly **one** variant task:

- **`assembleStableDebug`** — stable channel, debug (default unless the user names another flavor).

Only use `assembleBetaDebug` or `assembleFdroidDebug` if the user explicitly asks for that channel.

Do **not** run `assembleDebug` unless the user explicitly wants every debug variant.

## SDK location

If Gradle reports missing SDK:

1. Prefer `ANDROID_HOME` or `ANDROID_SDK_ROOT` already set in the environment.
2. Otherwise create or update **`local.properties`** at the repo root (already gitignored) with:

   ```properties
   sdk.dir=/absolute/path/to/Android/Sdk
   ```

   Use forward slashes in `sdk.dir` even on Windows (e.g. `C:/Users/.../Android/Sdk`).

## Build

From the repository root:

- **Windows:** `.\gradlew.bat assembleStableDebug --no-daemon -x lint`
- **Unix:** `./gradlew assembleStableDebug --no-daemon -x lint`

Adjust the task name if the user requested another flavor (`assembleBetaDebug`, `assembleFdroidDebug`).

## Artifact path and copy

After a successful **stable** debug build, the APK is:

`app/build/outputs/apk/stable/debug/app-stable-debug.apk`

Copy it to the repo root with a descriptive name, for example:

`DragonLauncher-v{VERSION_NAME}-stable-debug.apk`

Read `VERSION_NAME` from `gradle.properties` at the repo root. For beta/fdroid, mirror the output path under `app/build/outputs/apk/<flavor>/debug/` and reflect the flavor in the filename.

## Aftermath

- Debug APKs are **not** release-signed; that is expected for local testing.
- Root-level `*.apk` is **not** in this project’s `.gitignore`; warn the user before they `git add .` if an APK sits in the root.
