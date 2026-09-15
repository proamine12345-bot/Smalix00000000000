#!/usr/bin/env bash
cd "$(dirname "$0")"
JV="java"
if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then JV="$JAVA_HOME/bin/java"; fi
if ! command -v "$JV" >/dev/null 2>&1; then echo "Java 17+ chahiye: https://adoptium.net"; exit 1; fi
exec "$JV" -jar "$(dirname "$0")/Smalix.jar"
