package ru.yandex.praktikumchatapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _shouldShowKeyboard = MutableStateFlow(false)
    val shouldShowKeyboard: StateFlow<Boolean> = _shouldShowKeyboard.asStateFlow()

    // TODO Задание 4: замените messages и shouldShowKeyboard на state

    init {
        if (isWithReplies) {
            viewModelScope.launch {
                repository.getReplyMessage()
                    .catch {
                        receiveMessage(Message.OtherMessage("Не удалось получить ответ. Попробуйте позже."))
                    }
                    .collect { response ->
                        receiveMessage(Message.OtherMessage(response))
                    }
            }
        }
    }

    private fun receiveMessage(message: Message) {
        _messages.update { messages -> messages + message }
        _shouldShowKeyboard.value = true
    }

    fun sendMyMessage(messageText: String) {
        receiveMessage(Message.MyMessage(messageText))
    }
}
