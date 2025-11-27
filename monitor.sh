#!/bin/bash
# monitor_gradlew_7days - self-healing Gradle Ktor cu loguri organizate pe zile și păstrare ultimele 7 zile

# --- Configurație ---
APP_DIR="/home/salox/webapiservices/ktor-qrb-backend"
APP_CMD="./gradlew run"
APP_NAME="KtorServer"
BASE_LOG_DIR="$APP_DIR/logs"
DELAY=30              # secunde după boot
CHECK_INTERVAL=5       # secunde între verificări
MAX_LOG_SIZE=$((50*1024*1024))  # 50 MB în bytes
MAX_DAYS=7             # păstrează ultimele 7 zile de loguri

# --- Creează directorul de log principal dacă nu există ---
mkdir -p "$BASE_LOG_DIR"

# --- Delay după boot ---
sleep $DELAY

# --- Navighează în directorul aplicației ---
cd "$APP_DIR" || { echo "Nu am putut intra în $APP_DIR"; exit 1; }

echo "$(date '+%Y-%m-%d %H:%M:%S') : Pornire monitor $APP_NAME..." >> "$BASE_LOG_DIR/monitor_start.log"

# --- Loop infinit de monitorizare ---
while true; do
    # Directorul zilei curente (YYYY-MM-DD)
    TODAY=$(date +'%Y-%m-%d')
    LOG_DIR="$BASE_LOG_DIR/$TODAY"
    mkdir -p "$LOG_DIR"

    # Nume log curent
    TIMESTAMP=$(date +'%H-%M-%S')
    LOG_FILE="$LOG_DIR/ktor_$TIMESTAMP.log"

    # Verifică dimensiunea log curent și rotește dacă e prea mare
    if [ -f "$LOG_FILE" ] && [ $(stat -c%s "$LOG_FILE") -ge $MAX_LOG_SIZE ]; then
        mv "$LOG_FILE" "$LOG_DIR/ktor_$(date +'%H-%M-%S').log"
    fi

    # Rulează aplicația și repornește dacă cade
    until exec -a "$APP_NAME" $APP_CMD >> "$LOG_FILE" 2>&1; do
        echo "$(date '+%Y-%m-%d %H:%M:%S') : '$APP_CMD' a căzut. Restart..." >> "$LOG_FILE"
        sleep $CHECK_INTERVAL
    done

    # --- Curățare directoare vechi (>7 zile) ---
    find "$BASE_LOG_DIR" -mindepth 1 -maxdepth 1 -type d -mtime +$MAX_DAYS -exec rm -rf {} \;

    sleep $CHECK_INTERVAL
done


#Old version
##!/bin/bash
#until ./gradlew run; do
#    echo "'gradlew run' crashed with exit code $?. Restarting..." >&2
#    sleep 1
#done