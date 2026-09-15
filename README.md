<div align="center">

# 🛡️ SMALIX 3.0

### APK Decompiler & Smali Editor

**Decompile APKs to real Java, edit the exact Smali, then rebuild · sign · zipalign — all from one desktop app.**

_by [DedSec Cyber Security Research Institute](https://dedseec.com)_

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-17%2B-orange.svg)](https://adoptium.net)
[![Platform](https://img.shields.io/badge/Platform-Windows%20%7C%20Linux%20%7C%20macOS-lightgrey.svg)]()
[![Release](https://img.shields.io/github/v/release/USERNAME/Smalix?include_prereleases)](../../releases)

</div>

---

> ⚠️ **Ethical use only.** Smalix is for learning and for apps you own or are **authorized** to test (your own apps, apps you have written permission for, and educational/CrackMe practice apps). Cracking someone else's app, bypassing paid features, or piracy is **forbidden** and may be illegal. See [Ethical Use](#-ethical-use).

---

## ✨ What Smalix does

Smalix is a desktop program that:

- **Decompiles APKs to real Java** (JADX engine) — shown in the left view.
- **Shows the exact, editable Smali** — right view, with two-way sync highlighting.
- **Rebuilds + signs + zipaligns** your edits back into an installable APK.
- **Analyzes & deobfuscates** (online/offline detection, secrets, XOR/Base64) — and *emulates the app's own decryptor* to recover the exact plaintext.

---

## 🖥️ New UI (v3.0)

- **Left icon rail:** Files · Analyze · Deobf · Patch · Graph · Console · Setup — every feature in a clean section.
- **Top bar:** emblem + breadcrumb + status chips (ONLINE/OFFLINE, Obfuscated, ★ entry), plus actions: Open APK · Save · Build ▾ (Rebuild / Sign+Align / Rebuild+Sign / fast / random / Clean) · theme toggle · About · search box.
- **Center:** dual editor (Java | Smali) with synced highlight.
- **Right:** Inspector dock — Explain · Patch · Patch Lib · Calls · Called by · Graph.
- **Themes:** Dark & Light (toggle from the top bar or Setup).
- **Responsive:** heavy work (decode / JADX / analysis / deobfuscation / rebuild) runs in the background — the UI never hangs.

---

## 🚀 Quick start

### Windows

1. Download the release and extract the whole **`Smalix`** folder.
2. Double-click **`Smalix.bat`** (use **`Smalix-console.bat`** to see logs).
3. **Java 17+** required. Don't have it? Install [Temurin JDK/JRE](https://adoptium.net). Smalix auto-detects Java from `JAVA_HOME`, `PATH`, Program Files\Java, Adoptium, Microsoft, Zulu, and Android Studio's JBR.

### Linux / macOS

```bash
chmod +x Smalix.sh
./Smalix.sh          # requires Java 17+
```

---

## ⚙️ Engines (`bin/` folder)

Smalix auto-detects its engines from the `bin/` folder:

| Engine | File | Purpose |
|---|---|---|
| **Apktool** | `bin/apktool.jar` | decode + rebuild |
| **uber-apk-signer** | `bin/uber-apk-signer.jar` | zipalign + sign |
| **JADX** | `bin/jadx-all.jar` | real Java decompilation |
| **dex2jar / jar2dex** | `bin/dex2jar/` | APK ⇄ .jar conversion |

> 📦 **These jars are distributed via [GitHub Releases](../../releases), not committed to git** (they're large third-party binaries). After cloning, run the downloader once:
>
> **Windows:** `scripts\download-engines.bat`
> **Linux/macOS:** `./scripts/download-engines.sh`
>
> You can swap in newer versions any time — the filename just needs to contain `apktool`, `signer`, or `jadx`.

---

## 🧰 Features

- **Open APK** or **drag & drop** a `.apk` onto the program.
- **Left = decompiled Java** (JADX), syntax-highlighted, read-only. **Right = Smali**, editable, highlighted.
- Click any class/method → both views load and the Smali scrolls to that method.
- **Two-way highlight:** clicking a line puts a clear current-line band (cyan accent) in both views and cross-highlights the matching Smali ↔ Java method.
- **★ Entry** button jumps straight to the MAIN/LAUNCHER activity's `onCreate`.
- **Progress bar** during decompile / analysis / rebuild.
- **Explain** tab: what each line means (field read/write, boolean `0x0`/`0x1`, invoke target internal/external, if-conditions, entry-point `onCreate`).
- **Calls / Called by / Search:** cross-class navigation.

### 🔎 Auto analysis (on load)

- **Report:** package, min/target SDK, DEX/class/method stats, manifest security (debuggable, allowBackup, cleartextTraffic), permissions, exported components, signing certificate (keytool), and a **Connectivity Verdict** — ONLINE vs OFFLINE, network/Firebase/local-DB usage, and online-vs-offline validation guess, with URL/IP evidence.
- **Strings:** every hardcoded string with its location (click to jump).
- **Secrets:** auto-detected API keys, tokens, JWTs, IPs, DB URLs, Firebase, private keys.
- **APIs:** where Network / Crypto / Reflection / `Runtime.exec` / dynamic-load / Storage / WebView / Device-ID are used.
- **Images:** preview `res/` + `assets/` images.

### 🧩 Obfuscation & deobfuscation

- Auto-detection on load: name obfuscation, string encryption (XOR / decryptor methods), Base64, reflection, control-flow flattening. Status bar shows **● Obfuscated / ● Not obfuscated**.
- **Deobfuscate** (accurate, no guessing):
  1. **Emulation** — runs the app's *own* decryptor method in a small Smali interpreter with its real arguments, recovering the **exact** plaintext (DexGuard/DashO/Allatori/DProtect-style schemes). Static-field / cross-class keys (`<clinit>`) resolve automatically.
  2. **Literal decoders** (reliable only): Base64 and single-byte XOR where the key comes from code (`xor-int/lit8`), position-XOR — all verified against keywords/URLs.
- Decoded strings appear in the **Deobfuscation** tab and inline in the Smali view as `# ⇒ decoded: ...`. Anything that can't be emulated is left blank rather than wrongly guessed.

### 🏷️ ProGuard / R8 names

ProGuard/R8 only rename symbols. Put your build's `mapping.txt` next to the APK and Smalix restores original class names in the Report (obf → original). _For your own / authorized apps._

### 📤 Export & convert

- **Java project → Android Studio (JADX):** full Gradle project scaffold (sources + manifest + res + build files).
- **Smali project (apktool):** always-reliable rebuildable decode folder.
- **This .java / .smali file:** save the current view.
- **APK → .jar (dex2jar)** and **.jar → .dex (jar2dex)**.

### 🔐 Obfuscator (protect your own app)

Obfuscate + rebuild + sign without breaking functionality (safe techniques only): value-preserving string encryption, junk (nop) injection, debug-info stripping. Levels: **Light · Medium (default) · Aggressive**. Outputs a ready `<name>-obf.apk` or a rebuildable Smali project. _Symbol renaming is intentionally not done — it breaks reflection/JNI/manifest._

### 🎯 Auto-patch scan & patch assistant

One-click scan lists license/premium gates, LVL/billing, root/anti-emulator/anti-debug/anti-Frida, signature/tamper, SSL pinning, native/packers — each with category, class→method, confidence, suggested action, and "Go to". Apply selected, or Apply + Rebuild + Sign. Server-enforced (Play Integrity/SafetyNet) and native checks are marked inspect-only. Patch Assistant: force-return TRUE/FALSE/NULL/0/void, invert `if-*`, set const `0x0↔0x1`, NOP a call, comment a line.

### 🔍 Advanced find (Ctrl+F)

Scope Smali / Java / whole APK (all classes + XML), match-case, regex, find-all, replace / replace-all, replace-all-in-APK (background). Whole-APK scope searches Smali + AndroidManifest + `res/*.xml` + `apktool.yml`.

### ↩️ Undo & 🕸️ Graph

- Smali editor: Ctrl+Z / Ctrl+Y (redo) back to the originally-loaded Smali.
- **Graph:** IDA/x64dbg-style call graph of the opened class, realtime updates, click to navigate.

---

## 🔨 Build / sign buttons

| Button | Action |
|---|---|
| **Rebuild** | build APK from the edited project |
| **Sign + Align** | zipalign + sign via uber-apk-signer |
| **Rebuild + Sign** | both in one click (installable APK) |
| **Clean** | delete the temp folder now |

- **fast (code-only)** *(default ON)* — rebuilds only Smali/code (resources from original). Most reliable. Turn OFF if you edit `AndroidManifest`/`strings.xml` (full resource rebuild needs the apktool framework, installed automatically on first use).
- **random key** — new keystore per build if `keytool` is found, else debug key (`--allowResign`).

Temp folders (`smalix_...` in system temp) auto-clean on new open, on **Clean**, and on exit.

---

## 🧑‍⚖️ Ethical use

Smalix is **only** for:

- your own apps,
- apps you have **written permission** to test,
- practice / CrackMe (educational) apps.

Cracking others' apps, bypassing paid features, or piracy is forbidden and may be illegal.

---

## 🙏 Credits (open-source engines)

- **[JADX](https://github.com/skylot/jadx)** — Apache-2.0
- **Apktool** — Apache-2.0
- **baksmali/smali** — BSD (inside apktool)
- **[uber-apk-signer](https://github.com/patrickfav/uber-apk-signer)** — Apache-2.0

Smalix (GUI) — **DedSec Cyber Security Research Institute** · [dedseec.com](https://dedseec.com)

See [NOTICE](NOTICE) for full attributions.

---

## 📄 License

Licensed under the [Apache License 2.0](LICENSE).
