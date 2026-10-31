package ru.yandex.praktikumchatapp

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import ru.yandex.praktikumchatapp.data.ChatApi
import ru.yandex.praktikumchatapp.data.ChatRepository
import ru.yandex.praktikumchatapp.presentation.ChatState
import ru.yandex.praktikumchatapp.presentation.ChatViewModel
import ru.yandex.praktikumchatapp.presentation.Message

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ChatViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ChatViewModel(isWithReplies = false)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `send message should update state with MyMessage`() = runTest(testDispatcher) {
        val message = Message.MyMessage("TestMessage")
        viewModel.sendMyMessage(message.text)
        advanceUntilIdle()
        assertEquals(listOf(message), viewModel.chatState.value.messages)
        assertTrue(viewModel.chatState.value.shouldShowKeyboard)
    }

    @Test
    fun `initial state should have no messages or keyboard request`() = runTest(testDispatcher) {
        assertEquals(ChatState(), viewModel.chatState.value)
    }

    @Test
    fun `sending another message should keep previous messages`() = runTest(testDispatcher) {
        viewModel.sendMyMessage("First")
        viewModel.sendMyMessage("Second")
        assertEquals(
            listOf(Message.MyMessage("First"), Message.MyMessage("Second")),
            viewModel.chatState.value.messages
        )
    }

    @Test
    fun `received replies should update messages and keyboard together`() = runTest(testDispatcher) {
        val api: ChatApi = mock()
        whenever(api.getReply()).thenReturn(flowOf("First reply", "Second reply"))
        val receivingViewModel = ChatViewModel(repository = ChatRepository(api))
        assertFalse(receivingViewModel.chatState.value.shouldShowKeyboard)
        advanceUntilIdle()
        assertEquals(
            ChatState(
                messages = listOf(Message.OtherMessage("First reply"), Message.OtherMessage("Second reply")),
                shouldShowKeyboard = true
            ),
            receivingViewModel.chatState.value
        )
    }

    @Test
    fun `exhausted retries should show error and allow sending messages`() = runTest(testDispatcher) {
        val api: ChatApi = mock()
        whenever(api.getReply()).thenReturn(flow<String> { throw Exception("Unavailable") })
        val receivingViewModel = ChatViewModel(repository = ChatRepository(api))
        advanceUntilIdle()
        assertEquals(7_000L, testScheduler.currentTime)
        assertEquals(
            listOf(Message.OtherMessage("Не удалось получить ответ. Попробуйте позже.")),
            receivingViewModel.chatState.value.messages
        )
        receivingViewModel.sendMyMessage("Still here")
        assertEquals(Message.MyMessage("Still here"), receivingViewModel.chatState.value.messages.last())
    }

    @Test
    fun `cancellation should not appear as a chat error`() = runTest(testDispatcher) {
        val api: ChatApi = mock()
        whenever(api.getReply()).thenReturn(flow<String> { throw CancellationException("Cancelled") })
        val receivingViewModel = ChatViewModel(repository = ChatRepository(api))
        advanceUntilIdle()
        assertEquals(ChatState(), receivingViewModel.chatState.value)
    }

    @Test
    fun testReceiveMessage_concurrentMessages() = runTest(testDispatcher) {
        val messagesToSend = (1..100).map { Message.MyMessage("Message $it") }

        // TODO Задание 6: допишите юнит-тест
    }
}
