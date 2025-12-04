# bugnub 
<img src="https://github.com/leela9980/bugnub/blob/main/bugnub_icon.png" alt="Alt Text" width="96">
This is an Android floating application that stays on top of all other apps. It is designed to generate platform-level bug reports on custom Android devices.
To produce full, comprehensive Android bug reports, the app must be installed as a privileged system application.
It can be used on any Android-based device with a display, including mobile phones, automotive infotainment systems, and Android-powered IoT devices.

### How it works:
1. When the user taps the button, the app sets a system-level property value.
2. This property change triggers an init.rc listener, which runs a script to capture the bugreport.
3. The script generates a folder in /sdcard/ using the current system timestamp and stores the complete bugreport inside it.
4. Developers can then pull the bugreport from /sdcard/ or upload it to a cloud service (e.g., Jira) to create a bug ticket.

### Installation
1. Build the apk in AOSP using Android.bp provided
2 adb push bugnub_dump.sh /vendor/bin/bugnub_dump.sh
  adb shell chmod +x /vendor/bin/bugnub_dump.sh
  adb push init.bugnub.rc /vendor/etc/init/init.bugnub.rc
  adb shell "mkdir /system/priv-app/bugnub"
  adb push bugnub.apk /system/priv-app/bugnub/bugnub.apk
  adb shell sync
  adb reboot

Make modifications to install_bugnub.sh script based on your device type. If everything goes well the bugnub icon should appear on the screen.
