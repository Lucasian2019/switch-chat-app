package tivana.chat.switchapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dagger.hilt.android.AndroidEntryPoint
import tivana.chat.switchapp.data.ChatChannel
import tivana.chat.switchapp.data.ChatMessage
import tivana.chat.switchapp.data.Note
import tivana.chat.switchapp.data.User
import tivana.chat.switchapp.screens.AdminScreen
import tivana.chat.switchapp.screens.ChatListScreen
import tivana.chat.switchapp.screens.ChatThreadScreen
import tivana.chat.switchapp.screens.ContactsScreen
import tivana.chat.switchapp.screens.LoginScreen
import tivana.chat.switchapp.screens.MainScaffold
import tivana.chat.switchapp.screens.NavTab
import tivana.chat.switchapp.screens.NotesScreen
import tivana.chat.switchapp.screens.RegisterScreen
import tivana.chat.switchapp.ui.theme.SWITCHTheme

enum class AppScreen {
    LOGIN, REGISTER, MAIN
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SWITCHTheme {
                SwitchAppRoot()
            }
        }
    }
}

@Composable
fun SwitchAppRoot() {
    var currentScreen by remember { mutableStateOf(AppScreen.LOGIN) }
    var currentUser by remember { mutableStateOf(User("1", "Elon Musk", "elon", isAdmin = true, isSuspended = false)) }
    var selectedTab by remember { mutableStateOf<NavTab>(NavTab.Chat) }
    var activeChannel by remember { mutableStateOf<ChatChannel?>(null) }
    var loginError by remember { mutableStateOf<String?>(null) }
    var registerError by remember { mutableStateOf<String?>(null) }

    // Sample data collections
    var users by remember {
        mutableStateOf(
            listOf(
                User("1", "Elon Musk", "elon", isAdmin = true, isSuspended = false),
                User("2", "Gwynne Shotwell", "gwynne", isAdmin = false, isSuspended = false),
                User("3", "Hans Koenigsmann", "hans", isAdmin = false, isSuspended = false),
                User("4", "Justyna", "justyna", isAdmin = false, isSuspended = false)
            )
        )
    }

    var channels by remember {
        mutableStateOf(
            listOf(
                ChatChannel("boardroom", "Boardroom", true, "Elon: Falcon 9 static fire complete.", "10:40 AM", "B"),
                ChatChannel("chat_2", "Gwynne Shotwell", false, "Payload metrics look solid.", "Yesterday", "GS"),
                ChatChannel("chat_3", "Hans Koenigsmann", false, "Confirmed engine static fire.", "2 days ago", "HK")
            )
        )
    }

    var messagesMap by remember {
        mutableStateOf(
            mapOf(
                "boardroom" to listOf(
                    ChatMessage("1", "boardroom", "u1", "Elon Musk", "Falcon 9 static fire complete.", "10:30 AM", false),
                    ChatMessage("2", "boardroom", "u2", "Gwynne Shotwell", "Stage 2 separation verified.", "10:35 AM", false),
                    ChatMessage("3", "boardroom", "me", "You", "Telemetry nominal across all sensors.", "10:40 AM", true)
                ),
                "chat_2" to listOf(
                    ChatMessage("4", "chat_2", "u2", "Gwynne Shotwell", "Payload metrics look solid.", "Yesterday", false)
                )
            )
        )
    }

    var notes by remember {
        mutableStateOf(
            listOf(
                Note("1", "Starship OFT-4", "Heat shield tile inspection protocol.", "Oct 12, 2024"),
                Note("2", "Mars Architecture", "Direct methane production from CO2.", "Oct 10, 2024")
            )
        )
    }

    when (currentScreen) {
        AppScreen.LOGIN -> {
            LoginScreen(
                onLoginClick = { username, password ->
                    if (username.isBlank() || password.isBlank()) {
                        loginError = "Username or password cannot be blank."
                    } else if (username == "error") {
                        loginError = "Username or password is incorrect."
                    } else {
                        loginError = null
                        val found = users.find { it.username == username }
                        if (found != null) {
                            currentUser = found
                        } else {
                            currentUser = User("u_${System.currentTimeMillis()}", username, username, isAdmin = username == "admin")
                        }
                        currentScreen = AppScreen.MAIN
                    }
                },
                onRegisterClick = { currentScreen = AppScreen.REGISTER },
                errorMessage = loginError
            )
        }
        AppScreen.REGISTER -> {
            RegisterScreen(
                onRegisterClick = { fullName, username, password ->
                    if (password.isBlank()) {
                        registerError = "Password cannot be blank."
                    } else if (users.any { it.username == username }) {
                        registerError = "That username is taken. Try another."
                    } else {
                        registerError = null
                        val newUser = User("u_${System.currentTimeMillis()}", fullName, username, isAdmin = false)
                        users = users + newUser
                        currentUser = newUser
                        currentScreen = AppScreen.MAIN
                    }
                },
                onLoginClick = { currentScreen = AppScreen.LOGIN },
                errorMessage = registerError
            )
        }
        AppScreen.MAIN -> {
            if (activeChannel != null) {
                val chatMessages = messagesMap[activeChannel?.id] ?: emptyList()
                ChatThreadScreen(
                    chatTitle = activeChannel?.title ?: "Chat",
                    isGroup = activeChannel?.isGroup ?: false,
                    messages = chatMessages,
                    onBackClick = { activeChannel = null },
                    onSendMessage = { text ->
                        val chId = activeChannel?.id ?: "boardroom"
                        val newMsg = ChatMessage(
                            id = "m_${System.currentTimeMillis()}",
                            chatId = chId,
                            senderId = currentUser.id,
                            senderName = currentUser.fullName,
                            text = text,
                            timestamp = "Just now",
                            isMine = true
                        )
                        val updated = (messagesMap[chId] ?: emptyList()) + newMsg
                        messagesMap = messagesMap + (chId to updated)

                        channels = channels.map {
                            if (it.id == chId) it.copy(lastMessage = "${currentUser.fullName.split(" ").first()}: $text", timestamp = "Just now") else it
                        }
                    }
                )
            } else {
                MainScaffold(
                    isAdmin = currentUser.isAdmin,
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    onLogoutClick = {
                        currentUser = User("1", "Elon Musk", "elon", isAdmin = true)
                        activeChannel = null
                        currentScreen = AppScreen.LOGIN
                    }
                ) {
                    when (selectedTab) {
                        NavTab.Chat -> {
                            ChatListScreen(
                                channels = channels,
                                selectedChatId = null,
                                onChatClick = { channel -> activeChannel = channel }
                            )
                        }
                        NavTab.Contacts -> {
                            ContactsScreen(
                                users = users.filter { it.id != currentUser.id },
                                onMessageClick = { user ->
                                    val existing = channels.find { !it.isGroup && it.title == user.fullName }
                                    if (existing != null) {
                                        activeChannel = existing
                                    } else {
                                        val newCh = ChatChannel(
                                            id = "ch_${user.id}",
                                            title = user.fullName,
                                            isGroup = false,
                                            lastMessage = "Started conversation",
                                            timestamp = "Just now",
                                            avatarText = user.fullName.take(2)
                                        )
                                        channels = channels + newCh
                                        activeChannel = newCh
                                    }
                                }
                            )
                        }
                        NavTab.Notes -> {
                            NotesScreen(
                                notes = notes,
                                onSaveNote = { title, body ->
                                    val newNote = Note("n_${System.currentTimeMillis()}", title, body, "Today")
                                    notes = listOf(newNote) + notes
                                },
                                onDeleteNote = { noteId ->
                                    notes = notes.filter { it.id != noteId }
                                }
                            )
                        }
                        NavTab.Admin -> {
                            if (currentUser.isAdmin) {
                                AdminScreen(
                                    currentUserId = currentUser.id,
                                    users = users,
                                    totalMessagesCount = messagesMap.values.sumOf { it.size },
                                    onClearBoardroom = {
                                        messagesMap = messagesMap + ("boardroom" to emptyList())
                                        channels = channels.map { if (it.id == "boardroom") it.copy(lastMessage = "Boardroom cleared") else it }
                                    },
                                    onToggleAdmin = { targetUser ->
                                        users = users.map { if (it.id == targetUser.id) it.copy(isAdmin = !it.isAdmin) else it }
                                    },
                                    onToggleSuspend = { targetUser ->
                                        users = users.map { if (it.id == targetUser.id) it.copy(isSuspended = !it.isSuspended) else it }
                                    },
                                    onRemoveUser = { targetUser ->
                                        users = users.filter { it.id != targetUser.id }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
