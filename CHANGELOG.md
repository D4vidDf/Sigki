# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-08-17

### Added
- **Initial Release of SigKi**: A lightning-fast utility shortcut for Android.
- **Smart Multimedia Control**:
    - Instant Play/Pause and Stop actions.
    - Fallback logic to launch a "Preferred Media App" (Spotify, YouTube Music, etc.) if no music is active.
    - Automatic filtering of media-compatible apps in the selector.
- **Rich Screenshot Suite**:
    - Capture and share/edit with a single gesture.
    - Modern icon-only action panel for post-capture workflow.
    - High-quality system share sheet integration with image previews.
- **Instant Contact Actions**:
    - Quick Call, Message, or Profile View for your favorite contacts.
    - Implementation of Lookup URI for reliable linking to the system Contacts app.
- **Quick-Access Tools**:
    - **Camera**: Optimized for fast launching, including lock screen support.
    - **Flashlight**: One-tap toggle.
    - **App Launcher**: Start any installed app instantly.
    - **URL Shortcut**: Open any specific web destination.
- **System Utilities**:
    - Toggle **Do Not Disturb** and **TalkBack** accessibility settings.
- **Material 3 Expressive UI**:
    - Fluid carousel selector with proximity-based focus animations.
    - Smooth cross-fade transitions for option panels.
    - Edge-to-edge support with automatic keyboard (IME) padding.
- **Localization**: Full support for **English** and **Spanish** ("multimedia" terminology).
- **Privacy-First Architecture**: No data tracking, local execution only.
- **Project Documentation**: Added README.md, SECURITY.md, and contribution guidelines.

### Fixed
- Stabilized bottom sheet behavior using the latest Material 3 APIs.
- Resolved "re-appearing" screenshot preview bug after system theme changes.
- Fixed contact profile linking to use specific Lookup URIs.
- Corrected package structure and optimized service invocation after refactoring.

---
[1.0.0]: https://github.com/d4viddf/sigki/releases/tag/v1.0.0
