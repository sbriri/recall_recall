package com.recallrecall.app.ui.chat

import android.app.ActionBar
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.recallrecall.app.R
import com.recallrecall.app.databinding.FragmentChatBinding
import com.recallrecall.app.databinding.FragmentWechatBinding
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
import android.widget.TextView
import com.recallrecall.app.MainActivity
import kotlinx.android.synthetic.main.activity_main.*


class ChatViewModel(name: String) : ViewModel() {
    val dataBase = MessageDataBase.getInstance(Activity().baseContext)
    val msgs: LiveData<List<Message?>?>? = dataBase?.messageDao?.loadByName(name)

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
//                    Scaffold(topBar = { AppBar(title = name) }) {
                        ShowAllMessages(viewModel.msgs, viewModel)
//                    }

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


//@Composable
//fun ShowMessage(
//    time: String,
//    text: String,
//    recalled: Boolean = false,
//    click: (() -> Unit)? = null
//) {
//    val mark = remember { mutableStateOf(recalled) }
//    Row(
//        Modifier
//            .clickable(onClick = {
//                Log.d("Wechat", "clicked")
//                mark.value = !mark.value
//                click?.invoke()
//            })
//            .fillMaxWidth()
//            .background(color = if (mark.value) colorResource(R.color.pinky_red) else Color.White),
//        horizontalArrangement = Arrangement.Start,
//        verticalAlignment = Alignment.CenterVertically,
//
//        ) {
//
//        Text(
//            text = time, Modifier
//                .width(70.dp)
//                .height(48.dp)
//                .padding(start = 18.dp), fontSize = 12.sp, textAlign = TextAlign.Center
//        )
//
//        Text(
//            text = text, Modifier.padding(start = 10.dp), fontSize = 16.sp
//        )
//
//
//    }
//}
//
//
@ExperimentalFoundationApi
@Composable
fun ShowAllMessages(
    liveMessages: LiveData<List<Message?>?>?,
    viewModel: ChatViewModel
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

            items(messages!!) { message ->
                if (message != null) {
                    ShowMessage(
                        time = message.date!!,
                        text = message.content,
                        recalled = message.recalled,
                        click = {
                            Log.d("Wechat", "ShowAllMessages")
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


/**
 * only for preview
 */
@Composable
fun AppBar(title: String) {
    TopAppBar(
        title = {
            Text(text = title)
        }, navigationIcon = {
            // show drawer icon
            IconButton(
                onClick = {
                    Log.d("TAG", "Drawer icon clicked")
                }
            ) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "")
            }
        }
    )

}
