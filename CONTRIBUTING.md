# Contributing

This is a private project. These guidelines exist to keep the commit history clean and the CI green.

---

## Table of Contents

- [Branch Naming](#branch-naming)
- [Commit Conventions](#commit-conventions)
- [PR Checklist](#pr-checklist)
- [Local Setup](#local-setup)
- [Running Checks Locally](#running-checks-locally)

---

## Branch Naming

```
feature/ISSUE-NUMBER-short-description
fix/ISSUE-NUMBER-short-description
docs/ISSUE-NUMBER-short-description
chore/ISSUE-NUMBER-short-description
```

Examples:
```
feature/7-ytdlp-extractor
fix/12-crash-on-rotate
docs/4-contributing-md
```

All branches target `develop`. Only release PRs target `main`.

---

## Commit Conventions

Follows [Conventional Commits](https://www.conventionalcommits.org/).

```
<type>(<scope>): <description>

[optional body]

Closes #<issue>
```

**Types:** `feat` `fix` `docs` `style` `refactor` `perf` `test` `build` `ci` `chore` `revert`

**Scopes:** `download` `compress` `vault` `playback` `db` `ui` `ci`

Examples:
```
feat(download): implement yt-dlp audio extractor

Closes #7

fix(playback): prevent crash when track file is missing

Closes #12

test(db): add in-memory DAO unit tests

Closes #14
```

> **Note:** PR titles are validated against Conventional Commits by CI. The PR will be blocked if the title doesn't match.

---

## PR Checklist

Before opening a PR, verify:

- [ ] Branch is up to date with `develop`
- [ ] PR title follows Conventional Commits format
- [ ] `./gradlew ktlintCheck` passes
- [ ] `./gradlew testDebugUnitTest` passes
- [ ] KDoc added for all new public functions
- [ ] Linked issue number in PR description (`Closes #N`)
- [ ] Acceptance criteria in the linked issue are met

---

## Local Setup

**Prerequisites:** JDK 17, Android Studio Otter 2025.2.2+, Android SDK 37.

```bash
git clone https://github.com/milya18/milya-music.git
cd YOUR_REPO_NAME
./gradlew assembleDebug
```

Set `develop` as your default upstream:
```bash
git checkout dev
git pull origin dev
```

---

## Running Checks Locally

```bash
# Lint
./gradlew ktlintCheck

# Auto-fix lint
./gradlew ktlintFormat

# Unit tests
./gradlew testDebugUnitTest

# Instrumented tests (requires connected device or emulator)
./gradlew connectedAndroidTest

# Full build
./gradlew assembleDebug
```
