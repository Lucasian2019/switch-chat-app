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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tivana.chat.switchapp.data.BotMessage
import tivana.chat.switchapp.ui.components.ButtonVariant
import tivana.chat.switchapp.ui.components.SectionHeading
import tivana.chat.switchapp.ui.components.SwitchButton
import tivana.chat.switchapp.ui.components.SwitchTextField
import tivana.chat.switchapp.ui.components.Tag
import tivana.chat.switchapp.ui.components.TagVariant
import tivana.chat.switchapp.ui.theme.SWITCHTheme
import tivana.chat.switchapp.ui.theme.SwitchTheme
import tivana.chat.switchapp.ui.theme.SwitchTypography

@Composable
fun ChatBotScreen(
    modifier: Modifier = Modifier
) {
    var inputMessage by remember { mutableStateOf("") }
    var messages by remember {
        mutableStateOf(
            listOf(
                BotMessage("1", "Hello! I am Tivana AI. Ask me anything about engineering, code optimization, or system design.", false, "Just now", "Tivana AI")
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SwitchTheme.colors.Background)
            .padding(16.dp)
    ) {
        SectionHeading(text = "Tivana AI Assistant")
        Spacer(modifier = Modifier.height(12.dp))

        // Messages list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .border(1.dp, SwitchTheme.colors.Line, RoundedCornerShape(0.dp))
                .background(SwitchTheme.colors.Panel, RoundedCornerShape(0.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { msg ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (msg.isUser) SwitchTheme.colors.Background else SwitchTheme.colors.Line.copy(alpha = 0.2f), RoundedCornerShape(0.dp))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (msg.isUser) "You" else "Tivana AI",
                            style = SwitchTypography.body.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold),
                            color = SwitchTheme.colors.TextPrimary
                        )
                        Tag(text = msg.timestamp, variant = TagVariant.BLUE)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = msg.text,
                        style = SwitchTypography.secondary,
                        color = SwitchTheme.colors.TextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Input section (vertical)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SwitchTextField(
                value = inputMessage,
                onValueChange = { inputMessage = it },
                label = "Ask Tivana AI...",
                singleLine = false,
                maxLines = 3
            )
            SwitchButton(
                text = "Send Message",
                onClick = {
                    if (inputMessage.isNotBlank()) {
                        val userMsg = BotMessage("u_${System.currentTimeMillis()}", inputMessage, true, "Just now", "Tivana AI")
                        val prompt = inputMessage
                        inputMessage = ""

                        val botReplyText = "Tivana AI analysis on \"$prompt\": Neural reasoning engine processed successfully. System telemetry and architecture nominal."
                        val botMsg = BotMessage("b_${System.currentTimeMillis()}", botReplyText, false, "Just now", "Tivana AI")
                        messages = messages + userMsg + botMsg
                    }
                },
                variant = ButtonVariant.PRIMARY
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, device = Devices.PIXEL_7)
@Composable
fun ChatBotScreenPreview() {
    SWITCHTheme {
        ChatBotScreen()
    }
}
