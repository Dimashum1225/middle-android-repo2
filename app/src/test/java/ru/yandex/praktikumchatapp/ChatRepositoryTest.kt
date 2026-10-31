package ru.yandex.praktikumchatapp

import app.cash.turbine.test
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.yandex.praktikumchatapp.data.ChatApi
import ru.yandex.praktikumchatapp.data.ChatRepository

@OptIn(ExperimentalCoroutinesApi::class)
class ChatRepositoryTest {
    private val testDispatcher = StandardTestDispatcher()
    private val chatApi: ChatApi = mock()
    private lateinit var chatRepository: ChatRepository

    @Before
    fun setup() {
        chatRepository = ChatRepository(chatApi)
    }

    @Test
    fun `getReplyMessage should return a non-empty string`() = runTest(testDispatcher) {
        whenever(chatApi.getReply()).thenReturn(flowOf("Hello"))
        chatRepository.getReplyMessage().test {
            assertEquals("Hello", awaitItem())
            awaitComplete()
        }
        assertEquals(0L, testScheduler.currentTime)
        verify(chatApi).getReply()
    }

    @Test
    fun `getReplyMessage should retry on error then successfully return string`() = runTest(testDispatcher) {
        var isException = true
        whenever(chatApi.getReply()).thenReturn(flow {
            if (isException) {
                isException = false
                throw Exception("test exception")
            }
            emit("Hello")
        })
        chatRepository.getReplyMessage().test {
            assertEquals("Hello", awaitItem())
            awaitComplete()
        }
        assertEquals(1_000L, testScheduler.currentTime)
    }

    @Test
    fun `retry delays should grow exponentially`() = runTest(testDispatcher) {
        val attempts = mutableListOf<Long>()
        whenever(chatApi.getReply()).thenReturn(flow {
            attempts.add(testScheduler.currentTime)
            if (attempts.size <= 3) throw Exception("Temporary error")
            emit("Recovered")
        })
        chatRepository.getReplyMessage().test {
            assertEquals("Recovered", awaitItem())
            awaitComplete()
        }
        assertEquals(listOf(0L, 1_000L, 3_000L, 7_000L), attempts)
    }

    @Test
    fun `persistent error should stop after three retries`() = runTest(testDispatcher) {
        var attempts = 0
        val failure = Exception("Unavailable")
        whenever(chatApi.getReply()).thenReturn(flow<String> {
            attempts++
            throw failure
        })
        chatRepository.getReplyMessage().test { assertSame(failure, awaitError()) }
        assertEquals(4, attempts)
        assertEquals(7_000L, testScheduler.currentTime)
    }

    @Test
    fun `cancellation should not be retried`() = runTest(testDispatcher) {
        var attempts = 0
        val cancellation = CancellationException("Cancelled")
        whenever(chatApi.getReply()).thenReturn(flow<String> {
            attempts++
            throw cancellation
        })
        chatRepository.getReplyMessage().test { assertSame(cancellation, awaitError()) }
        assertEquals(1, attempts)
        assertEquals(0L, testScheduler.currentTime)
    }
}
