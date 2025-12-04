#!/bin/sh

# Generate a timestamped folder
foldername=$(date +%Y%m%d%H%M%S)
abs_path="/sdcard/bugnub/$foldername"
mkdir -p "$abs_path"


# Take screenshots of both displays
screencap -d 0 -p "$abs_path/screencap0.png"
screencap -d 1 -p "$abs_path/screencap1.png"

build_flavor=$(getprop ro.build.flavor)
if [[ "$build_flavor" == ucc* ]]; then
    display_0=129
    display_1=3
else
    display_0=4630946422468947329
    display_1=4630947246165612418
fi

screenrecord --display-id $display_0 --time-limit 10 "$abs_path/screenrecord_icr.mp4"
screenrecord --display-id $display_1 --time-limit 10 "$abs_path/screenrecord_cid.mp4"

lspci > "$abs_path/lspci.txt"

lsmod > "$abs_path/lsmod.txt"

dmesg > "$abs_path/kernel.txt"

iw dev > "$abs_path/iwdev.txt"
iw reg get > "$abs_path/iwreg_get.txt"

getprop > "$abs_path/getprop.txt"

#dumpsys vhal > "$abs_path/dumpsys_vhal.txt"

cp -r /data/misc/bluetooth "$abs_path/bluetooth"

cp -r /data/misc/bluedroid "$abs_path/bluedroid"

mkdir -p "$abs_path/logd"
logcat -b all -d > "$abs_path/logd/logcat_all.txt"
cp -r /data/misc/logd/* "$abs_path/logd/"
#cp -r /data/vendor/aplogs "$abs_path/aplogs"

cp -r /data/vendor/wifi/hostapd/hostapd_wlan1.conf "$abs_path/hostapd_wlan1.conf"

cp -r /data/tombstones "$abs_path/tombstones"
cp -r /data/anr "$abs_path/anr"

dumpsys SurfaceFlinger > "$abs_path/Surfaceflinger.txt"

dumpsys gfxinfo > "$abs_path/gfxinfo.txt"

dumpsys meminfo > "$abs_path/meminfo.txt"
cp -r /data/misc/projection "$abs_path/projection"
logcat -b crash -d -t 7000 > "$abs_path/crash_log.txt"

#rm -rf /data/misc/logs/*
echo "Bug capture completed in /sdcard/bugnub/$foldername"
sync