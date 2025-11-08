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
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF262626)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "AI Assistant",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF262626)
                    )
                    Text(
                        text = "Powered by AI",
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )
                }
            }
        }
        
        // Error message
        errorMessage?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = "⚠️ $error",
                    modifier = Modifier.padding(12.dp),
                    fontSize = 12.sp,
                    color = Color(0xFFD32F2F)
                )
            }
        }
        
        // Messages list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            state = listState,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type your message...", fontSize = 14.sp) },
                    singleLine = false,
                    maxLines = 4,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF5F5F5),
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(24.dp)
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
                        tint = if (messageText.isNotBlank() && !isLoading) Color(0xFF6B5BFF) else Color(0xFFCCCCCC)
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "👋",
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Hi! I'm your AI Assistant",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF262626)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "I can help you with:\n• Product recommendations\n• Order assistance\n• General questions",
                fontSize = 14.sp,
                color = Color(0xFF666666),
                lineHeight = 20.sp
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
                containerColor = if (isUser) Color(0xFF6B5BFF) else Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            )
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(12.dp),
                fontSize = 14.sp,
                color = if (isUser) Color.White else Color(0xFF262626),
                lineHeight = 20.sp
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
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF6B5BFF))
                    ) {
                        // Animated dots would go here
                    }
                }
            }
        }
    }
}

