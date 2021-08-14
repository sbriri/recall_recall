package com.recallrecall.app.ui.wechat

import android.app.Application
import androidx.lifecycle.*
import com.recallrecall.app.db.Message
import com.recallrecall.app.db.MessageDataBase


class WechatViewModel(application: Application) : AndroidViewModel(application) {

    val dataBase = MessageDataBase.getInstance(application.baseContext)
    val currentNames: LiveData<List<String>?>? = dataBase?.messageDao?.loadAllName()



}

