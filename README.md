# ColorMotion

An Xposed module (Vector/LSPosed) that gives Samsung One UI 8.5's notification shade and quick settings a smoother animation: a spring-driven panel, a row-by-row cascade of QS tiles and notification cards, rebound on release, and a matching blur.

Tested on a Galaxy S24 (SM-S921B) running One UI 8.5 / Android 16 with Vector. Other devices and versions are untested. The module checks the SystemUI classes it binds to and removes all of its hooks if they don't match.

## Install

1. Install the APK from [Releases](../../releases).
2. Enable ColorMotion in Vector/LSPosed with the System UI scope.
3. Restart SystemUI or reboot.

## Build

Requires JDK 17+ and the Android SDK (platform 36, build-tools 36.0.0). Point `local.properties` at the SDK (`sdk.dir=/path/to/Android/Sdk`), then:

```sh
./gradlew :app:assembleInstallWorkaround
```

The APK is written to `app/build/outputs/apk/installWorkaround/`. This variant is identical to `release` but marked debuggable, so Android 16 skips install-time dexopt, which can loop under some Vector builds. `./gradlew :app:assembleRelease` builds the non-debuggable variant.

## Layout

- `app` – the module.
- `nativeBridge` – a small typed adapter, dexed into the APK's assets and loaded with SystemUI's class loader, so per-frame layer and notification work avoids reflection.
- `nativeApi` – compile-only stubs of the SystemUI/Compose members the bridge uses. Never packaged.
