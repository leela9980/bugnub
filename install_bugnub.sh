#!/bin/bash

reboot_wait() {
    adb reboot
    echo "⏳ Waiting for device to reboot..."
    adb wait-for-device
     # Optional: give a few seconds for system services to stabilize
    echo "⌛ Waiting additional 10 seconds for full boot..."
    sleep 10
    adb root; adb remount
}

adb root
adb remount

# Get verity mode from device
verity_mode=$(adb shell getprop ro.boot.veritymode | tr -d '\r')
echo "Device verity mode: $verity_mode"

if [[ "$verity_mode" != "disabled" ]]; then
    echo "⚠️ dm-verity not fully disabled — reboot required"
    reboot_wait
else
    echo "✅ dm-verity is disabled — no reboot needed"
fi

adb push bugnub_dump.sh /vendor/bin/bugnub_dump.sh
adb shell chmod +x /vendor/bin/bugnub_dump.sh
adb push init.bugnub.rc /vendor/etc/init/init.bugnub.rc
adb shell "mkdir /system/priv-app/bugnub"
adb push bugnub.apk /system/priv-app/bugnub/bugnub.apk
adb shell sync
reboot_wait
adb shell appops set com.leela.bugnub SYSTEM_ALERT_WINDOW allow
reboot_wait
# data present in /sdcard/bugnub
