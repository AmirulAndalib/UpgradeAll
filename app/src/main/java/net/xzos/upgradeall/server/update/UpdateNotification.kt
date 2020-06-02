package net.xzos.upgradeall.server.update

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import kotlinx.coroutines.runBlocking
import net.xzos.upgradeall.R
import net.xzos.upgradeall.application.MyApplication.Companion.context
import net.xzos.upgradeall.core.oberver.Informer
import net.xzos.upgradeall.core.oberver.Observer
import net.xzos.upgradeall.core.server_manager.UpdateManager
import net.xzos.upgradeall.ui.activity.MainActivity
import net.xzos.upgradeall.utils.MiscellaneousUtils

object UpdateNotification : Informer {
    private const val CHANNEL_ID = "UpdateServiceNotification"
    private val UPDATE_NOTIFICATION_ID = context.resources.getInteger(R.integer.update_notification_id)
    val UPDATE_SERVER_RUNNING_NOTIFICATION_ID = context.resources.getInteger(R.integer.update_server_running_notification_id)
    const val FINISH_UPDATE = "FINISH_UPDATE"

    private val mainActivityPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
        addNextIntentWithParentStack(Intent(context, MainActivity::class.java))
        getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private val builder = NotificationCompat.Builder(context, CHANNEL_ID).apply {
        setContentTitle("UpgradeAll 更新服务运行中")
        setOngoing(true)
        setSmallIcon(R.drawable.ic_launcher_foreground)
        priority = NotificationCompat.PRIORITY_LOW
    }

    init {
        createNotificationChannel()
        UpdateManager.observeForever(object : Observer {
            override fun onChanged(vararg vars: Any): Any? {
                return getNotify()
            }
        })
    }

    private fun getNotify() {
        val allAppsNum = UpdateManager.getAppNum()
        val finishedAppNum = UpdateManager.finishedUpdateAppNum.toInt()
        if (finishedAppNum != allAppsNum) {
            updateStatusNotification(allAppsNum, finishedAppNum)
        } else {
            val needUpdateAppNum = runBlocking { UpdateManager.getNeedUpdateAppList(block = false).size }
            if (needUpdateAppNum != 0)
                updateNotification(needUpdateAppNum)
            else
                cancelNotification()
            notifyChanged(FINISH_UPDATE)
        }
    }

    fun startUpdateNotification(notificationId: Int): Notification {
        NotificationManagerCompat.from(context).apply {
            builder.setContentTitle("UpgradeAll 更新服务运行中")
                    .setContentText(null)
                    .setContentIntent(mainActivityPendingIntent)
        }
        return notificationNotify(notificationId)
    }

    private fun updateStatusNotification(allAppsNum: Int, finishedAppNum: Int) {
        val progress = (finishedAppNum.toDouble() / allAppsNum * 100).toInt()
        NotificationManagerCompat.from(context).apply {
            builder.setContentTitle("检查更新中")
                    .setContentText("已完成: ${finishedAppNum}/${allAppsNum}")
                    .setProgress(100, progress, false)
                    .setOngoing(true)
        }
        notificationNotify(UPDATE_SERVER_RUNNING_NOTIFICATION_ID)
    }

    private fun updateNotification(needUpdateAppNum: Int) {
        if (!MiscellaneousUtils.isBackground()) {
            NotificationManagerCompat.from(context).apply {
                builder.run {
                    setContentTitle("$needUpdateAppNum 个应用需要更新")
                    setProgress(0, 0, false)
                    setOngoing(false)
                    setContentText("点按打开应用主页")
                    setContentIntent(mainActivityPendingIntent)
                }
            }
            notificationNotify(UPDATE_NOTIFICATION_ID)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "更新服务", NotificationManager.IMPORTANCE_MIN)
            channel.description = "显示更新服务状态"
            channel.enableLights(false)
            channel.enableVibration(false)
            channel.setShowBadge(true)
            val notificationManager = context.getSystemService(
                    Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun notificationNotify(notificationId: Int): Notification {
        val notification = builder.build()
        NotificationManagerCompat.from(context).notify(notificationId, notification)
        return notification
    }

    private fun cancelNotification() {
        NotificationManagerCompat.from(context).cancel(UPDATE_SERVER_RUNNING_NOTIFICATION_ID)
    }
}