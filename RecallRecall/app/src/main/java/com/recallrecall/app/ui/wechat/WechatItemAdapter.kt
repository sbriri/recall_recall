package com.recallrecall.app.ui.wechat


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.recallrecall.app.R
import com.recallrecall.app.db.Message



class WechatItemAdapter(
    val messages: List<Message?>?
) :
    RecyclerView.Adapter<WechatItemAdapter.ViewHolder>() {

    var onItemClick: ((Message) -> Unit)? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val textViewUser: TextView = view.findViewById(R.id.textView_user)
        val textViewDate: TextView = view.findViewById(R.id.textView_date)
        val textViewContent: TextView = view.findViewById(R.id.textView_content)
        val view = view
        fun updateView(message: Message) {
            textViewUser.text = message.name
            textViewDate.text = message.date
            textViewContent.text = message.content
            if (message.recalled) view.setBackgroundColor(ContextCompat.getColor(view.context,R.color.pinky_red))
            Log.d("adapter", message.toString())

        }

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            onItemClick?.invoke(messages!![adapterPosition]!!)
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