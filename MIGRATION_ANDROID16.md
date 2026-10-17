# Barinsta — Android 16 (API 36) Modernization

This document records the modernization of this fork so that it **builds with the
current Android toolchain and runs on the latest stable Android release,
Android 16 (API 36)** — the level Google Play now requires for new apps and
updates (mandatory from 31 Aug 2026).

It is written so a future maintainer can see exactly what changed, why, and what
still needs attention.

---

## 1. Target

| Setting        | Before | After |
|----------------|--------|-------|
| Gradle         | 7.1.1  | **8.11.1** |
| Android Gradle Plugin | 4.2.2 | **8.9.1** |
| Kotlin         | 1.5.21 | **2.2.10** |
| `compileSdk`   | 30     | **36** |
| `targetSdk`    | 30     | **36** |
| `minSdk`       | 21     | **21** (unchanged) |
| Java source/target | 1.8 | **17** |
| JDK to build   | 8      | **17** |

`minSdk` stays at 21. Note that Android 15+ devices will not install an app whose
*target* is below 24; that is a `targetSdk` concern, not `minSdk`, and `targetSdk`
is now 36.

---

## 2. Build-system changes (the reason the old project no longer compiled)

Android Gradle Plugin 8 makes several formerly-optional things mandatory and
removes others:

* **`namespace`** — added `namespace 'awais.instagrabber'` to `app/build.gradle`
  and removed the `package="..."` attribute from `AndroidManifest.xml`. AGP 8 no
  longer accepts `package` in the manifest.
* **`buildConfig` is off by default** in AGP 8 — enabled explicitly
  (`buildFeatures { buildConfig true }`), because the code uses `BuildConfig.dsn`
  and `BuildConfig.isPre`.
* **`packagingOptions` → `packaging`** and **`aaptOptions` → `androidResources`**
  (renamed DSL).
* **Jetifier removed** — AGP 8 dropped `android.enableJetifier`. The two
  `com.android.support:*` test dependencies that relied on it were migrated to
  their AndroidX equivalents (`androidx.annotation:annotation`,
  `androidx.test:runner`).
* **`androidx.navigation.safeargs` → `androidx.navigation.safeargs.kotlin`**.
* **`task clean(type: Delete)` → `tasks.register('clean', Delete)`**; the inline
  `exec { ... }` git-hash helper was replaced with `providers.exec` (Gradle 8
  removed the eager `Project.exec`).
