#!/usr/bin/env bash
# Abre o emulador do zero (sem o estado salvo), instala a versão atual do app e o abre.
#
#   ./testar.sh          mantém os dados do app (ofensiva, novenas iniciadas…)
#   ./testar.sh --limpo  apaga o app antes, como numa primeira instalação
set -euo pipefail

SDK="${ANDROID_HOME:-$HOME/Android/Sdk}"
ADB="$SDK/platform-tools/adb"
AVD="fediaria"
PACOTE="br.app.fediaria.debug"
cd "$(dirname "$0")"

if "$ADB" get-state >/dev/null 2>&1; then
    echo "→ Fechando o emulador aberto…"
    "$ADB" emu kill >/dev/null 2>&1 || true
    while "$ADB" get-state >/dev/null 2>&1; do sleep 1; done
fi

echo "→ Ligando o emulador do zero (sem Quick Boot)…"
nohup "$SDK/emulator/emulator" -avd "$AVD" -no-snapshot-load -no-snapshot-save >/dev/null 2>&1 &

echo "→ Compilando o app enquanto o emulador liga…"
./gradlew :androidApp:assembleDebug -q --console=plain

"$ADB" wait-for-device
until [ "$("$ADB" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" = "1" ]; do sleep 2; done
echo "→ Emulador pronto."

if [ "${1:-}" = "--limpo" ]; then
    echo "→ Removendo a instalação anterior…"
    "$ADB" uninstall "$PACOTE" >/dev/null 2>&1 || true
fi

echo "→ Instalando…"
./gradlew :androidApp:installDebug -q --console=plain
"$ADB" shell am start -n "$PACOTE/br.app.fediaria.MainActivity" >/dev/null
echo "✓ FéDiária aberto no emulador."
