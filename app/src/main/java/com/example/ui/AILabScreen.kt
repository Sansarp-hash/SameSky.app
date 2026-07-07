package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.ui.theme.Obsidian
import com.example.ui.theme.ShimmeringGold
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AILabScreen(navController: NavHostController) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Chatbot", "Image Gen", "Voice", "Firebase")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gemini AI Lab", color = ShimmeringGold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = ShimmeringGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Obsidian)
            )
        },
        containerColor = Obsidian
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Obsidian,
                contentColor = ShimmeringGold
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize().weight(1f)) {
                when (selectedTab) {
                    0 -> GeminiChatTab()
                    1 -> ImageGenTab()
                    2 -> VoiceTab()
                    3 -> FirebaseTab()
                }
            }
        }
    }
}
