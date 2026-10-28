package tivana.chat.switchapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tivana.chat.switchapp.data.User
import tivana.chat.switchapp.ui.components.ButtonVariant
import tivana.chat.switchapp.ui.components.SectionHeading
import tivana.chat.switchapp.ui.components.SwitchButton
import tivana.chat.switchapp.ui.components.Tag
import tivana.chat.switchapp.ui.components.TagVariant
import tivana.chat.switchapp.ui.theme.SWITCHTheme
import tivana.chat.switchapp.ui.theme.SwitchTheme
import tivana.chat.switchapp.ui.theme.SwitchTypography

@Composable
fun ChatBotScreen(
    currentUserId: String,
    users: List<User>,
    totalMessagesCount: Int,
    onClearBoardroom: () -> Unit,
    onToggleAdmin: (User) -> Unit,
    onToggleSuspend: (User) -> Unit,
    onRemoveUser: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SwitchTheme.colors.Background)
            .padding(16.dp)
    ) {
        item {
            SectionHeading(text = "Admin Console")

            // Stats row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SwitchTheme.colors.Line, RoundedCornerShape(0.dp))
                    .background(SwitchTheme.colors.Panel, RoundedCornerShape(0.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatItem(label = "Users", value = users.size.toString())
                StatItem(label = "Messages", value = totalMessagesCount.toString())
                StatItem(label = "Boardroom", value = "Active")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // CLEAR BOARDROOM button
            SwitchButton(
                text = "Clear Boardroom",
                onClick = onClearBoardroom,
                variant = ButtonVariant.DANGER
            )

            Spacer(modifier = Modifier.height(24.dp))
            SectionHeading(text = "User Management")
        }

        items(users) { user ->
            val isMe = user.id == currentUserId

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .border(1.dp, SwitchTheme.colors.Line, RoundedCornerShape(0.dp))
                    .background(SwitchTheme.colors.Panel, RoundedCornerShape(0.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = user.fullName,
                                style = SwitchTypography.body.copy(fontWeight = FontWeight.SemiBold),
                                color = SwitchTheme.colors.TextPrimary
                            )
                            if (user.isAdmin) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Tag(text = "Admin", variant = TagVariant.BLUE)
                            }
                            if (user.isSuspended) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Tag(text = "Suspended", variant = TagVariant.DANGER)
                            }
                        }
                        Text(
                            text = "@${user.username}${if (isMe) " (You)" else ""}",
                            style = SwitchTypography.secondary,
                            color = SwitchTheme.colors.TextMuted
                        )
                    }
                }

                if (!isMe) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            SwitchButton(
                                text = if (user.isAdmin) "Demote" else "Make Admin",
                                onClick = { onToggleAdmin(user) },
                                variant = ButtonVariant.SECONDARY
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            SwitchButton(
                                text = if (user.isSuspended) "Restore" else "Suspend",
                                onClick = { onToggleSuspend(user) },
                                variant = ButtonVariant.DANGER
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            SwitchButton(
                                text = "Remove",
                                onClick = { onRemoveUser(user) },
                                variant = ButtonVariant.DANGER
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = SwitchTypography.logo.copy(fontSize = 18.sp),
            color = SwitchTheme.colors.TextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label.uppercase(),
            style = SwitchTypography.bodySmall,
            color = SwitchTheme.colors.TextMuted
        )
    }
}

@Preview(
    name = "ChatBot Screen Preview",
    showBackground = true,
    backgroundColor = 0xFF000000,
    device = Devices.PIXEL_7
)
@Composable
fun ChatBotScreenPreview() {
    val sampleUsers = listOf(
        User("1", "Elon Musk", "elon", true, false),
        User("2", "Gwynne Shotwell", "gwynne", false, false),
        User("3", "Hans Koenigsmann", "hans", false, true),
        User("4", "Justyna", "justyna", false, false)
    )
    SWITCHTheme {
        ChatBotScreen(
            currentUserId = "1",
            users = sampleUsers,
            totalMessagesCount = 142,
            onClearBoardroom = {},
            onToggleAdmin = {},
            onToggleSuspend = {},
            onRemoveUser = {}
        )
    }
}
