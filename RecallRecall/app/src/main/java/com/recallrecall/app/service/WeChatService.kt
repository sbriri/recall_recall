package com.recallrecall.app.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.recallrecall.app.db.Message
import com.recallrecall.app.db.MessageDao
import com.recallrecall.app.db.MessageDataBase
import com.recallrecall.app.db.MessageDataBase.Companion.getInstance
import androidx.core.app.NotificationManagerCompat


import android.app.NotificationManager

import android.app.NotificationChannel
import android.content.Intent

import android.os.Build
import java.util.*

import androidx.navigation.NavDeepLinkBuilder

import android.os.Bundle
import androidx.compose.ui.res.stringResource
import com.recallrecall.app.MainActivity
import com.recallrecall.app.R
import java.lang.StringBuilder


class WeChatService(context: Context) {

    val TAG = "Wechat"
    val CHANNEL_ID = "WeChatService"
    val CHANNEL_NAME = "Wechat"
    var instance: MessageDataBase? =
        getInstance(context)
    var messageDao: MessageDao? = instance?.messageDao
    val context = context


    @SuppressLint("LongLogTag")
    fun addNotification(title: String?, text: String?, date: String?) {
        if (title == null || text == null) return

//        Log.d(TAG, messageDao.toString())

        //找到内容左边的 ‘：’的位置，从而找到内容
        val idx = text.indexOf(title)

//        Log.d("GuardNotificationListenerService", idx.toString())

        var content = text
        //检查是否不是第一条信息
        if (idx != -1) {
            content = text.substring(idx + title.length + 2)
            Log.d("GuardNotificationListenerService", content.toString())
        }




        //检查是否是撤回消息提醒
        if (content.contains("撤回了一条消息") || content.contains("撤回一条消息")) {
            updateToRecalled(date, title)
            return
        }

        //建立message
        val msg = Message(name = title, content = content, date = date)

        messageDao?.insertAll(msg)


//        Log.d(TAG, msg.toString())


    }


    fun updateToRecalled(date: String?, title: String?) {

        //yyyy MM/dd hh:mm:ss
//       val startDate = date?.replace(date[15], date[15] - 2)
        val startDate = theWorld(date)
        if (startDate != null) {
            Log.d(TAG, "$startDate $date")
        }
        val messages = messageDao?.loadByDateAndName(startDate, date, title)
        if (messages != null) {
            if (messages[0] != null) sendNotification(this.context, messages[0]!!)

            for (message in messages) {

                message?.recalled = true

                messageDao?.update(message!!)
//                Log.d(TAG, message.toString())

            }
        }
    }


    fun theWorld(date: String?): String? {
        if (date == null) return null
        var min: StringBuilder = StringBuilder()
        var res = ""
        min.append(date[14]).append(date[15])
        var temp = (min.toString().toInt()) - 2

        //Todo: fix the situation that temp < 0 which need to cause hour to change as well
        if (temp < 0) temp = 0

        Log.d(TAG, temp.toString())
        if (temp < 10) {
            res = date.substring(0, 14) + "0" + temp.toString() + date.substring(16)
        } else {
            res = date.substring(0, 14) + temp.toString() + date.substring(16)
        }


        Log.d(TAG, res)
        return res
    }

    private fun sendNotification(context: Context, message: Message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            channel.description = "description"
            val notificationManager = context.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setContentTitle("${message.name} 撤回了一条信息")
            .setContentText("点击进入APP内查看")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(getPendingIntent(context, message))
            .setAutoCancel(true)
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(Random().nextInt(10000), builder.build())
    }

    private fun getPendingIntent(
        context: Context,
        message: Message
    ): PendingIntent? {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        intent.putExtra("name", message.name)
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        return pendingIntent
    }

}