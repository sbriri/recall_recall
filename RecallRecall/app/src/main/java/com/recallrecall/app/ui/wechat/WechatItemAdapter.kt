package com.recallrecall.app.ui.wechat

import android.graphics.ColorSpace
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.recallrecall.app.R
import com.recallrecall.app.db.Message


class WechatItemAdapter(
    val messages: List<Message?>?
) :
    RecyclerView.Adapter<WechatItemAdapter.ViewHolder>() {

    var onItemClick: ((Message) -> Unit)? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewUser: TextView = view.findViewById(R.id.textView_user)
        val textViewDate: TextView = view.findViewById(R.id.textView_date)
        val textViewContent: TextView = view.findViewById(R.id.textView_content)


        fun updateView(message: Message) {
            textViewUser.text = message.name
            textViewDate.text = message.date
            textViewContent.text = message.content

            Log.d("adapter", message.toString())

        }

        init {
            view.setOnClickListener {
                onItemClick?.invoke(messages!![adapterPosition]!!)
            }
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.chat_item, parent, false)
        Log.d("adapter", "onCreateViewHolder $messages")
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("adapter", messages.toString())
        holder.updateView(messages!![position]!!)
    }

    override fun getItemCount(): Int {
        if (messages != null) {
            return messages.size
        }
        return 0
    }
}