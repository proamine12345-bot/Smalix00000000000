========================================================
  SMALIX 3.0  —  APK Decompiler & Smali Editor
  by DedSec Cyber Security Research Institute
  dedseec.com
========================================================

Smalix ek desktop program hai jo APK ko:
  • REAL JAVA me decompile karta hai (JADX engine) — left view
  • uska EXACT SMALI dikhata hai (editable) — right view
  • edit karke wapas REBUILD + SIGN + ZIPALIGN kar deta hai
  • analyze + deobfuscate karta hai (online/offline, secrets, XOR/Base64,
    aur app ke apne decryptor ko EMULATE karke exact plaintext)

--------------------------------------------------------
0) NAYA UI (v3.0)
--------------------------------------------------------
  • Left ICON RAIL: Files · Analyze · Deobf · Patch · Graph · Console · Setup
        (saare features saaf-suthre sections me organized)
  • Top bar: emblem + breadcrumb + status chips (ONLINE/OFFLINE,
    Obfuscated, ★ entry), aur actions: Open APK · Save · Build ▾
    (Rebuild / Sign+Align / Rebuild+Sign / fast / random / Clean) ·
    theme toggle · About · search box.
  • Center: dual editor (Java | Smali) with sync highlight.
  • Right: INSPECTOR dock — Explain · Patch · Patch Lib · Calls ·
    Called by · Graph (pills se switch).
  • THEMES: Dark aur Light dono — top bar ke toggle ya Setup se badlein.
  • Heavy kaam (decode / JADX / analysis / deobfuscation / rebuild)
    background me chalta hai — UI hang nahi hoti, responsive rehti hai.

--------------------------------------------------------
1) CHALANE KA TARIQA (Windows)
--------------------------------------------------------
  1. Zip ko extract karein (poora "Smalix" folder).
  2. Smalix.bat par double-click karein.
     (Logs/console dekhne ke liye: Smalix-console.bat)
  3. Java chahiye — Java 17 ya us se naya recommended.
     Nahi hai to: https://adoptium.net  (Temurin JDK/JRE)
     Smalix khud Java dhundh leta hai (JAVA_HOME, PATH,
     Program Files\Java, Adoptium, Microsoft, Zulu, Android Studio jbr).

  Linux/Mac:  ./Smalix.sh   (Java 17+ hona chahiye)

--------------------------------------------------------
2) ENGINES (bin folder)
--------------------------------------------------------
  Smalix "bin" folder se engines KHUD detect karta hai:
     bin\apktool.jar            (decode + rebuild)
     bin\uber-apk-signer.jar    (zipalign + sign)
     bin\jadx-all.jar           (real Java decompilation)

  Ye teeno pehle se bin\ me shaamil hain — kaam out-of-the-box.
  Chahein to inhe naye version se replace kar sakte hain (naam
  me "apktool", "signer", "jadx" hona chahiye).

