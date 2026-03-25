alias op := open-plugins

sync:
    @cd ./aria2-plugin; pnpm run build
    gulp clean build
    npx cap sync

build: sync
    npx cap build android

open:
    npx cap open

open-plugins:
    @cd ./aria2-plugin; npx cap open

run:
    npx cap run android

server:
    @gulp serve

update:
    @pnpm i @capacitor/core @capacitor/android
    @pnpm i -D @capacitor/cli
    @pnpm install @capacitor/preferences

test:
    @cd ./aria2-plugin; pwd

preview: sync
    python -m http.server --directory dist
