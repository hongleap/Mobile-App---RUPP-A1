package com.example.app.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.ui.AppColors
import com.example.app.ui.AppDimensions
import com.example.app.chat.data.ChatMessage
import com.example.app.chat.data.MessageRole
import com.example.app.chat.service.GeminiService
import com.example.app.data.ProductRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onBackClick: () -> Unit = {},
    apiKey: String = "" // User will need to add their API key
) {
    var messageText by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf<List<ChatMessage>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    
    // Load products from repository
    val products by ProductRepository.products.collectAsState()
    
    // Load products when screen is first shown
    LaunchedEffect(Unit) {
        if (products.isEmpty()) {
            ProductRepository.loadProducts()
        }
    }
    
    // Auto-scroll to bottom when new message arrives
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        // Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppDimensions.SpacingL),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.SpacingM)
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = AppColors.TextPrimary
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "AI Assistant",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = AppColors.TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Powered by AI",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = AppColors.TextSecondary
                        )
                    )
                }
            }
        }
        
        // Error message
        errorMessage?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppDimensions.SpacingL, vertical = AppDimensions.SpacingS),
                colors = CardDefaults.cardColors(containerColor = AppColors.AccentRed.copy(alpha = 0.1f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = "⚠️ $error",
                    modifier = Modifier.padding(AppDimensions.SpacingM),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = AppColors.AccentRed
                    )
                )
            }
        }
        
        // Messages list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            state = listState,
            contentPadding = PaddingValues(horizontal = AppDimensions.SpacingL, vertical = AppDimensions.SpacingL),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.SpacingM)
        ) {
            if (messages.isEmpty()) {
                item {
                    WelcomeMessage()
                }
            }
            
            items(messages) { message ->
                MessageBubble(message = message)
            }
            
            if (isLoading) {
                item {
                    LoadingIndicator()
                }
            }
        }
        
        // Input field
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppDimensions.SpacingM),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.SpacingS)
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type your message...", style = MaterialTheme.typography.bodyMedium) },
                    singleLine = false,
                    maxLines = 4,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = AppColors.SurfaceVariant,
                        unfocusedContainerColor = AppColors.SurfaceVariant,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(AppDimensions.RadiusXL)
                )
                
                IconButton(
                    onClick = {
                        if (messageText.isNotBlank() && !isLoading && apiKey.isNotBlank()) {
                            val userMessage = ChatMessage(
                                text = messageText,
                                role = MessageRole.USER
                            )
                            messages = messages + userMessage
                            val currentMessage = messageText
                            messageText = ""
                            isLoading = true
                            errorMessage = null
                            
                            scope.launch {
                                try {
                                    val response = GeminiService.sendMessage(
                                        apiKey = apiKey,
                                        message = currentMessage,
                                        conversationHistory = messages,
                                        products = products
                                    )
                                    messages = messages + ChatMessage(
                                        text = response,
                                        role = MessageRole.ASSISTANT
                                    )
                                } catch (e: Exception) {
                                    errorMessage = e.message ?: "Failed to get response"
                                    messages = messages.dropLast(1) // Remove user message on error
                                } finally {
                                    isLoading = false
                                }
                            }
                        } else if (apiKey.isBlank()) {
                            errorMessage = "Please add your Gemini API key in ChatScreen.kt"
                        }
                    },
                    enabled = messageText.isNotBlank() && !isLoading
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = if (messageText.isNotBlank() && !isLoading) AppColors.Primary else AppColors.TextTertiary
                    )
                }
            }
        }
    }
}

@Composable
private fun WelcomeMessage() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(AppDimensions.RadiusL)
    ) {
        Column(
            modifier = Modifier.padding(AppDimensions.SpacingL),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "👋",
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(modifier = Modifier.height(AppDimensions.SpacingM))
            Text(
                text = "Hi! I'm your AI Assistant",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = AppColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(AppDimensions.SpacingS))
            Text(
                text = "I can help you with:\n• Product recommendations\n• Order assistance\n• General questions",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AppColors.TextSecondary,
                    lineHeight = 20.sp
                )
            )
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    val isUser = message.role == MessageRole.USER
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier.widthIn(max = 280.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) AppColors.Primary else AppColors.Surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            shape = RoundedCornerShape(
                topStart = AppDimensions.RadiusM,
                topEnd = AppDimensions.RadiusM,
                bottomStart = if (isUser) AppDimensions.RadiusM else AppDimensions.RadiusXS,
                bottomEnd = if (isUser) AppDimensions.RadiusXS else AppDimensions.RadiusM
            )
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(AppDimensions.SpacingM),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (isUser) AppColors.TextOnPrimary else AppColors.TextPrimary,
                    lineHeight = 20.sp
                )
            )
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Card(
            modifier = Modifier.widthIn(max = 80.dp),
            colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            shape = RoundedCornerShape(AppDimensions.RadiusL)
        ) {
            Row(
                modifier = Modifier.padding(AppDimensions.SpacingM),
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.SpacingXS)
            ) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .size(AppDimensions.IconXS)
                            .clip(RoundedCornerShape(AppDimensions.RadiusXS))
                            .background(AppColors.Primary)
                    ) {
                        // Animated dots would go here
                    }
                }
            }
        }
    }
}

