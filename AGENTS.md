# AGENTS.md — cll1-gdx

Cherokee Language Lessons 1 — libGDX (Java) picture-card drill app. Trunk branch: `master`. CI: CodeQL Java analysis on `master` only. No test suite exists.

## Toolchain & build

- Build exclusively through `./gradlew` — Gradle **7.3.3** wrapper, AGP 7.2.1, libGDX 1.9.10, RoboVM 2.3.9.
- **Gradle 7.3.3 requires JDK ≤ 17.** JDK 21/25 fail at daemon start (`Unsupported class file major version 65`) and no compatible JDK is preinstalled on the dev box. A Temurin 17.0.21 is vendored untracked at `.tools/jdk-17/`; run `JAVA_HOME="$PWD/.tools/jdk-17" ./gradlew …` (re-download from the Adoptium API if missing). Verified working: `help`, `core:compileJava`, `desktop:dist`, `android:tasks`.
- Bytecode targets: `core`, `desktop`, and `ios` compile with `options.release = 8`; only `android` stays on its declared source/target 1.7. `core` and `ios` pin UTF-8 source encoding.
- Modules (`settings.gradle`): `core` (game logic, entry class `com.cherokeelessons.cll1.CLL1`), `desktop` (`DesktopLauncher`), `android` (`AndroidLauncher`), `ios` (RoboVM, `IOSLauncher`). `core` depends on JitPack `com.github.CherokeeLanguage:LeitnerCards:20161025` — the Leitner-box deck engine lives in that sibling repo.
- Verification = compile + artifact, not tests: `./gradlew desktop:dist` produces `desktop/build/libs/CLL1-<version>.jar`.

## Assets — generated, don't hand-edit

- `android/assets/` is the single asset root for **all** platforms: desktop `resources.srcDirs` and the `desktop:run` working dir point at `../android/assets`; android `assets.srcDirs = ['assets']`; the ios Eclipse project links the same dir.
- `0_files.txt` / `0_dirs.txt` are codegen: the root `files_txt` task runs `android/assets/make-files-txt.sh` before every `core:compileJava`, writing an index pair into each assets subdir. They are gitignored — never hand-edit or commit them.
- Card art/audio are generated from sources: edit `artwork/card-data/` (SVGs, mp3s), then run `artwork/card-data/rebuild-all.sh` (needs `inkscape`, `pngcrush`; renders at DPI 45) which syncs into `android/assets/card-data/`. The shipped copies under `android/assets/card-data/` are build outputs.
- The shipped deck is `android/assets/card-data/cards.csv` (columns: `#CHAPTER`, `TEXT`, `AUDIO`, `ANSWER-PIC`, `BLACKLIST-PIC`, `ENGLISH GLOSS`, `NOTES`). Other `cards.csv` copies exist (`Assets/`, `notes/`) and differ — only the `android/assets` one is packaged.

## Run

- `./gradlew desktop:run` (working dir = `android/assets`). Desktop runtime state — per-slot `DeckStats.json` / `CardsInPlay.txt` — is written to `~/CLL2EV1/slots/<n>/` via `Gdx.files.external`; Android/iOS use app-local storage. The slot namespace is `CLL2EV1` — same cll2-era quirk as the package id.

## Android & release

- `applicationId` and manifest package are `com.cherokeelessons.cll2ev1` — a cll2-era id kept to match the published Play listing. Do not "fix" it.
- Release signing reads `RELEASE_STORE_FILEA`, `RELEASE_STORE_FILEB`, `RELEASE_STORE_PASSWORD`, `RELEASE_KEY_ALIAS`, `RELEASE_KEY_PASSWORD` — defined only in the user-level `~/.gradle/gradle.properties`, nowhere in this repo.
- Versions live in synced places: integer in the `version` file (`182`) ↔ `VERSION_CODE`/`VERSION_NAME` (`182`/`1.82`) in `gradle.properties`, mirrored into `android/AndroidManifest.xml` and `ios/robovm.properties`. `versionCode` = `VERSION_NAME` with the dot removed.
- `build-release.sh` is destructive-by-design: it builds, then `git add .` / `git commit -a`, bumps every version location, tags, and `git push --all --tags`. Never run it just to test a build — use plain gradle tasks.
- Android: `minSdkVersion 14`, `targetSdkVersion 29` (build.gradle wins over the manifest's `uses-sdk targetSdkVersion=30`). The android `run` task is stale (points at `com.mygdx.game`) — don't rely on it.

## Distribution (policy, 2026-11)

- App distribution: itch.io only. Ebook: cherokeelessons.org only. The README still links Google Play / App Store / Amazon / Smashwords — check local issue `cll1-gdx#1` before touching distribution or store links.
