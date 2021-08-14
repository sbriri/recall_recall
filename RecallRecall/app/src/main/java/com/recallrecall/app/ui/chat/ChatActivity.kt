package com.recallrecall.app.ui.chat

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.recallrecall.app.R
import com.recallrecall.app.db.Message
import com.recallrecall.app.db.MessageDataBase
import com.recallrecall.app.ui.chat.ui.theme.Purple200
import com.recallrecall.app.ui.chat.ui.theme.RecallRecallTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


/**
 * deprecated
 */
class ChatActivity : AppCompatActivity() {
    private lateinit var viewModel: ChatActivityViewModel

    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val name = intent.getStringExtra("name")
        supportActionBar?.title = name
        if (name == null) {
            return
        }
        viewModel = ChatActivityViewModel(name)


        setContent {
            RecallRecallTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    ShowAllMessages(viewModel.msgs, viewModel)
                }
            }
        }
    }


}


@Composable
fun ShowMessage(
    time: String,
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

        Text(
            text = time, Modifier
                .width(70.dp)
                .height(48.dp)
                .padding(start = 18.dp), fontSize = 12.sp, textAlign = TextAlign.Center
        )

        Text(
            text = text, Modifier.padding(start = 10.dp), fontSize = 16.sp
        )


    }
}


@ExperimentalFoundationApi
@Composable
fun ShowAllMessages(
    liveMessages: LiveData<List<Message?>?>?,
    viewModel: ChatActivityViewModel
) {
    val messages by liveMessages!!.observeAsState(initial = emptyList())
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    LazyColumn(
        reverseLayout = false,
        state = lazyListState,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxHeight(),
    ) {
        if (messages!!.isNotEmpty()) {

            items(messages!!) { message->
                if (message != null) {
                    ShowMessage(
                        time = message.date!!,
                        text = message.content,
                        recalled = message.recalled,
                        click = {
                            Log.d("Wechat","ShowAllMessages")
                            message.recalled = !message.recalled
                            viewModel.dataBase?.messageDao?.update(message)
                        }
                    )
                }
                Divider()
            }
            scope.launch {
                // scroll to the first item
                lazyListState.scrollToItem(lazyListState.firstVisibleItemIndex)

            }
        }


    }


}




//@ExperimentalFoundationApi
//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    val msg = Message(name = "标题", content = "内容1", date = "2021 08/05 03:20")
//    val msg2 = Message(
//        name = "标题",
//        content = "内容长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长",
//        date = "2021 08/20 03:20:13"
//    )
//    var messages: List<Message?>? = mutableListOf(msg, msg2)
//    RecallRecallTheme {
//        Column {
//            AppBar()
//            ShowAllMessages(messages as LiveData<List<Message?>?>)
//
//        }
//
//    }
//}