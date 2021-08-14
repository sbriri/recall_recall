package com.recallrecall.app.ui.wechat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.recallrecall.app.MainActivity
import com.recallrecall.app.R
import com.recallrecall.app.databinding.FragmentWechatBinding
import com.recallrecall.app.db.Message
import com.recallrecall.app.ui.chat.ChatActivity
import com.recallrecall.app.ui.chat.ChatFragment
import kotlinx.android.synthetic.main.fragment_wechat.view.*

class WechatFragment : Fragment() {

    private lateinit var wechatViewModel: WechatViewModel
    private var _binding: FragmentWechatBinding? = null
    val TAG = "Wechat fragment"

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        wechatViewModel =
            ViewModelProvider(this).get(WechatViewModel::class.java)

        _binding = FragmentWechatBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //create recycler view as chat item
        val recyclerView: RecyclerView = root.recyclerView_wechat

        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)

            //set up adapter
            wechatViewModel.currentNames?.observe(viewLifecycleOwner, { names ->
                if (names == null) {
                    return@observe
                }
                val tempMessages = getLatestMessages(names)
                Log.d(TAG, tempMessages.toString())

                Log.d(TAG, names.toString())
                val adp = WechatItemAdapter(tempMessages)
                adp.onItemClick = { message ->
                    val fragment = ChatFragment(message.name, getString(R.string.title_wechat))
                    val transation = activity?.supportFragmentManager?.beginTransaction()!!.apply {
                        replace(R.id.fragment_wechat, fragment)
                        addToBackStack(null)
                    }
                    transation.commit()

                }
                adapter = adp


            })

        }






        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getLatestMessages(names: List<String>): MutableList<Message>? {
        val messages: MutableList<Message>? = mutableListOf()
        for (name in names) {
            messages?.add(wechatViewModel.dataBase?.messageDao?.loadLatestByName(name)!!)
        }
        return messages
    }


}