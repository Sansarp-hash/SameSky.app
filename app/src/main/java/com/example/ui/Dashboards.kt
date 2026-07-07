package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Flag
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import kotlinx.coroutines.launch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.DashboardViewModel
import com.example.ui.theme.*
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag

data class KanbanTask(
    val id: String,
    val title: String,
    val description: String,
    val priority: String, // "High", "Medium", "Low"
    val status: String // "To Do", "In Progress", "Done"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(navController: NavHostController, viewModel: DashboardViewModel = viewModel()) {
    val metrics by viewModel.adminMetrics.collectAsState()
    val entries by viewModel.contentEntries.collectAsState()
    val supabaseSeriesList by viewModel.supabaseSeries.collectAsState()
    val isCreatingSeries by viewModel.isCreatingSeries.collectAsState()
    val createSeriesError by viewModel.createSeriesError.collectAsState()

    val selectedIds = remember { mutableStateListOf<String>() }
    
    // Fake reports for demo
    val reports = remember { mutableStateListOf(
        Triple("r1", "Spam post in Community Feed", "Pending"),
        Triple("r2", "Inappropriate comment on MBTI post", "Flagged")
    ) }

    // Kanban board state
    val kanbanTasks = remember { mutableStateListOf(
        KanbanTask("t1", "Review reported community posts", "Audit and inspect the flagged items in the feed", "High", "To Do"),
        KanbanTask("t2", "Investigate spam coins activity", "Check transactions that exceed regular threshold", "Medium", "In Progress"),
        KanbanTask("t3", "Update community guidelines", "Add specific rules on GL content interactions", "Low", "Done"),
        KanbanTask("t4", "Audit MBTI accuracy reports", "Verify accuracy calculations with the matching algorithm", "Low", "To Do"),
        KanbanTask("t5", "Moderation policy draft", "Write the guidelines for newly onboarded moderators", "High", "In Progress")
    ) }

    var activeDashboardTab by remember { mutableStateOf(0) } // 0 = Content Audit, 1 = Kanban Board, 2 = Supabase Series
    var showAddTaskForm by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskDesc by remember { mutableStateOf("") }
    var newTaskPriority by remember { mutableStateOf("Medium") }
    var newTaskStatus by remember { mutableStateOf("To Do") }

    // State for creating a new seed entry to test audit features
    var seedTitle by remember { mutableStateOf("") }
    var seedContent by remember { mutableStateOf("") }
    var seedCategory by remember { mutableStateOf("General") }
    var seedMbti by remember { mutableStateOf("") }
    var seedSunSign by remember { mutableStateOf("") }
    var showSeedForm by remember { mutableStateOf(false) }

    // State for Supabase series upload form
    var seriesTitle by remember { mutableStateOf("") }
    var seriesDescription by remember { mutableStateOf("") }
    var seriesImageUrl by remember { mutableStateOf("") }
    var seriesPriority by remember { mutableStateOf("Medium") }

    LaunchedEffect(Unit) {
        viewModel.fetchAdminMetrics()
        viewModel.fetchContentEntries()
        viewModel.fetchSupabaseSeries()
    }

    Column(modifier = Modifier.fillMaxSize().background(Obsidian)) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ShimmeringGold)
            }
            Text(
                "Moderator Dashboard",
                color = ShimmeringGold,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        // Custom Premium Tab Selector Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .background(WarmObsidian, RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (activeDashboardTab == 0) CosmicViolet else Color.Transparent)
                    .clickable { activeDashboardTab = 0 }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Content Audit",
                    color = if (activeDashboardTab == 0) Color.White else LightText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (activeDashboardTab == 1) CosmicViolet else Color.Transparent)
                    .clickable { activeDashboardTab = 1 }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Kanban Board",
                    color = if (activeDashboardTab == 1) Color.White else LightText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (activeDashboardTab == 2) CosmicViolet else Color.Transparent)
                    .clickable { activeDashboardTab = 2 }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Supabase Series",
                    color = if (activeDashboardTab == 2) Color.White else LightText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }

        if (activeDashboardTab == 0) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text("Community Engagement Metrics", color = LightText, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }

                // Summary Metrics block
                if (metrics == null) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = ShimmeringGold)
                        }
                    }
                } else {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = WarmObsidian)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Coin Volume", color = MutedText, fontSize = 11.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(metrics?.totalCoinVolume?.toString() ?: "0.0", color = ShimmeringGold, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = WarmObsidian)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Subs Count", color = MutedText, fontSize = 11.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(metrics?.subscriptionCount?.toString() ?: "0", color = LightText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = WarmObsidian)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Raffle Entries", color = MutedText, fontSize = 11.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(metrics?.raffleParticipationRates?.toString() ?: "0", color = CosmicViolet, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                item {
                    HorizontalDivider(color = WarmObsidian, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("User Reports & Flags", color = ShimmeringGold, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                
                items(reports) { report ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = WarmObsidian),
                        border = BorderStroke(1.dp, if (report.third == "Flagged") DustyRose else CosmicViolet)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Report ID: ${report.first}", color = MutedText, fontSize = 11.sp)
                                Text(report.second, color = LightText, style = MaterialTheme.typography.bodyMedium)
                                Text("Status: ${report.third}", color = if (report.third == "Flagged") DustyRose else ShimmeringGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            IconButton(onClick = {
                                val idx = reports.indexOf(report)
                                if (idx != -1) {
                                    reports[idx] = report.copy(third = if (report.third == "Pending") "Flagged" else "Resolved")
                                }
                            }) {
                                Icon(Icons.Filled.Flag, "Flag", tint = if (report.third == "Flagged") DustyRose else MutedText)
                            }
                        }
                    }
                }

                // Celestial content auditing section header
                item {
                    HorizontalDivider(color = WarmObsidian, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Content Auditing",
                                color = ShimmeringGold,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Audit flagged posts or delete content entries in bulk",
                                color = MutedText,
                                fontSize = 11.sp
                            )
                        }
                        Button(
                            onClick = { showSeedForm = !showSeedForm },
                            colors = ButtonDefaults.buttonColors(containerColor = if (showSeedForm) DustyRose else WarmObsidian)
                        ) {
                            Text(if (showSeedForm) "Close Form" else "Seed Entry", color = if (showSeedForm) Color.White else ShimmeringGold, fontSize = 12.sp)
                        }
                    }
                }

                // Seed form block
                if (showSeedForm) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = WarmObsidian),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                                Text("Create Seed Content Entry", color = ShimmeringGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = seedTitle,
                                    onValueChange = { seedTitle = it },
                                    label = { Text("Title", color = MutedText) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ShimmeringGold, unfocusedBorderColor = Obsidian)
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = seedContent,
                                    onValueChange = { seedContent = it },
                                    label = { Text("Content body", color = MutedText) },
                                    modifier = Modifier.fillMaxWidth().height(80.dp),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ShimmeringGold, unfocusedBorderColor = Obsidian)
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = seedCategory,
                                        onValueChange = { seedCategory = it },
                                        label = { Text("Category", color = MutedText) },
                                        modifier = Modifier.weight(1f),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ShimmeringGold, unfocusedBorderColor = Obsidian)
                                    )
                                    OutlinedTextField(
                                        value = seedMbti,
                                        onValueChange = { seedMbti = it },
                                        label = { Text("Target MBTI", color = MutedText) },
                                        modifier = Modifier.weight(1f),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ShimmeringGold, unfocusedBorderColor = Obsidian)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = seedSunSign,
                                    onValueChange = { seedSunSign = it },
                                    label = { Text("Target Sun Sign", color = MutedText) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ShimmeringGold, unfocusedBorderColor = Obsidian)
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        if (seedTitle.isNotBlank() && seedContent.isNotBlank()) {
                                            viewModel.createContentEntry(
                                                seedTitle,
                                                seedContent,
                                                seedCategory,
                                                seedMbti.ifBlank { null },
                                                seedSunSign.ifBlank { null }
                                            )
                                            seedTitle = ""
                                            seedContent = ""
                                            seedMbti = ""
                                            seedSunSign = ""
                                            showSeedForm = false
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = ShimmeringGold, contentColor = Obsidian)
                                ) {
                                    Text("Publish Content Entry", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Bulk actions banner when items are selected
                if (selectedIds.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = DustyRose.copy(alpha = 0.25f)),
                            border = BorderStroke(1.dp, DustyRose)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "${selectedIds.size} entries selected",
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                               )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    TextButton(
                                        onClick = { selectedIds.clear() }
                                    ) {
                                        Text("Clear Selection", color = LightText, fontSize = 12.sp)
                                    }
                                    Button(
                                        onClick = {
                                            viewModel.bulkDeleteContentEntries(selectedIds.toList())
                                            selectedIds.clear()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = DustyRose)
                                    ) {
                                        Text("Bulk Delete", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // Content entries list
                if (entries.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = WarmObsidian)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.AutoMirrored.Filled.List, "Empty", tint = SoftViolet, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("No celestial content entries found.", color = LightText, fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Use the 'Seed Entry' form to publish test content for auditing.", color = MutedText, fontSize = 12.sp, textAlign = TextAlign.Center)
                            }
                        }
                    }
                } else {
                    items(entries) { entry ->
                        val isSelected = selectedIds.contains(entry.id)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = if (isSelected) WarmObsidian.copy(alpha = 0.8f) else WarmObsidian),
                            border = if (isSelected) BorderStroke(1.5.dp, ShimmeringGold) else null,
                            onClick = {
                                if (isSelected) selectedIds.remove(entry.id) else selectedIds.add(entry.id)
                            }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = isSelected,
                                            onCheckedChange = { checked ->
                                                if (checked == true) selectedIds.add(entry.id) else selectedIds.remove(entry.id)
                                            },
                                            colors = CheckboxDefaults.colors(checkedColor = ShimmeringGold, uncheckedColor = MutedText)
                                        )
                                        Text(
                                            entry.title,
                                            color = LightText,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .background(SoftViolet.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(entry.category, color = SoftViolet, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(entry.content, color = MutedText, fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(12.dp))

                                // Display metadata targets if present
                                if (entry.mbti != null || entry.sunSign != null) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        entry.mbti?.let { m ->
                                            Text("Target MBTI: $m", color = ShimmeringGold, fontSize = 11.sp)
                                        }
                                        entry.sunSign?.let { s ->
                                            Text("Target Sign: $s", color = BlushPink, fontSize = 11.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                }

                                // Show Flagged Badge and toggler
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (entry.flagged) {
                                        Column {
                                            Box(
                                                modifier = Modifier
                                                    .background(DustyRose.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                    .border(0.5.dp, DustyRose, RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Filled.Flag, "Flagged", tint = DustyRose, modifier = Modifier.size(12.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("FLAGGED", color = DustyRose, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                            entry.flagReason?.let { reason ->
                                                Text(reason, color = DustyRose.copy(alpha = 0.8f), fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                                            }
                                        }
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .background(Color.DarkGray.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text("SECURE QUALITY", color = LightText, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    TextButton(
                                        onClick = {
                                            viewModel.flagContentEntry(
                                                id = entry.id,
                                                flagged = !entry.flagged,
                                                reason = if (!entry.flagged) "Reported for redundant content quality" else null
                                            )
                                        }
                                    ) {
                                        Text(
                                            if (entry.flagged) "Unflag Entry" else "Flag Entry",
                                            color = if (entry.flagged) LightText else DustyRose,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (activeDashboardTab == 1) {
            // Kanban Task Board Tab!
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                // Header with Add Task Toggle Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Task Backlog & Board", color = ShimmeringGold, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Collaborative task management for moderators", color = MutedText, fontSize = 11.sp)
                    }
                    Button(
                        onClick = { showAddTaskForm = !showAddTaskForm },
                        colors = ButtonDefaults.buttonColors(containerColor = if (showAddTaskForm) DustyRose else CosmicViolet)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (showAddTaskForm) "Close" else "Add Task", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Add Task Form (AnimatedVisibility)
                AnimatedVisibility(
                    visible = showAddTaskForm,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = WarmObsidian),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, ShimmeringGold.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Create Kanban Task", color = ShimmeringGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            OutlinedTextField(
                                value = newTaskTitle,
                                onValueChange = { newTaskTitle = it },
                                label = { Text("Task Title", color = MutedText) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ShimmeringGold, unfocusedBorderColor = Obsidian)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            OutlinedTextField(
                                value = newTaskDesc,
                                onValueChange = { newTaskDesc = it },
                                label = { Text("Description", color = MutedText) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ShimmeringGold, unfocusedBorderColor = Obsidian)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            // Priority Selection
                            Text("Priority Level", color = LightText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("High", "Medium", "Low").forEach { prio ->
                                    val isPrioSelected = newTaskPriority == prio
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isPrioSelected) DustyRose else Obsidian)
                                            .clickable { newTaskPriority = prio }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(prio, color = if (isPrioSelected) Color.White else MutedText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            // Status Selection
                            Text("Initial Stage", color = LightText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("To Do", "In Progress", "Done").forEach { stat ->
                                    val isStatSelected = newTaskStatus == stat
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isStatSelected) SoftViolet else Obsidian)
                                            .clickable { newTaskStatus = stat }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(stat, color = if (isStatSelected) Color.White else MutedText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    if (newTaskTitle.isNotBlank()) {
                                        val newTask = KanbanTask(
                                            id = "t_${System.currentTimeMillis()}",
                                            title = newTaskTitle,
                                            description = newTaskDesc,
                                            priority = newTaskPriority,
                                            status = newTaskStatus
                                        )
                                        kanbanTasks.add(newTask)
                                        newTaskTitle = ""
                                        newTaskDesc = ""
                                        showAddTaskForm = false
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = ShimmeringGold, contentColor = Obsidian)
                            ) {
                                Text("Add Task to Board", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                
                // Horizontally Scrollable Kanban Columns
                LazyRow(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    val columnsList = listOf("To Do", "In Progress", "Done")
                    items(columnsList) { col ->
                        val filteredTasks = kanbanTasks.filter { it.status == col }
                        
                        Card(
                            modifier = Modifier
                                .width(280.dp)
                                .fillMaxHeight(),
                            colors = CardDefaults.cardColors(containerColor = WarmObsidian),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, CosmicViolet.copy(alpha = 0.2f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp).fillMaxSize()) {
                                // Column Header
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .background(
                                                    color = when (col) {
                                                        "To Do" -> ShimmeringGold
                                                        "In Progress" -> BlushPink
                                                        else -> SoftViolet
                                                    },
                                                    shape = CircleShape
                                                )
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            col,
                                            color = LightText,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(Obsidian, CircleShape)
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            filteredTasks.size.toString(),
                                            color = ShimmeringGold,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                if (filteredTasks.isEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f)
                                            .background(Obsidian.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "No tasks in this column",
                                            color = MutedText,
                                            fontSize = 12.sp,
                                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                        )
                                    }
                                } else {
                                    LazyColumn(
                                        verticalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        items(filteredTasks) { task ->
                                            Card(
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = CardDefaults.cardColors(containerColor = Obsidian),
                                                shape = RoundedCornerShape(10.dp),
                                                border = BorderStroke(
                                                    width = 0.5.dp,
                                                    color = when (task.priority) {
                                                        "High" -> DustyRose
                                                        "Medium" -> ShimmeringGold
                                                        else -> SoftViolet
                                                    }.copy(alpha = 0.4f)
                                                )
                                            ) {
                                                Column(modifier = Modifier.padding(12.dp)) {
                                                    // Priority Badge
                                                    Box(
                                                        modifier = Modifier
                                                            .background(
                                                                color = when (task.priority) {
                                                                    "High" -> DustyRose.copy(alpha = 0.15f)
                                                                    "Medium" -> ShimmeringGold.copy(alpha = 0.15f)
                                                                    else -> SoftViolet.copy(alpha = 0.15f)
                                                                },
                                                                shape = RoundedCornerShape(4.dp)
                                                            )
                                                            .border(
                                                                width = 0.5.dp,
                                                                color = when (task.priority) {
                                                                    "High" -> DustyRose
                                                                    "Medium" -> ShimmeringGold
                                                                    else -> SoftViolet
                                                                },
                                                                shape = RoundedCornerShape(4.dp)
                                                            )
                                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(
                                                            task.priority,
                                                            color = when (task.priority) {
                                                                "High" -> DustyRose
                                                                "Medium" -> ShimmeringGold
                                                                else -> SoftViolet
                                                            },
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                    
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    Text(task.title, color = LightText, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                    if (task.description.isNotBlank()) {
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Text(task.description, color = MutedText, fontSize = 11.sp, maxLines = 2)
                                                    }
                                                    
                                                    Spacer(modifier = Modifier.height(12.dp))
                                                    
                                                    // Action controls
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        IconButton(
                                                            onClick = {
                                                                val idx = kanbanTasks.indexOf(task)
                                                                if (idx != -1) {
                                                                    val prevStatus = when (task.status) {
                                                                        "In Progress" -> "To Do"
                                                                        "Done" -> "In Progress"
                                                                        else -> null
                                                                    }
                                                                    if (prevStatus != null) {
                                                                        kanbanTasks[idx] = task.copy(status = prevStatus)
                                                                    }
                                                                }
                                                            },
                                                            enabled = task.status != "To Do",
                                                            modifier = Modifier.size(28.dp)
                                                        ) {
                                                            Icon(
                                                                Icons.AutoMirrored.Filled.ArrowBack,
                                                                contentDescription = "Move Left",
                                                                tint = if (task.status != "To Do") ShimmeringGold else MutedText.copy(alpha = 0.3f),
                                                                modifier = Modifier.size(16.dp)
                                                            )
                                                        }
                                                        
                                                        IconButton(
                                                            onClick = { kanbanTasks.remove(task) },
                                                            modifier = Modifier.size(28.dp)
                                                        ) {
                                                            Icon(
                                                                Icons.Filled.Delete,
                                                                contentDescription = "Delete",
                                                                tint = DustyRose,
                                                                modifier = Modifier.size(16.dp)
                                                            )
                                                        }
                                                        
                                                        IconButton(
                                                            onClick = {
                                                                val idx = kanbanTasks.indexOf(task)
                                                                if (idx != -1) {
                                                                    val nextStatus = when (task.status) {
                                                                        "To Do" -> "In Progress"
                                                                        "In Progress" -> "Done"
                                                                        else -> null
                                                                    }
                                                                    if (nextStatus != null) {
                                                                        kanbanTasks[idx] = task.copy(status = nextStatus)
                                                                    }
                                                                }
                                                            },
                                                            enabled = task.status != "Done",
                                                            modifier = Modifier.size(28.dp)
                                                        ) {
                                                            Icon(
                                                                Icons.AutoMirrored.Filled.ArrowForward,
                                                                contentDescription = "Move Right",
                                                                tint = if (task.status != "Done") ShimmeringGold else MutedText.copy(alpha = 0.3f),
                                                                modifier = Modifier.size(16.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (activeDashboardTab == 2) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Supabase Series Management",
                        color = LightText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = WarmObsidian),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, ShimmeringGold.copy(alpha = 0.2f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Upload New Series Entry ☄️",
                                color = ShimmeringGold,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Fill in the details below to write a new romantic series recommendation directly to the live Supabase tables.",
                                color = LightText.copy(alpha = 0.8f),
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )

                            // Error Display
                            if (createSeriesError != null) {
                                Text(
                                    text = createSeriesError ?: "",
                                    color = Color(0xFFE84A5F),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }

                            // Title Field
                            OutlinedTextField(
                                value = seriesTitle,
                                onValueChange = { seriesTitle = it },
                                label = { Text("Series Title", color = ShimmeringGold, fontSize = 12.sp) },
                                placeholder = { Text("e.g. Blank The Series", color = MutedText, fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth().testTag("series_title_input"),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ShimmeringGold,
                                    unfocusedBorderColor = Obsidian,
                                    focusedTextColor = LightText,
                                    unfocusedTextColor = LightText,
                                    focusedContainerColor = Obsidian,
                                    unfocusedContainerColor = Obsidian
                                )
                            )

                            // Image URL Field
                            OutlinedTextField(
                                value = seriesImageUrl,
                                onValueChange = { seriesImageUrl = it },
                                label = { Text("Image URL", color = ShimmeringGold, fontSize = 12.sp) },
                                placeholder = { Text("https://example.com/image.jpg", color = MutedText, fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth().testTag("series_image_url_input"),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ShimmeringGold,
                                    unfocusedBorderColor = Obsidian,
                                    focusedTextColor = LightText,
                                    unfocusedTextColor = LightText,
                                    focusedContainerColor = Obsidian,
                                    unfocusedContainerColor = Obsidian
                                )
                            )

                            // Quick Preset Buttons for testing
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Presets:",
                                    color = MutedText,
                                    fontSize = 11.sp
                                )
                                listOf(
                                    "https://images.unsplash.com/photo-1518199266791-5375a83190b7?q=80&w=800",
                                    "https://images.unsplash.com/photo-1516589178581-6cd7833ae3b2?q=80&w=800",
                                    "https://images.unsplash.com/photo-1534447677768-be436bb09401?q=80&w=800"
                                ).forEachIndexed { index, url ->
                                    Button(
                                        onClick = { seriesImageUrl = url },
                                        colors = ButtonDefaults.buttonColors(containerColor = CosmicViolet.copy(alpha = 0.5f)),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(24.dp)
                                    ) {
                                        Text("Image ${index + 1}", color = ShimmeringGold, fontSize = 9.sp)
                                    }
                                }
                            }

                            // Description Field
                            OutlinedTextField(
                                value = seriesDescription,
                                onValueChange = { seriesDescription = it },
                                label = { Text("Description", color = ShimmeringGold, fontSize = 12.sp) },
                                placeholder = { Text("Write a captivating description about the series storyline...", color = MutedText, fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth().height(100.dp).testTag("series_description_input"),
                                maxLines = 4,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ShimmeringGold,
                                    unfocusedBorderColor = Obsidian,
                                    focusedTextColor = LightText,
                                    unfocusedTextColor = LightText,
                                    focusedContainerColor = Obsidian,
                                    unfocusedContainerColor = Obsidian
                                )
                            )

                            // Priority Selector Row
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Priority Level", color = ShimmeringGold, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf("High", "Medium", "Low").forEach { level ->
                                        val isSelected = seriesPriority == level
                                        Card(
                                            onClick = { seriesPriority = level },
                                            modifier = Modifier.weight(1f).height(36.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isSelected) CosmicViolet else Obsidian
                                            ),
                                            border = BorderStroke(
                                                width = 1.dp,
                                                color = if (isSelected) ShimmeringGold else Color.Transparent
                                            )
                                        ) {
                                            Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = when (level) {
                                                        "High" -> "High 🔥"
                                                        "Medium" -> "Medium ⚡"
                                                        else -> "Low 💫"
                                                    },
                                                    color = if (isSelected) ShimmeringGold else LightText,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Submit Button
                            Button(
                                onClick = {
                                    if (seriesTitle.isNotBlank() && seriesDescription.isNotBlank() && seriesImageUrl.isNotBlank()) {
                                        viewModel.uploadSupabaseSeries(
                                            title = seriesTitle,
                                            description = seriesDescription,
                                            imageUrl = seriesImageUrl,
                                            priority = seriesPriority
                                        ) { success ->
                                            if (success) {
                                                seriesTitle = ""
                                                seriesDescription = ""
                                                seriesImageUrl = ""
                                                seriesPriority = "Medium"
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("upload_series_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = ShimmeringGold),
                                shape = RoundedCornerShape(12.dp),
                                enabled = !isCreatingSeries && seriesTitle.isNotBlank() && seriesDescription.isNotBlank() && seriesImageUrl.isNotBlank()
                            ) {
                                if (isCreatingSeries) {
                                    CircularProgressIndicator(
                                        color = Obsidian,
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(
                                        text = "Upload to Supabase DB 🚀",
                                        color = Obsidian,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Header for live list
                item {
                    Text(
                        text = "Live Database Series Entries (${supabaseSeriesList.size})",
                        color = ShimmeringGold,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                if (supabaseSeriesList.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = WarmObsidian),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No custom series in Supabase table yet.", color = MutedText, fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    items(supabaseSeriesList) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = WarmObsidian),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, DustyRose.copy(alpha = 0.15f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = item.image_url,
                                    contentDescription = item.title,
                                    modifier = Modifier.size(50.dp).clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = item.title,
                                            color = ShimmeringGold,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = CosmicViolet),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = item.priority ?: "Low",
                                                color = LightText,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = item.description,
                                        color = LightText,
                                        fontSize = 11.sp,
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDashboardScreen(navController: NavHostController, viewModel: DashboardViewModel = viewModel()) {
    val dashboard by viewModel.userDashboard.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var isCheckedIn by remember { mutableStateOf(false) }
    var streakCount by remember { mutableStateOf(3) }
    var localCoinsOffset by remember { mutableStateOf(0) }
    var isRaffleEntering by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchUserDashboard()
    }

    val baseCoins = dashboard?.coinBalance?.toIntOrNull() ?: 1250
    val displayCoins = baseCoins + localCoinsOffset
    val activeRaffles = dashboard?.activeRaffles ?: 1

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Obsidian,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "User Dashboard", 
                        color = ShimmeringGold, 
                        fontWeight = FontWeight.Bold,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Serif
                    ) 
                },
                navigationIcon = {
                    IconButton(
                        modifier = Modifier.minimumInteractiveComponentSize(),
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ShimmeringGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Obsidian)
            )
        }
    ) { innerPadding ->
        if (dashboard == null) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ShimmeringGold)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Obsidian),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Welcome Banner
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = WarmObsidian),
                        border = BorderStroke(1.dp, ShimmeringGold.copy(alpha = 0.3f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            CosmicViolet.copy(alpha = 0.4f),
                                            WarmObsidian
                                        )
                                    )
                                )
                                .padding(20.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(CircleShape)
                                            .background(Brush.linearGradient(listOf(ShimmeringGold, DustyRose))),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            (dashboard?.name ?: "U").take(2).uppercase(),
                                            color = Obsidian,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(
                                            "Welcome, ${dashboard?.name ?: "GL Fanatic"}!",
                                            color = LightText,
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Filled.AutoAwesome,
                                                contentDescription = null,
                                                tint = ShimmeringGold,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                dashboard?.loyaltyBadge ?: "Celestial Pioneer",
                                                color = ShimmeringGold,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Grid stats
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Coins Card
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = WarmObsidian)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Filled.Star,
                                        contentDescription = "Coins",
                                        tint = ShimmeringGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Stars & Coins", color = MutedText, fontSize = 11.sp)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "$displayCoins",
                                    color = ShimmeringGold,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Active Raffles Card
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = WarmObsidian)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Filled.ConfirmationNumber,
                                        contentDescription = "Raffles",
                                        tint = DustyRose,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Raffles Entered", color = MutedText, fontSize = 11.sp)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "$activeRaffles",
                                    color = LightText,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Premium Status Card
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = WarmObsidian)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Filled.CardMembership,
                                        contentDescription = "Premium",
                                        tint = SoftViolet,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Membership", color = MutedText, fontSize = 11.sp)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    if (dashboard?.premiumStatus == true) "CELESTIAL VIP" else "Standard",
                                    color = if (dashboard?.premiumStatus == true) Lavender else LightText,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Watchlist Card
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = WarmObsidian)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Filled.Bookmark,
                                        contentDescription = "Watchlist",
                                        tint = SoftViolet,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Watchlist Size", color = MutedText, fontSize = 11.sp)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "${dashboard?.watchlistCount ?: 0} series",
                                    color = LightText,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Daily Check-In Roadmap Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = WarmObsidian),
                        border = BorderStroke(1.dp, WarmObsidian)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Filled.CalendarMonth,
                                        contentDescription = null,
                                        tint = ShimmeringGold
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Daily Cosmic Check-In",
                                        color = LightText,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                                Text(
                                    "Streak: $streakCount days",
                                    color = ShimmeringGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // 7 Days roadmap
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                for (day in 1..7) {
                                    val isChecked = day < 4 || (day == 4 && isCheckedIn)
                                    val isToday = day == 4
                                    val dayBgColor by animateColorAsState(
                                        targetValue = when {
                                            isChecked -> ShimmeringGold.copy(alpha = 0.2f)
                                            isToday -> SoftViolet.copy(alpha = 0.3f)
                                            else -> Obsidian
                                        },
                                        label = "dayBg"
                                    )
                                    val borderStroke = when {
                                        isChecked -> BorderStroke(1.dp, ShimmeringGold)
                                        isToday -> BorderStroke(1.dp, SoftViolet)
                                        else -> BorderStroke(1.dp, WarmObsidian)
                                    }

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(dayBgColor)
                                                .border(borderStroke)
                                                .clickable(enabled = isToday && !isCheckedIn) {
                                                    isCheckedIn = true
                                                    streakCount += 1
                                                    localCoinsOffset += 50
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar(
                                                            "Checked in successfully! +50 Coins claimed."
                                                        )
                                                    }
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isChecked) {
                                                Icon(
                                                    Icons.Filled.CheckCircle,
                                                    contentDescription = "Checked",
                                                    tint = ShimmeringGold,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            } else {
                                                Text(
                                                    "D$day",
                                                    color = if (isToday) SoftViolet else MutedText,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    isCheckedIn = true
                                    streakCount += 1
                                    localCoinsOffset += 50
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            "Checked in successfully! +50 Coins claimed."
                                        )
                                    }
                                },
                                enabled = !isCheckedIn,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ShimmeringGold,
                                    contentColor = Obsidian,
                                    disabledContainerColor = WarmObsidian,
                                    disabledContentColor = MutedText
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .minimumInteractiveComponentSize(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    if (isCheckedIn) "Checked In Today" else "Claim 50 Daily Coins",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Interactive Raffle Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = WarmObsidian),
                        border = BorderStroke(1.dp, CosmicViolet.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(CosmicViolet.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Filled.ConfirmationNumber,
                                        contentDescription = null,
                                        tint = Lavender,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        "Celestial Equinox Raffle",
                                        color = LightText,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        "Exclusive FreenBecky photo cards",
                                        color = MutedText,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                "Win autographed albums, personalized video shoutouts, and special user roles in our Cosmic community! Registered entries are verified instantly.",
                                color = LightText,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    isRaffleEntering = true
                                    viewModel.enterRaffle("celestial-raffle-2026") {
                                        isRaffleEntering = false
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                "Raffle ticket registered in DB successfully!"
                                            )
                                        }
                                    }
                                },
                                enabled = !isRaffleEntering,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CosmicViolet,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .minimumInteractiveComponentSize(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                if (isRaffleEntering) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                                } else {
                                    Text("Register Celestial Entry", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
