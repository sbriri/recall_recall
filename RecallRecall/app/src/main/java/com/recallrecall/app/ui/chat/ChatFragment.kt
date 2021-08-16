package com.recallrecall.app.ui.chat

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.paging.*
import com.recallrecall.app.R
import com.recallrecall.app.databinding.FragmentChatBinding
import com.recallrecall.app.db.Message
import com.recallrecall.app.db.MessageDataBase
import com.recallrecall.app.ui.chat.ui.theme.RecallRecallTheme
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.recallrecall.app.MainActivity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*


class ChatViewModel(name: String) : ViewModel() {
    val dataBase = MessageDataBase.getInstance(Activity().baseContext)

    val msgs =
        Pager(PagingConfig(pageSize = 30, enablePlaceholders = true)) {
            dataBase?.messageDao!!.loadByName(name)
        }.flow

}


class ChatFragment(private val name: String, private val from: String) : Fragment() {

    private lateinit var viewModel: ChatViewModel
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!


    @ExperimentalFoundationApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ChatViewModel(name)
        _binding = FragmentChatBinding.inflate(inflater, container, false)

        val root: View = binding.root

        (activity as MainActivity?)?.findViewById<Toolbar>(R.id.toolbar)?.title = name


        val chatCompose = root.findViewById<ComposeView>(R.id.chatCompose)
        chatCompose.setContent {
            RecallRecallTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Scaffold(content = {
                        ShowAllMessages(viewModel.msgs, viewModel)
                    })

                }
            }
        }

        return root
    }


    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity?)?.findViewById<Toolbar>(R.id.toolbar)?.title = from
    }

}

@Composable
fun ShowDate(date: String) {
    LazyRow(
        Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically

    ) {
        item {
            Text(text = dateToWrittenDate(date))
        }
    }
}

@Composable
fun ShowMessage(
    date: String,
    text: String,
    recalled: Boolean = false,
    click: (() -> Unit)? = null
) {
    val mark = remember { mutableStateOf(recalled) }

    Row(
        Modifier
            .clickable(onClick = {
                Log.d("Wechat", "clicked")
                mark.value = !mark.value
                click?.invoke()
            })
            .fillMaxWidth()
            .background(color = if (mark.value) colorResource(R.color.pinky_red) else Color.White),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,

        ) {

        //yyyy dd/dd tt:tt:tt
        val time = date.substring(10)
        Text(
            text = time, Modifier
                .width(70.dp)
                .height(48.dp)
                .padding(start = 18.dp), fontSize = 12.sp, textAlign = TextAlign.Center
        )
        Divider(
            color = Color.Black,
            modifier = Modifier
                .padding(10.dp)
                .width(1.dp)
                .height(30.dp)
        )
        Text(
            text = text, Modifier.padding(start = 10.dp), fontSize = 16.sp
        )


    }
}


private fun isToday(date: String): Boolean {
    val sdf = SimpleDateFormat("yyyy MM/dd hh:mm:ss")
    val currentDate = sdf.format(Date())
    return date.substring(0, 10) == currentDate.substring(0, 10)
}

private fun dateToWrittenDate(date: String): String {
    if (isToday(date)) {
        return "Today"
    }
    val year = date.substring(0, 4)
    val month = date.substring(5, 7)
    val day = date.substring(8, 10)
    return "$month/$day $year"
}

@ExperimentalFoundationApi
@Composable
fun ShowAllMessages(
    pageMessages: Flow<PagingData<Message>>,

    viewModel: ChatViewModel
) {

    val lazyMessages: LazyPagingItems<Message> = pageMessages.collectAsLazyPagingItems()
    var date: String
    SwipeRefreshList(collectAsLazyPagingItems = lazyMessages) {
        items(items = lazyMessages,
            key = { message ->
                message.id
            }) { message ->
            if (message != null) {

                if (isToday(message.date!!)) {
                    ShowDate(date = message.date!!)
                }
                ShowMessage(
                    date = message.date!!,
                    text = message.content,
                    recalled = message.recalled,
                    click = {
                        Log.d("Wechat", "ShowAllMessages")
                        message.recalled = !message.recalled
                        viewModel.dataBase?.messageDao?.update(message)
                    }
                )
            }
//            Divider()
        }

    }


}


@Preview
@Composable
fun preview() {
    val date = "2021 07/16 08:59:16"
    val txt = "this is a text"
    ShowMessage(date = date, text = txt)
}