package com.recallrecall.app.ui.chat

import android.app.Activity
import androidx.constraintlayout.helper.widget.Flow
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.*
import com.recallrecall.app.db.Message
import com.recallrecall.app.db.MessageDataBase

class ChatActivityViewModel(name: String) : ViewModel() {
    val dataBase = MessageDataBase.getInstance(Activity().baseContext)
    val messageList:
            PagingSource<Int, Message> = dataBase?.messageDao!!.loadByName(name)

    val msgs =  Pager(PagingConfig(pageSize = 50,enablePlaceholders = true)){
        messageList
    }.flow

}