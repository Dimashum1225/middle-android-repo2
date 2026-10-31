package ru.yandex.praktikumchatapp

import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import org.junit.Rule
import org.junit.Test
import ru.yandex.praktikumchatapp.presentation.ChatViewModel
import ru.yandex.praktikumchatapp.ui.theme.PraktikumChatAppTheme

class ChatScreenTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun firstMessageRequestsFocusOnlyOnce() {
        val viewModel = ChatViewModel(isWithReplies = false)
        lateinit var focusManager: FocusManager
        compose.setContent {
            focusManager = LocalFocusManager.current
            PraktikumChatAppTheme { ChatScreen(viewModel = viewModel) }
        }
        val input = compose.onNode(hasSetTextAction())
        input.assertIsNotFocused()
        compose.runOnIdle { viewModel.sendMyMessage("Первое сообщение") }
        input.assertIsFocused()
        compose.onNodeWithText("Первое сообщение").assertIsDisplayed()
        compose.runOnIdle { focusManager.clearFocus() }
        compose.runOnIdle { viewModel.sendMyMessage("Второе сообщение") }
        input.assertIsNotFocused()
    }
}
