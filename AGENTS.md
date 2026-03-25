# AGENTS.md - AriaNg Developer Guide

## Overview
AriaNg is a modern web frontend for aria2, written in AngularJS 1.8.3. The project uses Gulp for build orchestration and has no formal test framework.

## Build Commands

### Installation
```bash
npm install
```

### Build
- **Standard version** (outputs to `dist/`):
  ```bash
  npm run build
  # or: gulp clean build
  ```

- **All-in-one version** (single HTML file):
  ```bash
  gulp clean build-bundle
  ```

### Development Server
```bash
gulp serve          # Development server on port 9000
gulp serve:dist     # Serve built files from dist/
```

### Linting
No formal linter is configured. Code style is consistent with existing codebase.

### Testing
**No test framework is configured.** The `npm test` command exits with error:
```bash
npm test  # echo "Error: no test specified" && exit 0
```

## Code Style Guidelines

### General Structure
- Use **IIFE pattern** with `"use strict"` at the top of every JS file:
  ```javascript
  (function () {
      "use strict";
      // code here
  })();
  ```

### File Organization
- Source files in `src/scripts/` organized by type:
  - `core/` - App initialization, router
  - `controllers/` - Angular controllers
  - `services/` - Angular services/factories
  - `directives/` - Angular directives
  - `filters/` - Angular filters
  - `config/` - Configuration, constants, options
  - `aria2/` - Aria2-specific logic

### Naming Conventions
- **Files**: lowercase with hyphens (e.g., `aria2-rpc-service.js`)
- **Angular modules**: `ariaNg` (lowercase, no hyphens)
- **Factories/Services**: camelCase (e.g., `aria2RpcService`)
- **Controllers**: camelCase (e.g., `mainController`)
- **Directives**: camelCase in code, kebab-case in HTML (e.g., `autoFocus` -> `auto-focus`)

### Angular Patterns
- Use **array notation** for dependency injection to minify safely:
  ```javascript
  angular.module("ariaNg").factory("serviceName", [
      "$q",
      "otherService",
      function ($q, otherService) {
          // implementation
      }
  ]);
  ```

- Use `$q` for promises (not native Promises)
- Use `$timeout` instead of native `setTimeout`

### Error Handling
- Use `tryFn` from `nice-try` package for safe function calls that might fail
- Handle aria2 RPC errors via `aria2RpcErrors` constants
- Log errors using `ariaNgLogService`

### CSS/LESS
- Source styles in `src/styles/**/*.css`
- No preprocessor configured (plain CSS)
- Uses AdminLTE, Bootstrap 3.4.1

### HTML/Views
- Views in `src/views/**/*.html`
- Minified and compiled to Angular template cache during build
- Use `ariaNg` translation functions (`{{"key" | translate}}`)

### Build Process
- Gulp tasks defined in `gulpfile.js`
- Build outputs to `.tmp/` then `dist/`
- Uses `gulp-terser` for JS minification (ecma: 2020)
- Uses `gulp-cssnano` for CSS minification

### Capacitor Integration
- Android support via `@capacitor/core` v8.2.0
- Capacitor-specific code in `src/scripts/capacitor.js` (compiled to `js/capacitor.min.js`)

## Common Tasks

### Adding a New Language
1. Add language config to `src/scripts/config/languages.js`
2. Copy `i18n/en.sample.txt` to `src/langs/<langcode>.txt`
3. Translate strings

### Adding a New aria2 Option
1. Edit `src/scripts/config/aria2Options.js`
2. Define options with proper sections and types

### Modifying the UI
1. Edit HTML in `src/` or `src/views/`
2. Edit CSS in `src/styles/`
3. Run `gulp serve` for live preview

## Important Files
- `gulpfile.js` - Build orchestration
- `package.json` - Dependencies and scripts
- `src/scripts/core/app.js` - Main Angular module definition
- `src/scripts/config/aria2Options.js` - aria2 configuration options
- `src/scripts/services/aria2RpcService.js` - Core RPC communication