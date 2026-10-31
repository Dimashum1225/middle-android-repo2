package ru.yandex.praktikumchatapp.presentation

data class ChatState(
    val messages: List<Message> = emptyList(),
    val shouldShowKeyboard: Boolean = false
)
