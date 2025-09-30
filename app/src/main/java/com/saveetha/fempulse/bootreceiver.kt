
package com.saveetha.fempulse

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Re-schedule drink water reminders
            val drinkWater = DrinkWater(context)
            drinkWater.init() // will read prefs and reschedule

            // will read prefs and reschedule
        }
    }
}

