package ru.yandex.praktikumchatapp.data

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.retryWhen

class ChatRepository(
    private val api: ChatApi = ChatApi()
) {
    fun getReplyMessage(): Flow<String> = api.getReply()
        .retryWhen { cause, attempt ->
            if (cause is Exception && cause !is CancellationException && attempt < MAX_RETRIES) {
                delay(INITIAL_RETRY_DELAY * (1L shl attempt.toInt()))
                true
            } else {
                false
            }
        }

    private companion object {
        const val MAX_RETRIES = 3L
        const val INITIAL_RETRY_DELAY = 1_000L
    }
}
