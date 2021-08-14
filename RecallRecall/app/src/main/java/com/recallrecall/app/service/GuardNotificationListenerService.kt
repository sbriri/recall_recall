package com.recallrecall.app.service

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.text.SpannableString
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*


/**
 * @Description:
 * @Author: lhw
 * @CreateDate: 2020/2/22 17:59
 */
class GuardNotificationListenerService : NotificationListenerService() {

    val wechatPkg = "com.tencent.mm"


    override fun onNotificationPosted(sbn: StatusBarNotification) {
//        Log.d(TAG, "onNotificationPosted")
        showMsg(sbn)
    }

    private fun showMsg(sbn: StatusBarNotification) {

        val packageName = sbn.packageName
        if (packageName != wechatPkg) return
        val extras = sbn.notification.extras
        if (extras != null) {
            //获取通知消息标题
            val title = extras.getString(Notification.EXTRA_TITLE)
            // 获取通知消息内容
            val msgText: Any? = extras.getCharSequence(Notification.EXTRA_TEXT)
            //注意：获取的通知信息和短信的传递内容不一样 短信为SpannableString 这里容易造成转换异常
            if (msgText is SpannableString) {
                Log.d(TAG, "is SpannableString ...." + msgText.subSequence(0, msgText.length))
            } else {

                saveMsg(packageName, title, msgText.toString())
                Log.d(
                    TAG,
                    "showMsg packageName=$packageName,title=$title,msgText=$msgText"
                )
            }
        } else {
            Log.d(TAG, "is null ....$packageName")
        }
    }

    private fun saveMsg(pkgName: String, title: String?, text: String?) {
//        Log.d(TAG, "on save, $pkgName")
        if (pkgName != wechatPkg) return

        val sdf = SimpleDateFormat("yyyy MM/dd hh:mm:ss")
        val currentDate = sdf.format(Date())
        Log.d(TAG, "on save, $currentDate")

        WeChatService(applicationContext).addNotification(title,text,currentDate)

    }



    companion object {
        val TAG = GuardNotificationListenerService::class.java.simpleName
    }
}