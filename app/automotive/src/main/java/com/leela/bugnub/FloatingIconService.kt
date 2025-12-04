package com.leela.bugnub

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.core.app.NotificationCompat
import android.os.Process;
class FloatingIconService : Service(), View.OnTouchListener {

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: ImageView
    private val originalIcon = R.drawable.bugnub
    private val clickedIcon = R.drawable.tick
    private val dumpSnapshot = DumpSnapshot()
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f

    override fun onCreate() {
        super.onCreate()
//        if (Process.myUid() == Process.SYSTEM_UID) {
//            Log.w("FloatingIconService", "Skipping service for user-0")
//            stopSelf()
//            return
//        }
        createNotificationChannel()
        startForeground(1, createNotification())
       try {

           // ✅ Get the secondary display
           val displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
           val displays = displayManager.displays

           // Usually index 0 = main display, index 1 = secondary (e.g. passenger or cluster)
           val secondaryDisplay = if (displays.size > 1) displays[1] else displays[0]

           // ✅ Create a WindowManager for that display
           val contextForDisplay = createDisplayContext(secondaryDisplay)
           Log.d("FloatingIconService", "Using display: ${secondaryDisplay.name} with id ${secondaryDisplay.displayId}")

           windowManager = contextForDisplay.getSystemService(Context.WINDOW_SERVICE) as WindowManager
           floatingView = ImageView(contextForDisplay).apply {
               setImageResource(originalIcon)
               layoutParams = ViewGroup.LayoutParams(dpToPx(contextForDisplay, 80), dpToPx(contextForDisplay, 80))  // width, height in dp
               scaleType = ImageView.ScaleType.CENTER_INSIDE
               setPadding(10, 10, 10, 10) // optional padding for rounded edges
               setOnClickListener {
                   // TODO: trigger log capture or diagnostic action
                    isEnabled = false
                   setImageResource(clickedIcon)
                   dumpSnapshot.takeSnapshot();
                   // revert after 1 second
                   Handler(Looper.getMainLooper()).postDelayed({
                       setImageResource(originalIcon);
                       isEnabled = true
                   }, 10000)
               }
           }

           floatingView.setOnTouchListener(this);

           val params = WindowManager.LayoutParams(
               WindowManager.LayoutParams.WRAP_CONTENT,
               WindowManager.LayoutParams.WRAP_CONTENT,
               // ✅ Since this is a system app, TYPE_APPLICATION_OVERLAY works fine
               // but you can also use TYPE_STATUS_BAR_SUB_PANEL if you need higher Z-order
               WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
               WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
               PixelFormat.TRANSLUCENT
           ).apply {
               gravity = Gravity.TOP or Gravity.END
               x = 50
               y = 400
           }

           windowManager.addView(floatingView, params)
       }catch (e: Exception){
           Log.e("FloatingIconService", "Error creating floating icon: $e" )
       }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::floatingView.isInitialized) {
            windowManager.removeView(floatingView)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "bug_nub_channel",
                "Bug Nub Overlay Service",
                NotificationManager.IMPORTANCE_MIN
            ).apply {
                description = "Keeps bug nub overlay active"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, "bug_nub_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Bug Nub Running")
            .setContentText("Tap the icon to capture logs or screenshots")
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setOngoing(true)
            .build()
    }

    private fun dpToPx(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val params = floatingView.layoutParams as WindowManager.LayoutParams
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = params.x
                initialY = params.y
                initialTouchX = event.rawX
                initialTouchY = event.rawY
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = (event.rawX - initialTouchX).toInt()
                val dy = (event.rawY - initialTouchY).toInt()
                // Only move if drag distance is significant
                if (dx != 0 || dy != 0) {
                    params.x = initialX + dx
                    params.y = initialY + dy
                    windowManager.updateViewLayout(floatingView, params)
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                val dx = (event.rawX - initialTouchX).toInt()
                val dy = (event.rawY - initialTouchY).toInt()
                // If the user did not drag, treat as click
                if (Math.abs(dx) < 10 && Math.abs(dy) < 10) {
                    v?.performClick()
                }
                return true
            }
        }
        return false
    }
}
