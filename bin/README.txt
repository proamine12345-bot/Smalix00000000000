This folder holds the Smalix engine jars at runtime:

    apktool.jar
    uber-apk-signer.jar
    jadx-all.jar
    dex2jar/   (bundle of jars)

They are NOT stored in git (jadx-all.jar alone is >100MB, GitHub's limit).
Fetch them once from the GitHub Release:

    Windows:      scripts\download-engines.bat
    Linux/macOS:  ./scripts/download-engines.sh

You can also drop in your own newer versions — the filename just has to
contain "apktool", "signer", or "jadx".
