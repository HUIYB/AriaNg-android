alias op := open-plugins

sync:
    @cd ./aria2-plugin; pnpm run build
    @gulp clean build
    @npx cap sync

web-build:
    @gulp clean build

build: sync && build-ns

build-ns:
    npx cap build android

open:
    npx cap open

open-plugins:
    cd ./aria2-plugin; npx cap open android

run: sync && run-ns

run-ns: && log
    npx cap run android

log:
    #!/usr/bin/env bash
    PIDS=$(adb shell pidof com.ariang.app)
    adb logcat --pid=$PIDS

log-t tag:
    adb logcat -s "{{ tag }}"

server:
    @gulp serve

update:
    @pnpm i @capacitor/core @capacitor/android
    @pnpm i -D @capacitor/cli
    @pnpm install @capacitor/preferences

test:
    @cd ./aria2-plugin; pwd

preview: sync && preview-ns

preview-ns:
    python -m http.server --directory dist
