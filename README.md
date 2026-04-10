# AriangUI

[![License](https://img.shields.io/github/license/mayswind/AriaNg.svg?style=flat)](https://github.com/mayswind/AriaNg/blob/master/LICENSE)

## Introduction

AriangUI is the Android application of [AriaNg](https://github.com/mayswind/AriaNg), a modern web frontend for [aria2](https://github.com/aria2/aria2). Built with Capacitor, AriangUI provides a native Android experience while retaining all the features of AriaNg.

## Features

1. Native Android application with AriaNg web interface
2. Built-in aria2 download engine (no separate installation required)
3. Responsive design supporting desktop and mobile devices
4. User-friendly interface
   - Sort tasks (by name, size, progress, remaining time, download speed, etc.)
   - Search tasks
   - Retry tasks
   - Adjust task order by dragging
   - More task information (health percentage, BT peer client info, etc.)
   - Filter files by type or extension
   - Tree view for multi-directory tasks
   - Download/upload speed charts
   - Full aria2 settings support
5. Dark theme support
6. Url command line API support
7. Download finished notifications
8. Multi-language support
9. Multiple aria2 RPC host support
10. Export/import settings support
11. Low bandwidth usage (incremental data requests)

## Architecture

- **Frontend**: AriaNg 1.3.13 (AngularJS 1.8.3)
- **Framework**: Capacitor 8.x
- **Native Plugin**: aria2-plugin (provides aria2 service)

## Installation

### Prebuilt APK

Download the latest APK from the [Releases](https://github.com/mayswind/AriaNg/releases) page.

### Building from Source

Prerequisites:
- [Node.js](https://nodejs.org/)
- [pnpm](https://pnpm.io/) (recommended) or npm
- [Android SDK](https://developer.android.com/studio)

```bash
# Install dependencies
pnpm install

# Build aria2-plugin
just sync

# Build Android APK
just build
```

Or use the following commands:

```bash
# Install npm dependencies
npm install

# Build aria2-plugin
cd aria2-plugin && pnpm install && pnpm run build

# Build AriaNg frontend
gulp clean build

# Sync Capacitor
npx cap sync

# Build Android APK
npx cap build android
```

The APK will be generated in `android/app/build/outputs/apk/debug/`.

## Quick Start

1. Install and launch AriangUI
2. The app will automatically start the built-in aria2 service
3. Configure your download directory in settings
4. Add download tasks via the + button or paste URLs

## Configuration

Access AriaNg settings via the menu to configure:
- RPC connection (host, port, secret)
- Download directory
- Concurrent downloads
- Connection settings
- And all other aria2 options

## Screenshots

#### Main Interface
![AriangUI](screenshots/main.png)
#### Downloads
![AriangUI](screenshots/downloads.png)
#### Settings
![AriangUI](screenshots/settings.png)

## API Reference

The aria2-plugin provides the following interfaces:

```typescript
// Configure aria2 service
await Aria2.configure({
  config: {
    rpcPort: 6800,
    downloadDir: '/sdcard/Download',
    split: 8,
    maxConcurrentDownloads: 10
  },
  restart: true
});

// Start aria2 service
await Aria2.start();

// Stop aria2 service
await Aria2.stop();
```

## Translating

Language files are located in `src/langs/`. To add a new language:
1. Add language config to `src/scripts/config/languages.js`
2. Copy `i18n/en.sample.txt` to `src/langs/<langcode>.txt`
3. Translate the strings

Current supported languages: English, 简体中文, 繁體中文, and more.

## License

[AriaNg License](https://github.com/mayswind/AriaNg/blob/master/LICENSE) - MIT