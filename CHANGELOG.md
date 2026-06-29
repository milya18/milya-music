# Changelog

All notable changes to Milya are documented here.

Format follows [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).
Versioning follows [Semantic Versioning](https://semver.org/).

---

## [Unreleased]

### Added
- Project scaffold and package structure
- GitHub Actions CI pipeline (ktlint, unit tests, instrumented tests, assemble)
- Commitlint on PR titles
- Auto-assign and auto-label workflows
- Branch protection on `main`
- `.github` templates (PR, bug report, feature request)

---

## [0.1.0-alpha] — TBD

### Added
- YouTube audio extraction via yt-dlp
- Background download pipeline via WorkManager with progress notification
- Opus compression via ffmpeg-kit (`MUSIC_BALANCED` through `MINIMUM` profiles)
- Room database for track metadata and last-played tracking
- Auto-eviction of tracks not played in 14 days
- `VaultDataSource` — custom Media3 DataSource reading `.opus` files from `filesDir`
- `AudioPlaybackService` — foreground `MediaSessionService` with wake lock and audio focus
- Lock-screen media controls (play, pause, stop)
- Minimal playback UI — track list, controls, YouTube URL input, download progress
- Full unit test suite (≥ 80% line coverage on core modules)
- End-to-end instrumented test (URL → compress → Room → playback)
- KDoc on all public APIs
- Dokka HTML documentation

---

[Unreleased]: https://github.com/milya18/YOUR_REPO_NAME/compare/v0.1.0-alpha...HEAD
[0.1.0-alpha]: https://github.com/milya18/YOUR_REPO_NAME/releases/tag/v0.1.0-alpha
