package com.recallrecall.app.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "db_message")
data class Message(

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo
    var name: String,

    @ColumnInfo
    var content: String,

    @ColumnInfo
    var date: String?,

    @ColumnInfo
    var recalled: Boolean = false

) {

//    constructor() : this(0, "","","",false)

    override fun toString(): String {
        return "Message(mid=$id, name=$name, content=$content, date=$date, recalled=$recalled)"
    }


}