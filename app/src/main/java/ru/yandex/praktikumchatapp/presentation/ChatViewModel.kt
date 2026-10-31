package ru.yandex.praktikumchatapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.yandex.praktikumchatapp.data.ChatRepository

class ChatViewModel(
    val isWithReplies: Boolean = true,
    private val repository: ChatRepository = ChatRepository()
) : ViewModel() {

    private val _chatState = MutableStateFlow(ChatState())
    val chatState: StateFlow<ChatState> = _chatState.asStateFlow()

    init {
        if (isWithReplies) {
            viewModelScope.launch {
                repository.getReplyMessage()
                    .catch { cause ->
                        if (cause is CancellationException) throw cause
                        receiveMessage(Message.OtherMessage("Не удалось получить ответ. Попробуйте позже."))
                    }
                    .collect { response ->
                        receiveMessage(Message.OtherMessage(response))
                    }
            }
        }
    }

    private fun receiveMessage(message: Message) {
        _chatState.update { state ->
            state.copy(
                messages = state.messages + message,
                shouldShowKeyboard = true
            )
        }
    }

    fun sendMyMessage(messageText: String) {
        receiveMessage(Message.MyMessage(messageText))
    }
}