--------------------------------------------------------
3) FEATURES
--------------------------------------------------------
  • Open APK ya DRAG & DROP .apk program par.
  • Left  = decompiled Java (JADX), syntax-highlighted, read-only.
    Right = smali (editable), highlighted.
  • Kisi class/method par click:
        - dono views load
        - smali us method par scroll
  • TWO-WAY HIGHLIGHT (fixed & bright): jis line par click karo
    dono views me ek clear current-line band (cyan accent) aata hai,
    aur smali<->Java ka wahi method cross-highlight ho jata hai.
  • "\u2605 Entry" button: seedha app ke MAIN/LAUNCHER activity ke
    onCreate (entry point) par le jata hai.
  • PROGRESS BAR: decompile / analysis / rebuild ke waqt chalti hai.
  • EXPLAIN tab: har line ka theek matlab (field read/write,
    boolean 0x0/0x1, invoke target internal/external, if-condition,
    entry-point onCreate).
  • CALLS / CALLED BY / SEARCH: cross-class navigation.

  == AUTO ANALYSIS (APK load hote hi) ==
  • REPORT tab: package, min/target SDK, DEX/class/method stats,
    manifest security (debuggable, allowBackup, cleartextTraffic),
    permissions, exported components, signing certificate (keytool),
    aur sab se ahem:
        CONNECTIVITY VERDICT \u2014 app ONLINE hai ya OFFLINE,
        network calls / Firebase / local DB, aur online-vs-offline
        validation ka andaza, EVIDENCE (URL/IP) ke saath.
  • STRINGS tab: saari hardcoded strings (URL, keys, messages)
    location ke saath \u2014 click = us line par jao.
  • SECRETS tab: auto-detected API keys, tokens, JWT, IP, DB URLs,
    Firebase, private keys.
  • APIs tab: kahan Network / Crypto / Reflection / Runtime.exec /
    Dynamic-load / Storage / WebView / Device-ID use ho raha hai.
  • IMAGES tab: res/ + assets/ ki tasveerein preview karein.

  == OBFUSCATION + DEOBFUSCATION ==
  • Load par auto-detection (Report tab me OBFUSCATION section):
        name obfuscation, string encryption (XOR / decryptor methods),
        Base64 usage, reflection, control-flow flattening.
  • Status bar par "\u25CF Obfuscated" / "\u25CF Not obfuscated".
  • "Deobfuscate" button (top-right) \u2014 ACCURATE, koi guessing nahi:
        1) EMULATION (asli tareeqa): Smalix app ke APNE decryptor
           method ko ek chhote smali interpreter me CHALATA hai (aur
           us ke asli arguments ke saath). Jo string app khud banati,
           wahi EXACT plaintext milta hai (DexGuard/DashO/Allatori/
           DProtect jaisi getChars/decrypt(String,int) schemes).
           Static fields / doosri classes me chhupi keys (<clinit>)
           bhi khud resolve ho jati hain.
        2) LITERAL decoders (sirf reliable): Base64, aur single-byte
           XOR jahan key CODE se milti hai (xor-int/lit8), position-XOR
           (canonical). Sab keyword/URL se verify \u2014 galat guess nahi.
     Decoded strings "Deobfuscation" tab me (emulated = exact) aur
     smali view me inline "# \u21D2 decoded: ..." comments ke tor par.
  • Jo emulate na ho sake (native/runtime-only/complex), uska koi
    galat guess NAHI dikhaya jata (khali chhod diya jata hai).

  == PROGUARD / R8 NAMES (original form) ==
  • ProGuard/R8 sirf naam badalte hain (string encrypt nahi karte).
    Asli naam wapas laane ka theek tareeqa = build ki "mapping.txt".
  • Apni app ki mapping.txt ko APK ke saath (same folder) rakhein \u2014
    Smalix use load karke Report me original class naam dikha deta
    hai (obf \u2192 original). (Aap ki apni/authorized app ke liye.)

  == EXPORT & CONVERT (Export menu, top bar) ==
  • Java project -> Android Studio (JADX): poore APK ko ek Gradle
    project me export (sources + manifest + res + build.gradle /
    settings.gradle scaffold) -> Android Studio me import ho jata hai.
        NOTE: decompiled Java 100% error-free guarantee nahi (kisi bhi
        decompiler ki limitation). Simple apps build ho jate hain;
        complex apps me kuch manual fixes lag sakte hain. HAMESHA
        reliable rebuild ke liye niche wala 'Smali project' use karein.
  • Smali project (apktool, rebuildable): apktool decode folder export
    -> ye hamesha reliably rebuild hota hai (apktool b).
  • This .java file  : Java view ka mojooda source save.
  • This .smali file : editor ka mojooda smali save.
  • APK -> .jar (dex2jar): dex ko Java bytecode .jar me (JD-GUI etc.).
  • .jar -> .dex (jar2dex): wapas .dex.
        (dex2jar/jar2dex ke jars 'bin/dex2jar/' me pehle se shaamil.)

  == OBFUSCATOR (Obfuscate menu, top bar) ==
  Apni app ko obfuscate karke (protect) rebuild+sign karta hai.
  Functionality NAHI todta (safe techniques only):
    - String encryption: har const-string encrypt + chhota decryptor
      inject (value-preserving + .intern() -> reflection/resource/'=='
      sab theek rehte hain). Decompiler ab plain strings nahi dikhata.
    - Junk (nop) injection: bytecode/signature badalta hai, behaviour same.
    - Debug-info strip: .line/.source/.local/.param/.prologue hata deta hai.
  Levels: Light (strip+junk) · Medium (+string encryption, default) ·
          Aggressive (+ extra junk).
  Do output:
    - Obfuscate -> Rebuild + Sign  (ready APK: <name>-obf.apk)
    - Obfuscate -> Export smali project (khud rebuild kar sakte hain)
  NOTE: renaming (classes/methods) jaan-boojh kar NAHI kiya (wo aksar
  apps todta hai: reflection/JNI/manifest). Ye set har type ki APK par
  safe rehta hai. Bahut bade methods (bohat saari strings) par size
  barh sakta hai.

  == AUTO-PATCH SCAN (Patch Library -> "Auto-Scan", ya Patch panel) ==
  Poori app ek click me scan karke SAARE patch-points ki badi list deta hai:
    - License/Premium boolean gates (isPremium/isPro/checkLicense/validate...)
    - Google Play Licensing (LVL Policy.allowAccess), Billing wrappers
    - Root (RootBeer + su/magisk indicators), Anti-emulator, Anti-debug,
      Anti-Frida/Xposed, Signature/tamper (GET_SIGNATURES), SSL pinning
      (checkServerTrusted / CertificatePinner.check / HostnameVerifier),
      native (.so) methods, packers.
  Har finding par: category, class->method, [confidence High/Med/Low],
  suggested action (force-return true/false, no-op, ya inspect), checkbox
  aur "Go to". Aap CHOOSE karein:
    - Select High / Select all / Clear
    - Apply selected            (sirf patch)
    - Apply + Rebuild + Sign     (ready patched APK)
  Server-enforced (Play Integrity/SafetyNet) aur native checks sirf
  "inspect" (safe) — inhe auto-patch nahi kiya jata (client patch bekaar).
   Research: MASTG/MASVS + RootBeer/LVL/OkHttp docs ke mutabiq.

  == ADVANCED FIND (Ctrl+F, dono views + poora APK) ==
  Java view ya Smali editor me Ctrl+F -> find bar. Options:
    - Scope: Smali (right) / Java (left) / Whole APK (all classes + xml)
    - Aa (match case), .* (regex)
    - Prev / Next (baar-baar), Find all (sab highlight + count)
    - Replace / Replace all (smali editor)
    - Replace all in APK (background, saari smali files) -> phir Rebuild
    - Esc = close
  Whole-APK scope smali + AndroidManifest + res/*.xml + apktool.yml
  sab me search karta hai (heavy kaam background me — UI hang nahi hoti).

  == UNDO (smali editor) ==
  • Ctrl+Z = undo, Ctrl+Y / Ctrl+Shift+Z = redo. Baar-baar Ctrl+Z se
    APK load ke waqt wali ORIGINAL smali tak wapas.
  • APK load par saari classes KHUD nahi khulti — packages dikhte hain,
    aap khud class kholte hain (methods click par).


  == GRAPH (IDA / x64dbg style) ==
  • Sirf OPENED class ka call-graph, arrows ke saath: selected
    method se kaunse methods/other-class calls hote hain (flow).
  • Selected method cyan highlight; entry onCreate amber; doosri
    app-class calls (blue) par click = wahan navigate.
  • Jab aap koi method/line select karte hain to graph realtime
    update hota hai.

--------------------------------------------------------
4) PATCHING  (Patch Assistant + Patch Library)
--------------------------------------------------------
  PATCH ASSISTANT (line/method aware, 1-click Apply):
    • Force return TRUE / FALSE / NULL / 0 / return-void \u2014
      poore method ko neutralize (bypass) karne ka sab se aam tareeqa
      (root check, SSL pinning, emulator, signature, license).
    • Invert koi bhi if-* condition (eqz/nez/eq/ne/lt/ge/gt/le).
    • const value 0x0<->0x1 ya custom set.
    • NOP a call, ya line ko comment-out.

  PATCH LIBRARY (common + complex \u2014 detect phir neutralize):
    • Anti-Root detection
    • SSL Pinning
    • Anti-Emulator
    • Anti-Debug
    • Signature / Tamper check
    • Anti-Screenshot / Record (FLAG_SECURE)
    • Runtime.exec ("su") checks
    Har button poori app me us protection ke likely methods dhundhta
    hai (Search me candidates), phir un par jaake Patch Assistant se
    "Force return" se neutralize karein.

    \u26A0 SIRF apni / authorized / practice app par. Doosron ki app,
    piracy ya paid-bypass MANA hai.

--------------------------------------------------------
5) REBUILD / SIGN — BUTTONS
--------------------------------------------------------
  • Rebuild          -> edited project se APK banata hai
  • Sign + Align     -> uber-apk-signer se zipalign + sign
  • Rebuild + Sign   -> dono ek click me (installable APK)
  • Clean            -> temp folder abhi delete

  "fast (code-only)"  (default ON):
     Sirf smali/code rebuild hota hai (resources original se).
     Sabse RELIABLE — CrackMe practice apps par test-shuda.

  Agar aap AndroidManifest/strings.xml (resources) edit karte hain:
     "fast (code-only)" ko OFF karein taake full rebuild ho.
     Full resource rebuild ke liye apktool ka framework machine par
     hona chahiye (pehli baar apktool khud install kar leta hai).

  "random key": har build ke liye nayi keystore (agar keytool mila).
     Warna debug key (--allowResign) use hoti hai.

--------------------------------------------------------
6) TEMP FOLDER — AUTO CLEAN
--------------------------------------------------------
  Jab bhi APK open/drag karte hain, ek temp folder banta hai
  (system temp me "smalix_..."), usi me decompile hota hai.
  Ye khud delete ho jata hai:
     - naya APK open karne par
     - "Clean" button se
     - program band karne par (poora folder, contents ke saath)

--------------------------------------------------------
7) ETHICAL USE  (zaroori)
--------------------------------------------------------
  Smalix sirf SEEKHNE aur apni/authorized apps ke liye hai:
     • apni khud ki app
     • jis app ki tehreeri ijazat ho
     • practice/CrackMe (educational) apps
  Kisi doosre ki app ko crack karna, paid features bypass karna,
  ya piracy — MANA hai aur ghair-qanooni ho sakta hai.

--------------------------------------------------------
8) CREDITS (open-source engines)
--------------------------------------------------------
  • JADX               — Apache-2.0  (skylot/jadx)
  • Apktool            — Apache-2.0
  • baksmali/smali     — BSD          (apktool ke andar)
  • uber-apk-signer    — Apache-2.0  (patrickfav)

  Smalix (GUI) — DedSec Cyber Security Research Institute
  dedseec.com
========================================================
