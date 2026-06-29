# TODO

Living list of known gaps, limitations, and deferred work. Items are linked to GitHub issues where tracked formally.

> Update this file alongside issue status. If something is done, close the issue and remove it here.

---

## Alpha Blockers

These must be resolved before `v0.1.0-alpha` is tagged.

| # | Item                                                           | Issue                                                      |
|---|----------------------------------------------------------------|------------------------------------------------------------|
| 1 | yt-dlp integration (Chaquopy vs remote proxy decision pending) | [#7](https://github.com/milya18/YOUR_REPO_NAME/issues/7)   |
| 2 | ffmpeg-kit Opus compression pipeline                           | [#9](https://github.com/milya18/YOUR_REPO_NAME/issues/9)   |
| 3 | Room DB Track entity and DAO                                   | [#10](https://github.com/milya18/YOUR_REPO_NAME/issues/10) |
| 4 | VaultDataSource for ExoPlayer                                  | [#14](https://github.com/milya18/YOUR_REPO_NAME/issues/14) |
| 5 | MediaSessionService background playback                        | [#13](https://github.com/milya18/YOUR_REPO_NAME/issues/13) |
| 6 | Unit test coverage ≥ 80% on core modules                       | [#17](https://github.com/milya18/YOUR_REPO_NAME/issues/17) |
| 7 | End-to-end integration test (URL → playback)                   | [#18](https://github.com/milya18/YOUR_REPO_NAME/issues/18) |

---

## Known Alpha Limitations

Accepted for alpha, will be addressed post-release.

- **No playlist support** — tracks play individually, no queue management
- **No seek persistence** — playback position is not saved on app close
- **No offline YouTube metadata** — title/artist depends on yt-dlp response; no local cache fallback
- **No retry UI** — failed downloads silently retry via WorkManager; no manual retry button
- **Single audio source** — only YouTube via yt-dlp; no podcast RSS, SoundCloud, or direct URL support
- **No settings screen** — compression profile is hardcoded; no in-app way to change it
- **No lock-screen album art** — media notification shows title only, no artwork
- **Mono downmix not exposed in UI** — must be changed in code (`CompressionProfile`)
- **No export / backup** — `.opus` files and Room DB are in `filesDir`, not user-accessible storage

---

## Post-Alpha Backlog

Not tracked as issues yet. Open an issue when picking these up.

- [ ] Playlist / queue management
- [ ] Seek position persistence across app restarts
- [ ] In-app compression profile picker per track
- [ ] Podcast RSS source support
- [ ] Background metadata fetch (thumbnail, duration) after download
- [ ] Storage usage screen with per-track size breakdown
- [ ] Batch delete / eviction controls in UI
- [ ] Widget for lock-screen playback controls
- [ ] Export tracks to user-accessible storage
- [ ] Equalizer (ExoPlayer audio processor)
