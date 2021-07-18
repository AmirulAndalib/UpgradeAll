package net.xzos.upgradeall.server.update

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import net.xzos.upgradeall.application.MyApplication
import net.xzos.upgradeall.core.utils.FlagDelegate

class UpdateServiceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        startUpdateWorker(context)
    }

    companion object {
        private val context get() = MyApplication.context
        private val ACTION_SNOOZE = "${context.packageName}.UPDATE_SERVICE_BROADCAST"
        fun setAlarms(t_h: Int) {
            if (t_h <= 0) return
            val alarmTime: Long = t_h.toLong() * 60 * 60 * 1000
            val alarmIntent = PendingIntent.getBroadcast(
                context, 0,
                Intent(context, UpdateServiceBroadcastReceiver::class.java).apply {
                    action = ACTION_SNOOZE
                },
                PendingIntent.FLAG_UPDATE_CURRENT or FlagDelegate.PENDING_INTENT_FLAG_IMMUTABLE
            )
            val alarmManager = (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
            alarmManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + alarmTime,
                alarmTime, alarmIntent
            )
        }
    }
}
