package com.example.app.chat.service

import com.example.app.chat.data.ChatMessage
import com.example.app.chat.data.MessageRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    
    // List available models to find the correct one
    suspend fun listAvailableModels(apiKey: String): List<String> = withContext(Dispatchers.IO) {
        try {
            val url = "https://generativelanguage.googleapis.com/v1/models?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .get()
                .build()
            
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: throw Exception("Empty response")
            
            if (!response.isSuccessful) {
                throw Exception("API Error: ${response.code} - $responseBody")
            }
            
            val jsonResponse = JSONObject(responseBody)
            val models = jsonResponse.getJSONArray("models")
            val modelNames = mutableListOf<String>()
            
            for (i in 0 until models.length()) {
                val model = models.getJSONObject(i)
                val name = model.getString("name")
                // Extract model name (e.g., "models/gemini-1.5-flash" -> "gemini-1.5-flash")
                val modelName = name.substringAfter("models/")
                
                // Check if model supports generateContent
                val supportedMethods = model.optJSONArray("supportedGenerationMethods")
                if (supportedMethods != null) {
                    for (j in 0 until supportedMethods.length()) {
                        if (supportedMethods.getString(j) == "generateContent") {
                            modelNames.add(modelName)
                            break
                        }
                    }
                }
            }
            
            modelNames
        } catch (e: Exception) {
            throw Exception("Failed to list models: ${e.message}")
        }
    }
    
    // Will be set after listing models
    private var currentModel: String? = null
    private fun getApiUrl(model: String) = "https://generativelanguage.googleapis.com/v1/models/$model:generateContent"
    
    // Format products for AI context
    private fun formatProductsForContext(products: List<com.example.app.product.Product>): String {
        if (products.isEmpty()) {
            return "No products available in the store at the moment."
        }
        
        val productList = products.joinToString("\n\n") { product ->
            buildString {
                append("ID: ${product.id}\n")
                append("Name: ${product.name}\n")
                append("Price: ${product.price}\n")
                append("Category: ${product.category}\n")
                if (product.gender.isNotEmpty()) append("Gender: ${product.gender}\n")
                if (product.description.isNotEmpty()) append("Description: ${product.description}\n")
                if (product.sizes.isNotEmpty()) append("Available Sizes: ${product.sizes.joinToString(", ")}\n")
                if (product.colors.isNotEmpty()) append("Available Colors: ${product.colors.joinToString(", ") { it.name }}\n")
                if (product.onSale) append("On Sale: Yes\n")
                if (product.freeShipping) append("Free Shipping: Yes\n")
            }
        }
        
        return "PRODUCTS IN STORE:\n\n$productList"
    }
    
    suspend fun sendMessage(
        apiKey: String,
        message: String,
        conversationHistory: List<ChatMessage> = emptyList(),
        products: List<com.example.app.product.Product> = emptyList()
    ): String = withContext(Dispatchers.IO) {
        try {
            // Get available model if not already set
            if (currentModel == null) {
                val availableModels = listAvailableModels(apiKey)
                if (availableModels.isEmpty()) {
                    throw Exception("No models available that support generateContent")
                }
                // Prefer flash models (faster), then pro models, then any other
                currentModel = availableModels.find { it.contains("flash") } 
                    ?: availableModels.find { it.contains("pro") }
                    ?: availableModels.first()
            }
            
            // Build conversation context
            val contents = JSONArray()
            
            // Format products for context
            val productsContext = formatProductsForContext(products)
            
            // Add system context as first message if no history
            if (conversationHistory.isEmpty()) {
                val contextMessage = JSONObject()
                val contextParts = JSONArray()
                contextParts.put(JSONObject().put("text", """You are the AI shopping assistant for a clothing store mobile app. This app sells clothing items including Hoodies, Jackets, T-Shirts, Pants, Shoes, and Accessories. 

Your role is to help customers:
- Find products and get recommendations based on the available products
- Answer questions about products (sizes, colors, prices, availability)
- Provide shopping assistance
- Help with orders, shipping, and returns

You have access to the store's product database. Use the product information provided to answer customer questions accurately.

$productsContext

When customers ask about products, use the product information above to give accurate answers. Reference specific products by name and ID when relevant. If a customer asks about something not in the product list, let them know it's not currently available.

Now, the customer is asking: $message"""))
                contextMessage.put("parts", contextParts)
                contextMessage.put("role", "user")
                contents.put(contextMessage)
            } else {
                // Add conversation history (last 10 messages for context)
                conversationHistory.takeLast(10).forEach { chatMessage ->
                    val content = JSONObject()
                    val parts = JSONArray()
                    parts.put(JSONObject().put("text", chatMessage.text))
                    content.put("parts", parts)
                    content.put("role", if (chatMessage.role == MessageRole.USER) "user" else "model")
                    contents.put(content)
                }
                
                // Add current message with context reminder and products
                val currentContent = JSONObject()
                val currentParts = JSONArray()
                currentParts.put(JSONObject().put("text", """[Context: You are the shopping assistant for a clothing store app. You have access to the store's product database. Use the product information below to answer customer questions accurately.]

$productsContext

Customer question: $message"""))
                currentContent.put("parts", currentParts)
                currentContent.put("role", "user")
                contents.put(currentContent)
            }
            
            // Build request body
            val requestBody = JSONObject()
            requestBody.put("contents", contents)
            
            // Create request
            val url = "${getApiUrl(currentModel!!)}?key=$apiKey"
            val mediaType = "application/json".toMediaType()
            val body = requestBody.toString().toRequestBody(mediaType)
            
            val request = Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build()
            
            // Execute request
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: throw Exception("Empty response")
            
            if (!response.isSuccessful) {
                throw Exception("API Error: ${response.code} - $responseBody")
            }
            
            // Parse response
            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.getJSONArray("candidates")
            
            if (candidates.length() == 0) {
                throw Exception("No response from AI")
            }
            
            val candidate = candidates.getJSONObject(0)
            val content = candidate.getJSONObject("content")
            val parts = content.getJSONArray("parts")
            val text = parts.getJSONObject(0).getString("text")
            
            text
        } catch (e: Exception) {
            throw Exception("Failed to get AI response: ${e.message}")
        }
    }
}

