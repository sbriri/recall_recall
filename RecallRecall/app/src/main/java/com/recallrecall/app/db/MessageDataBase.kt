package com.recallrecall.app.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope


@Database(entities = [Message::class], version = 1, exportSchema = false)
abstract class MessageDataBase : RoomDatabase() {


    abstract val messageDao: MessageDao?

    companion object {
        const val DB_NAME_WECHAT = "wechat.db"

        private var instance: MessageDataBase? = null
        @Synchronized
        fun getInstance(context: Context?): MessageDataBase? {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context!!,
                    MessageDataBase::class.java,
                    DB_NAME_WECHAT
                )
                    .allowMainThreadQueries() //默认room不允许在主线程操作数据库，这里设置允许
                    .build()
            }
            return instance
        }
    }

}