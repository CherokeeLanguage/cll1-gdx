# CLL-1 itch publish record — evidence log (cll1-gdx#5)

## 2026-11-12

### Page creation (developer-executed in browser)

- URL: `https://cherokee-language.itch.io/cherokee-language-lessons-1` — anonymous GET = 200 (was 404 on 2026-11-11 pre-creation). SC-3 evidence.
- Page body contains "Cherokee Language Lessons 1" (count 2); `og:image` present; `game_iframe|html_embed` count = 0 (Downloadable-classification discriminator). SC-7 partial evidence.
- Cover: developer-uploaded 630×500 icon (developer: "the icon is 630x500") — meets itch's recommended cover size (315:250 ratio, 630×500 recommended).
- Classification Games / kind Downloadable, per spec R-3's page guidance.

### Description (SC-7)

- Copy derived from the app's existing copy (CherokeeLessons `content/apps/cherokee-language-lessons-1.md`), formatted as HTML per developer note ("the details/description is HTML, not markdown").
- Two developer-directed corrections applied to the publication ("typos fixed for the itch.io publication", 2026-11-12): "though"→"through" (×2) and the Pimsleur sentence reworded to "Pimsleur timings for each training session".
- **SC-7 deviation note:** the instrument reads "matching the app's existing copy (the site page's summary sentence)" — the publication carries the corrected copy ahead of the source page. Convergence spec filed on the CherokeeLessons tracker: https://github.com/CherokeeLanguage/CherokeeLessons/issues/35 (typo fixes; interdependencies declared to CherokeeLessons#32/#30). No CherokeeLessons file was modified from this session — repo boundary held per developer directive ("do not cross repo-boundary for typos, file fix specs instead where appropriate").

### Desktop jar push (R-3 / SC-4 desktop half — done)

- Artifact: `desktop/build/libs/CLL1-1.82.jar` (built 2026-11-11 22:32 local). SC-1 checks passed: manifest `Main-Class: com.cherokeelessons.cll1.desktop.DesktopLauncher`; `DesktopLauncher.class` present.
- Command: `./.tools/butler/butler push desktop/build/libs/CLL1-1.82.jar cherokee-language/cherokee-language-lessons-1:win-linux-mac-stable --userversion 1.82`
- Transcript: `research/butler-push-jar-1.82.log` (stored copy) — "Pushing 48.57 MiB (1 files)", "✓ Added 48.57 MiB fresh data", first build on the channel.
- `butler status`: channel `win-linux-mac-stable`, upload #20457549, build #2362541 ✓, VERSION 1.82.
- SC-5 (desktop half): page HTML server-renders "CLL1-1.82.jar" (count 1).
- Channel naming: `win-linux-mac-stable` per itch docs' Java-game pattern (single jar, three platform tags; tag set adjustable later in Edit game).

### Butler tooling

- butler v15.32.0 installed at `.tools/butler/` (`/.tools/` is gitignored — never enters history), from `https://broth.itch.zone/butler/linux-amd64/LATEST/archive/default` — the research-verified official automation source.
- Credentials: `~/.config/itch/butler_creds` (2026-11-05) validated via `butler login` → "Your local credentials are valid!" (no `whoami` command exists in current butler — research finding; `login` with existing creds is the validation path).
- `BUTLER_API_KEY` env var remains supported for future CI (research-verified).

### Android APK (R-1b / R-2 / SC-2) — pending

- Existing artifact `android/build/outputs/apk/release/cll1-gdx-1.82-release.apk` is dated 2022-08-01 — stale; rebuild required through the sanctioned toolchain (`JAVA_HOME="$PWD/.tools/jdk-17" ./gradlew android:assembleRelease`).
- Signing (triage resolved 2026-11-12): `android/build.gradle`'s `release` config selects `RELEASE_STORE_FILEA` **if it is a directory**, else `RELEASE_STORE_FILEB`. FILEA's value is a dead macOS path (`/Users/mconrad`, absent on this box) → the config auto-falls to FILEB = `/home/muksihs/Keystore` — an existing 1713-byte keystore file (2020-06-22). Passwords/alias come from the same user-level properties; validity is proven only by the R-2 instrument (`apksigner verify --print-certs` exit 0). No new keystore generation unless build/verify fails.
- Android channel plan: `android` (itch platform inference).

### Tree state at publish (SC-6 / SC-8)

- Spec probe (2026-11-11): HEAD `68d05fb` + 6 modified build files (in-flight modernization). At publish (2026-11-12): HEAD `0f70a8d`, tree **clean** — the modernization landed in `cd16277` ("record changes and prep for opencode usage"), plus `6cd2cac` (AGENTS.md, PR #6) and `0f70a8d` (gitignore `.issues/`/`.tools/`, PR #7). SC-6's instrument's literal sha (`68d05fb…`) is superseded by the actual publish-time state recorded here; the pushed jar is the `0f70a8d`-state artifact (Gradle `:desktop:dist UP-TO-DATE` against this tree).
- SC-8: this session's build+publish triggered no commits, tags, or version changes (`git status --porcelain` empty before and after the build/publish; no `build-release.sh` use).

### SC-6 provenance

- The 2026-11-11 22:32 jar build predates this session; its build log was not captured. A logged `desktop:dist` rebuild from the identical tree state executed 2026-11-12: **BUILD SUCCESSFUL, exit 0, `:desktop:dist UP-TO-DATE`** — Gradle affirms the pushed jar is the current-tree artifact. Pre-state at `research/provenance-prestate.txt` (HEAD sha, dirty diff, JDK 17 toolchain), build log at `research/desktop-dist-rebuild.log`, both stored in this record folder — SC-6's evidence set (sha, diff, log, toolchain, issue reference via this folder) is complete.

🤖 Co-authored with AI: OpenCode (huggingface/zai-org/GLM-5.3-Flash)
