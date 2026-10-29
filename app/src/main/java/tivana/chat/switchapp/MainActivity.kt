package tivana.chat.switchapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import tivana.chat.switchapp.data.ChatChannel
import tivana.chat.switchapp.data.ChatMessage
import tivana.chat.switchapp.data.Note
import tivana.chat.switchapp.data.User
import tivana.chat.switchapp.screens.AdminScreen
import tivana.chat.switchapp.screens.ChatBotScreen
import tivana.chat.switchapp.screens.ChatListScreen
import tivana.chat.switchapp.screens.ChatThreadScreen
import tivana.chat.switchapp.screens.ContactsScreen
import tivana.chat.switchapp.screens.LoginScreen
import tivana.chat.switchapp.screens.MainScaffold
import tivana.chat.switchapp.screens.NavTab
import tivana.chat.switchapp.screens.NotesScreen
import tivana.chat.switchapp.screens.RegisterScreen
import tivana.chat.switchapp.screens.SettingsScreen
import tivana.chat.switchapp.ui.components.ButtonVariant
import tivana.chat.switchapp.ui.components.SwitchButton
import tivana.chat.switchapp.ui.components.SwitchTextField
import tivana.chat.switchapp.ui.theme.SWITCHTheme
import tivana.chat.switchapp.ui.theme.SwitchTheme
import tivana.chat.switchapp.ui.theme.SwitchTypography

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
fun PinLockScreen(
    onUnlock: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SwitchTheme.colors.Background)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "SWITCH SECURE LOCK", style = SwitchTypography.logo, color = SwitchTheme.colors.TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Enter 4-digit PIN (default: 1234)", style = SwitchTypography.secondary, color = SwitchTheme.colors.TextMuted)
        Spacer(modifier = Modifier.height(24.dp))
        SwitchTextField(
            value = pin,
            onValueChange = { if (it.length <= 4) pin = it },
            label = "PIN Code",
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        SwitchButton(
            text = "Unlock",
            onClick = {
                if (pin == "1234") {
                    onUnlock()
                } else {
                    error = "Incorrect PIN. Try 1234."
                }
            },
            variant = ButtonVariant.PRIMARY
        )
        if (error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = error!!, style = SwitchTypography.bodySmall, color = SwitchTheme.colors.DangerRed)
        }
    }
}

@Composable
fun SwitchAppRoot() {
    val context = LocalContext.current
    var isLocked by remember { mutableStateOf(true) }
    var currentScreen by remember { mutableStateOf(AppScreen.LOGIN) }
    var currentUser by remember { mutableStateOf(User("1", "Elon Musk", "elon", isAdmin = true, isSuspended = false)) }
    var selectedTab by remember { mutableStateOf<NavTab>(NavTab.Chat) }
    var activeChannel by remember { mutableStateOf<ChatChannel?>(null) }
    var loginError by remember { mutableStateOf<String?>(null) }
    var registerError by remember { mutableStateOf<String?>(null) }
    var broadcastMessage by remember { mutableStateOf<String?>(null) }

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

    if (isLocked) {
        PinLockScreen(onUnlock = { isLocked = false })
        return
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
                    broadcastMessage = broadcastMessage,
                    onLogoutClick = {
                        currentUser = User("1", "Elon Musk", "elon", isAdmin = true)
                        activeChannel = null
                        currentScreen = AppScreen.LOGIN
                        isLocked = true
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
                        NavTab.Bot -> {
                            ChatBotScreen()
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
                                onSaveNote = { noteToSave ->
                                    val existingIndex = notes.indexOfFirst { it.id == noteToSave.id }
                                    notes = if (existingIndex >= 0) {
                                        notes.map { if (it.id == noteToSave.id) noteToSave else it }
                                    } else {
                                        listOf(noteToSave) + notes
                                    }
                                    Toast.makeText(context, "Note saved successfully", Toast.LENGTH_SHORT).show()
                                },
                                onArchiveNote = { noteId ->
                                    notes = notes.map { if (it.id == noteId) it.copy(isArchived = !it.isArchived) else it }
                                    Toast.makeText(context, "Note archive status updated", Toast.LENGTH_SHORT).show()
                                },
                                onExportNote = { note ->
                                    Toast.makeText(context, "Exported note '${note.title}' as JSON report", Toast.LENGTH_LONG).show()
                                }
                            )
                        }
                        NavTab.Settings -> {
                            SettingsScreen(
                                currentUser = currentUser,
                                onUpdateUser = { updated ->
                                    currentUser = updated
                                    users = users.map { if (it.id == updated.id) updated else it }
                                    Toast.makeText(context, "Profile settings updated", Toast.LENGTH_SHORT).show()
                                },
                                onLogoutClick = {
                                    currentUser = User("1", "Elon Musk", "elon", isAdmin = true)
                                    activeChannel = null
                                    currentScreen = AppScreen.LOGIN
                                    isLocked = true
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
                                        Toast.makeText(context, "Boardroom cleared", Toast.LENGTH_SHORT).show()
                                    },
                                    onToggleAdmin = { targetUser ->
                                        users = users.map { if (it.id == targetUser.id) it.copy(isAdmin = !it.isAdmin) else it }
                                    },
                                    onToggleSuspend = { targetUser ->
                                        users = users.map { if (it.id == targetUser.id) it.copy(isSuspended = !it.isSuspended) else it }
                                    },
                                    onRemoveUser = { targetUser ->
                                        users = users.filter { it.id != targetUser.id }
                                    },
                                    onUpdateBroadcast = { msg ->
                                        broadcastMessage = if (msg.isBlank()) null else msg
                                        Toast.makeText(context, "Broadcast banner updated", Toast.LENGTH_SHORT).show()
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
