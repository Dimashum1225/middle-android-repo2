package ru.yandex.praktikumchatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.yandex.praktikumchatapp.presentation.ChatViewModel
import ru.yandex.praktikumchatapp.presentation.Message
import ru.yandex.praktikumchatapp.ui.theme.PraktikumChatAppTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PraktikumChatAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(stringResource(R.string.app_name))
                            }
                        )
                    },
                    content = { innerPadding ->
                        ChatScreen(
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier
) {
    val viewModel = remember { ChatViewModel() }
    val messagesList = viewModel.messages.collectAsState()
    val messageText = remember { mutableStateOf("") }
    // TODO Задание 3: добавьте focusRequester

    Column(modifier = modifier.fillMaxSize()) {

        // Список сообщений
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp, end = 16.dp, top = 16.dp)
        ) {
            items(messagesList.value) { message ->
                when (message) {
                    is Message.MyMessage -> MyMessageCard(message)
                    is Message.OtherMessage -> OtherMessageCard(message)
                }
            }
        }

        // Поле для ввода сообщения
        Row(
            modifier = Modifier
                .padding(16.dp)
                // TODO Задание 3: добавьте focusRequester
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = messageText.value,
                onValueChange = { messageText.value = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .background(Color.LightGray, shape = MaterialTheme.shapes.small)
                    .padding(10.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (messageText.value.isNotBlank()) {
                            viewModel.sendMyMessage(messageText.value)
                            messageText.value = ""
                        }
                    }
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (messageText.value.isNotBlank()) {
                        viewModel.sendMyMessage(messageText.value)
                        messageText.value = ""
                    }
                }
            ) {
                Text(stringResource(R.string.send))
            }
        }
    }
}

@Composable
fun MyMessageCard(message: Message.MyMessage) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = message.text,
            fontSize = 16.sp,
            modifier = Modifier
                .padding(vertical = 4.dp)
                .background(Color.Green.copy(alpha = 0.2f), shape = MaterialTheme.shapes.small)
                .padding(8.dp)
                .align(Alignment.CenterEnd)
        )
    }
}

@Composable
fun OtherMessageCard(message: Message.OtherMessage) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = message.text,
            fontSize = 16.sp,
            modifier = Modifier
                .padding(vertical = 4.dp)
                .background(Color.Blue.copy(alpha = 0.2f), shape = MaterialTheme.shapes.small)
                .padding(8.dp)
                .align(Alignment.CenterStart)
        )
    }
}