* **`flavorDimensions "repo"` → `flavorDimensions += "repo"`**.
* **`androidx.lifecycle:lifecycle-extensions` removed** — that artifact was
  deleted from AndroidX long ago. `lifecycle-service` (needed by
  `DMSyncService`'s `LifecycleService` superclass) plus `lifecycle-runtime-ktx`
  were added explicitly.
* **`coreLibraryDesugaring`** bumped to `desugar_jdk_libs:2.1.3` (Java 17 +
  `java.time` support).

---

## 3. Dependencies

AndroidX / Google libraries were moved to current stable, API-36-compatible
versions:

| Library | Before | After |
|---|---|---|
| Material Components | 1.4.0 | 1.12.0 |
| androidx.core | 1.6.0 | 1.13.1 (core-ktx) |
| fragment-ktx | 1.3.6 | 1.8.5 |
| lifecycle | 2.3.1 | 2.8.7 |
| navigation | 2.4.0-alpha05 | 2.9.5 |
| Room | 2.3.0 | 2.8.5 |
| CameraX | 1.1.0-alpha07 | 1.3.4 |
| WorkManager | 2.5.0 | 2.9.1 |
| ExoPlayer | 2.14.2 | 2.19.1 |
| RecyclerView | 1.2.1 | 1.3.2 |
| ConstraintLayout | 2.0.4 | 2.2.0 |
| Guava | 27.0.1-android | 33.3.1-android |
| Fresco | 2.5.0 | 3.9.0 |
| Retrofit | 2.9.0 | 2.11.0 |
| LeakCanary | 2.7 | 2.14 |
| Sentry (github flavor) | 5.0.1 | 7.14.0 |

**Deliberately not bumped:**

* `androidx.emoji:emoji` / `emoji-appcompat` stay at **1.1.0**. The 1.5.0 line is
  `androidx.emoji2`, which moved to the `androidx.emoji2.text.*` package and
  would require rewriting every `EmojiCompat` call site. The 1.1.0 artifact is
  the last release of the old API and still runs on API 36.
* `com.github.skydoves:balloon` stays at **1.3.5**. Balloon 1.4+ changed the
  `setBalloonHighlightAnimation` signature and moved overlay types; bumping it
  needs a small code migration that was out of scope for a build-fixing pass.
* Third-party JitPack artifacts from `ammargitham` are unchanged.

---

## 4. Runtime behaviour changes (Android 12 → 16)

These are the changes that would **crash or misbehave at runtime** on a modern
device, independent of the build system.

### 4.1 `PendingIntent` mutability (crash on Android 12 / API 31+)
Since API 31 every `PendingIntent` must declare `FLAG_IMMUTABLE` or
`FLAG_MUTABLE`, or the call throws `IllegalArgumentException`. Five call sites
were fixed, all with `FLAG_IMMUTABLE` (none need a mutable intent):

* `workers/DownloadWorker.kt`
* `services/DeleteImageIntentService.java`
* `services/DMSyncService.java`
* `services/DMSyncAlarmReceiver.java`
* `services/ActivityCheckerService.java`

### 4.2 Foreground-service type (crash on Android 14 / API 34+)
`DMSyncService` starts a foreground service. From API 34 a foreground service
must declare a `foregroundServiceType` and hold the matching permission, or
`startForeground` throws. Fixed:

* Manifest: `android:foregroundServiceType="dataSync"` on `DMSyncService`, plus
  `FOREGROUND_SERVICE` and `FOREGROUND_SERVICE_DATA_SYNC` permissions.
* Code: switched to `ServiceCompat.startForeground(..., FOREGROUND_SERVICE_TYPE_DATA_SYNC)`
  so the type is supplied on every API level.

### 4.3 `android:exported` (build failure on API 31+)
Any component with an `<intent-filter>` must declare `android:exported`.
`MainActivity` (the launcher, plus VIEW/SEND handlers) now has
`android:exported="true"`. Every other component is explicitly
`android:exported="false"`.

### 4.4 Notifications permission (Android 13 / API 33+)
Added `POST_NOTIFICATIONS` to the manifest and a runtime request in
`MainActivity.onCreate` (`requestNotificationPermissionIfNeeded()`), guarded so
it is a no-op below API 33. Notification channels were already created.

### 4.5 Storage permissions (Android 13 / API 33+)
`READ_EXTERNAL_STORAGE` is capped at `maxSdkVersion="32"` and the granular
`READ_MEDIA_IMAGES` / `READ_MEDIA_VIDEO` permissions were added for API 33+.
`WRITE_EXTERNAL_STORAGE` is capped at `maxSdkVersion="28"` (it is ignored from
Android 11 onward; the app already downloads through SAF via
`DownloadUtils`).

### 4.6 Edge-to-edge (Android 15/16)
`MainActivity` already calls `WindowCompat.setDecorFitsSystemWindows(window, false)`
and installs an insets callback, so the enforced edge-to-edge mode on API 35/36
is handled. Visual verification on a device is still recommended (see §6).

### 4.7 Predictive back (Android 13+)
`android:enableOnBackInvokedCallback="true"` added so the system predictive-back
animation works.

---

## 5. CI

A new workflow, `.github/workflows/android.yml`, runs on every push and pull
request to `master`:

1. JDK 17 + Android SDK setup,
2. `./gradlew assembleGithubDebug` (produces debug APKs, uploaded as an artifact),
3. `./gradlew testGithubDebugUnitTest` (unit tests).

The two pre-existing release workflows were updated from JDK 8 to JDK 17 and
from the deprecated `actions/*@v2` to `@v4`.

---

## 6. What is verified, and what is not

**Verified here:** every change above is in the tree and internally consistent;
the build configuration targets API 36 with AGP 8 / Gradle 8 / JDK 17.

**Verified by CI:** compilation and the unit tests — the CI workflow is the
authority, since a full Android build cannot be run in the authoring sandbox.

**Not yet verified (needs a device/emulator):**

* Visual result of the theme and layouts on Android 16 (edge-to-edge insets,
  status/navigation bar contrast).
* The DM-sync foreground service and its notification on a real API 34+ device.
* The download flow under scoped storage on API 33+.

**Recommended follow-ups:**

* Migrate the theme from Material 2 (`Theme.MaterialComponents.*`) to Material 3
  (`Theme.Material3.*`) — this is a layout-and-style-wide change that should be
  done against a running app, not blind.
* Replace `androidx.emoji` with `androidx.emoji2`.
* Migrate `startActivityForResult` / `onActivityResult` (34 call sites) to the
  Activity Result API.
* Bump `balloon` and fix its call sites.
