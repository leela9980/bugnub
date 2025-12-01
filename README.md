# bugnub
This is an Android floating application that stays on top of all other apps. It is designed to generate platform-level bug reports on custom Android devices.
To produce full, comprehensive Android bug reports, the app must be installed as a privileged system application.
It can be used on any Android-based device with a display, including mobile phones, automotive infotainment systems, and Android-powered IoT devices.

### How it works:
1. When the user taps the button, the app sets a system-level property value.
2. This property change triggers an init.rc listener, which runs a script to capture the bugreport.
3. The script generates a folder in /sdcard/ using the current system timestamp and stores the complete bugreport inside it.
4. Developers can then pull the bugreport from /sdcard/ or upload it to a cloud service (e.g., Jira) to create a bug ticket.

### Installation
1. You can load the project into Android studio and generate APK or use the one already in the repo
2. run ./install_bugnub.sh script

Make modifications to install_bugnub.sh script based on your device type. If everything goes well the bugnub icon should appear on the screen.
