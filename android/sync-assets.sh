#!/usr/bin/env bash
# ينسخ ملفات الموقع من جذر المستودع إلى أصول تطبيق الأندرويد
set -e
cd "$(dirname "$0")/.."
DEST="android/app/src/main/assets"
mkdir -p "$DEST"
for f in index.html world.js sw.js manifest.webmanifest icon.svg icon-192.png icon-512.png icon-maskable-512.png favicon-32.png apple-touch-icon.png azan_nasser.mp3; do
  if [ -f "$f" ]; then cp -f "$f" "$DEST/"; echo "copied $f"; else echo "MISSING $f"; fi
done
ls -la "$DEST"
