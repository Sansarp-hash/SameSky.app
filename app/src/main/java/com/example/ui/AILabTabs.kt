package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.ui.theme.DustyRose
import com.example.ui.theme.LightText
import com.example.ui.theme.Obsidian
import com.example.ui.theme.ShimmeringGold
import com.example.ui.theme.WarmObsidian
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.io.OutputStreamWriter
import android.util.Base64
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image

suspend fun callAdvancedGemini(
    model: String,
    prompt: String,
    systemInstruction: String? = null,
    history: List<Pair<String, String>> = emptyList(), // Role -> Text
    useThinking: Boolean = false,
    useGrounding: Boolean = false
): String = withContext(Dispatchers.IO) {
    val apiKey = BuildConfig.GEMINI_API_KEY
    if (apiKey.isEmpty()) return@withContext "API Key is missing."
    
    try {
        val url = URL("https://generativelanguage.googleapis.com/v1beta/models/\$model:generateContent?key=\$apiKey")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json")
        conn.doOutput = true

        val root = JSONObject()
        val contents = JSONArray()

        // History
        for (msg in history) {
            val contentObj = JSONObject()
            contentObj.put("role", msg.first)
            val parts = JSONArray()
            val part = JSONObject()
            part.put("text", msg.second)
            parts.put(part)
            contentObj.put("parts", parts)
            contents.put(contentObj)
        }

        // Current message
        val currentContent = JSONObject()
        currentContent.put("role", "user")
        val currentParts = JSONArray()
        val currentPart = JSONObject()
        currentPart.put("text", prompt)
        currentParts.put(currentPart)
        currentContent.put("parts", currentParts)
        contents.put(currentContent)

        root.put("contents", contents)

        if (useThinking) {
            val genConfig = JSONObject()
            genConfig.put("thinkingLevel", "HIGH")
            // Do not set maxOutputTokens for thinking
            root.put("generationConfig", genConfig)
        }

        if (systemInstruction != null) {

            val sysInst = JSONObject()
            val sysParts = JSONArray()
            val sysPart = JSONObject()
            sysPart.put("text", systemInstruction)
            sysParts.put(sysPart)
            sysInst.put("parts", sysParts)
            root.put("systemInstruction", sysInst)
        }

        if (useGrounding) {
            val tools = JSONArray()
            val tool = JSONObject()
            tool.put("googleSearch", JSONObject())
            val mapTool = JSONObject()
            val googleMaps = JSONObject()
            mapTool.put("googleMaps", googleMaps)
            tools.put(mapTool)
            tools.put(tool)
            root.put("tools", tools)
        }

        val writer = OutputStreamWriter(conn.outputStream)
        writer.write(root.toString())
        writer.flush()
        writer.close()

        val responseCode = conn.responseCode
        if (responseCode == 200) {
            val response = conn.inputStream.bufferedReader().use { it.readText() }
            val jsonResponse = JSONObject(response)
            val candidates = jsonResponse.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    return@withContext parts.getJSONObject(0).optString("text", "No text")
                }
            }
            return@withContext "Empty response"
        } else {
            val error = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: "Unknown error"
            return@withContext "Error \$responseCode: \$error"
        }
    } catch (e: Exception) {
        return@withContext "Exception: \${e.message}"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeminiChatTab() {
    val coroutineScope = rememberCoroutineScope()
    var messages by remember { mutableStateOf(listOf<Pair<String, String>>()) }
    var inputText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    
    var selectedModel by remember { mutableStateOf("gemini-3.5-flash") }
    var useThinking by remember { mutableStateOf(false) }
    var useGrounding by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Controls
        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = selectedModel == "gemini-3.5-flash", onClick = { selectedModel = "gemini-3.5-flash" }, label = { Text("Flash") })
            FilterChip(selected = selectedModel == "gemini-3.1-pro-preview", onClick = { selectedModel = "gemini-3.1-pro-preview" }, label = { Text("Pro") })
            FilterChip(selected = selectedModel == "gemini-3.1-flash-lite", onClick = { selectedModel = "gemini-3.1-flash-lite" }, label = { Text("Lite") })
            FilterChip(selected = useThinking, onClick = { useThinking = !useThinking }, label = { Text("Thinking (Pro)") })
            FilterChip(selected = useGrounding, onClick = { useGrounding = !useGrounding }, label = { Text("Grounding") })
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Chat History
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(messages) { msg ->
                val isUser = msg.first == "user"
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .background(if (isUser) ShimmeringGold else WarmObsidian, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                            .fillMaxWidth(0.85f)
                    ) {
                        Text(text = msg.second, color = if (isUser) Obsidian else LightText, fontSize = 14.sp)
                    }
                }
            }
        }
        
        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), color = ShimmeringGold)
        }
        
        // Input
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = LightText, unfocusedTextColor = LightText),
                placeholder = { Text("Ask Gemini...", color = Color.Gray) }
            )
            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        val prompt = inputText
                        messages = messages + Pair("user", prompt)
                        inputText = ""
                        isLoading = true
                        coroutineScope.launch {
                            val response = callAdvancedGemini(
                                model = selectedModel,
                                prompt = prompt,
                                history = messages.dropLast(1),
                                useThinking = useThinking && selectedModel == "gemini-3.1-pro-preview",
                                useGrounding = useGrounding
                            )
                            messages = messages + Pair("model", response)
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, "Send", tint = ShimmeringGold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageGenTab() {
    val coroutineScope = rememberCoroutineScope()
    var prompt by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf("Ready to generate images.") }
    
    var selectedModel by remember { mutableStateOf("gemini-3.1-flash-image-preview") }
    var selectedRatio by remember { mutableStateOf("1:1") }
    var selectedSize by remember { mutableStateOf("1K") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text("Model", color = ShimmeringGold, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
            FilterChip(selected = selectedModel == "gemini-3.1-flash-image-preview", onClick = { selectedModel = "gemini-3.1-flash-image-preview" }, label = { Text("Flash Image") })
            FilterChip(selected = selectedModel == "gemini-3-pro-image-preview", onClick = { selectedModel = "gemini-3-pro-image-preview" }, label = { Text("Pro Image") })
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text("Aspect Ratio", color = ShimmeringGold, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
            listOf("1:1", "2:3", "3:2", "3:4", "4:3", "9:16", "16:9", "21:9").forEach { ratio ->
                FilterChip(selected = selectedRatio == ratio, onClick = { selectedRatio = ratio }, label = { Text(ratio) })
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text("Size", color = ShimmeringGold, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("1K", "2K", "4K").forEach { size ->
                FilterChip(selected = selectedSize == size, onClick = { selectedSize = size }, label = { Text(size) })
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = prompt,
            onValueChange = { prompt = it },
            modifier = Modifier.fillMaxWidth().height(100.dp),
            placeholder = { Text("Describe the image to generate...", color = Color.Gray) },
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = LightText, unfocusedTextColor = LightText)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (prompt.isNotBlank()) {
                    isLoading = true
                    resultText = "Generating image using \$selectedModel (\$selectedSize, \$selectedRatio)..."
                    coroutineScope.launch {
                        // Using generateContent as a placeholder for image generation API call
                        val response = callAdvancedGemini(
                            model = selectedModel,
                            prompt = "Generate an image of \$prompt. Aspect ratio: \$selectedRatio, Size: \$selectedSize",
                            useGrounding = false
                        )
                        resultText = "Image API Response: \$response (Note: rendering base64 images requires decoding inlineData from REST API)"
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = ShimmeringGold, contentColor = Obsidian),
            enabled = !isLoading
        ) {
            Text(if (isLoading) "Generating..." else "Generate Image")
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Text(resultText, color = LightText, fontSize = 14.sp)
        
        // Placeholder for the generated image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(top = 16.dp)
                .background(WarmObsidian, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = ShimmeringGold)
            } else {
                Text("Image will appear here", color = Color.Gray)
            }
        }
    }
}

@Composable
fun VoiceTab() {
    var transcript by remember { mutableStateOf("Press the microphone to speak, and Gemini will transcribe and analyze it.") }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(transcript, color = LightText, fontSize = 16.sp, modifier = Modifier.padding(bottom = 32.dp))
        
        if (isLoading) {
            CircularProgressIndicator(color = ShimmeringGold)
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        Box(modifier = Modifier.size(80.dp)) {
            VoiceInputButton(onResult = { result ->
                transcript = "Transcribing: \$result..."
                isLoading = true
                coroutineScope.launch {
                    val analysis = callAdvancedGemini(
                        model = "gemini-3.5-flash",
                        prompt = "Please format, transcribe, and analyze the following voice input: \$result"
                    )
                    transcript = "Transcription:\n\$result\n\nGemini Analysis:\n\$analysis"
                    isLoading = false
                }
            })
        }
        Text("Transcribe with gemini-3.5-flash", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
fun FirebaseTab() {
    // A placeholder for Firebase Auth and Firestore integration
    var userStatus by remember { mutableStateOf("Not Logged In") }
    var firestoreStatus by remember { mutableStateOf("Checking connection...") }
    
    LaunchedEffect(Unit) {
        // Here we would check Firebase.auth.currentUser
        // Since we may not have google-services.json loaded properly, we simulate or try/catch it.
        try {
            val authClass = Class.forName("com.google.firebase.auth.FirebaseAuth")
            val getInstance = authClass.getMethod("getInstance")
            val auth = getInstance.invoke(null)
            val getCurrentUser = authClass.getMethod("getCurrentUser")
            val currentUser = getCurrentUser.invoke(auth)
            
            if (currentUser != null) {
                userStatus = "Logged in as Anonymous / User"
            } else {
                userStatus = "Not Logged In (Firebase SDK initialized)"
            }
            firestoreStatus = "Firestore SDK ready."
        } catch (e: Exception) {
            userStatus = "Firebase Auth not available"
            firestoreStatus = "Please configure google-services.json to use Firestore."
        }
    }
    
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Firebase Integration", color = ShimmeringGold, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(colors = CardDefaults.cardColors(containerColor = WarmObsidian), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Authentication Status:", color = Color.Gray, fontSize = 14.sp)
                Text(userStatus, color = LightText, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp, bottom = 16.dp))
                
                Button(onClick = { /* Implement Google Sign In */ }, colors = ButtonDefaults.buttonColors(containerColor = ShimmeringGold, contentColor = Obsidian)) {
                    Text("Google Sign-In")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(colors = CardDefaults.cardColors(containerColor = WarmObsidian), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Firestore Database:", color = Color.Gray, fontSize = 14.sp)
                Text(firestoreStatus, color = LightText, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp, bottom = 16.dp))
                
                Button(onClick = { /* Implement Firestore Sync */ }, colors = ButtonDefaults.buttonColors(containerColor = ShimmeringGold, contentColor = Obsidian)) {
                    Text("Sync Data to Firestore")
                }
            }
        }
    }
}
