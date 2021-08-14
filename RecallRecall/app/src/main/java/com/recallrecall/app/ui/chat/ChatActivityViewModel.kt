package com.recallrecall.app.ui.chat

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.recallrecall.app.db.Message
import com.recallrecall.app.db.MessageDataBase

class ChatActivityViewModel(name: String) : ViewModel() {
    val dataBase = MessageDataBase.getInstance(Activity().baseContext)
    val msgs: LiveData<List<Message?>?>? = dataBase?.messageDao?.loadByName(name)

}