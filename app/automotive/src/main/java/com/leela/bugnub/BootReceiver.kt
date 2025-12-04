package com.leela.bugnub

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Process
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED){
            Log.d("Bootreceiver", "BOOT_COMPLETED received , starting service")
            if (android.os.Process.myUid() == Process.SYSTEM_UID) {
                Log.w("FloatingIconService", "Skipping service for user-0")
                return
            }
            val serviceIntent = Intent(context, FloatingIconService::class.java)
            context.startForegroundService(serviceIntent)
        }
    }
}