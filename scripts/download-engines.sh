#!/usr/bin/env bash
# ---------------------------------------------------------------------------
# Smalix engine downloader (Linux / macOS)
# Fetches the large engine jars from the GitHub Release into bin/.
# These are NOT stored in git (jadx-all.jar alone is >100MB).
# ---------------------------------------------------------------------------
set -euo pipefail

# EDIT THESE two lines after you create your repo + release:
REPO="USERNAME/Smalix"       # e.g. dedsec/Smalix
TAG="engines-v3.0"           # the Release tag that holds the jars

BASE="https://github.com/${REPO}/releases/download/${TAG}"
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BIN="${HERE}/bin"
mkdir -p "${BIN}/dex2jar"

get () {  # get <url> <dest>
  local url="$1" dest="$2"
  if [[ -f "$dest" ]]; then echo "  ✓ $(basename "$dest") already present"; return; fi
  echo "  ↓ $(basename "$dest")"
  curl -fL# "$url" -o "$dest"
}

echo "Downloading Smalix engines from ${REPO}@${TAG} ..."
get "${BASE}/jadx-all.jar"         "${BIN}/jadx-all.jar"
get "${BASE}/apktool.jar"          "${BIN}/apktool.jar"
get "${BASE}/uber-apk-signer.jar"  "${BIN}/uber-apk-signer.jar"

# dex2jar bundle (single archive is easiest):
if [[ ! -f "${BIN}/dex2jar/dex-tools-v2.4.jar" ]]; then
  echo "  ↓ dex2jar.zip"
  curl -fL# "${BASE}/dex2jar.zip" -o "${BIN}/dex2jar.zip"
  unzip -o "${BIN}/dex2jar.zip" -d "${BIN}/dex2jar" >/dev/null
  rm -f "${BIN}/dex2jar.zip"
fi

echo "Done. Engines are in ${BIN}"
