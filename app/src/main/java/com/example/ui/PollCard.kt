package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Analytics
import com.example.generateContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.ui.theme.*

data class PollOption(
    val id: String,
    val text: String,
    val votes: Int
)

data class PollComment(
    val id: String,
    val authorName: String,
    val text: String,
    val timestamp: Long
)

data class PollData(
    val id: String,
    val question: String,
    val options: List<PollOption>,
    val totalVotes: Int,
    val closesAt: Long,
    val comments: List<PollComment> = emptyList()
)

@Composable
fun PollCard(
    pollId: String,
    currentUserId: String?,
    modifier: Modifier = Modifier
) {
    // 1. Loading State
    var isLoading by remember { mutableStateOf(true) }
    
    // 2. Poll Data
    var pollData by remember { mutableStateOf<PollData?>(null) }
    
    // 3. User State
    var hasVoted by remember { mutableStateOf(false) }
    var selectedOptionId by remember { mutableStateOf<String?>(null) }
    var votedOptionId by remember { mutableStateOf<String?>(null) }
    var showUndo by remember { mutableStateOf(false) }
    var undoTimeRemaining by remember { mutableIntStateOf(0) }
    var showComments by remember { mutableStateOf(false) }
    var newCommentText by remember { mutableStateOf("") }
    
    // Timer state
    var timeRemaining by remember { mutableStateOf("") }
    var isClosed by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(1f) }
    var communityPulse by remember { mutableStateOf<String?>("Analyzing...") }
    var showDownloadDialog by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()

    // Simulate Supabase fetch
    LaunchedEffect(pollId) {
        isLoading = true
        delay(1500) // Fake network delay
        
        val isArchive = pollId.startsWith("archive")
        val closeTime = if (isArchive) System.currentTimeMillis() - 86400000 else System.currentTimeMillis() + (2 * 60 * 60 * 1000) + (15 * 60 * 1000)
        
        pollData = PollData(
            id = pollId,
            question = if (isArchive) "Past Poll: Favorite 2024 Series?" else "Which series deserves the next adaptation?",
            options = listOf(
                PollOption("1", "Pluto", 120),
                PollOption("2", "Mate", 85),
                PollOption("3", "Reverse 4 You", 200),
                PollOption("4", "The Secret of Us", 150)
            ),
            totalVotes = 555,
            closesAt = closeTime, // Archive closes in past
            comments = listOf(
                PollComment("c1", "FanA", "Reverse 4 You is a masterpiece!", System.currentTimeMillis() - 3600000),
                PollComment("c2", "FanB", "I need Pluto rn.", System.currentTimeMillis() - 1800000)
            )
        )
        isLoading = false
    }

    // Countdown Timer logic
    LaunchedEffect(pollData?.closesAt) {
        pollData?.closesAt?.let { closesAt ->
            val totalDuration = 24 * 60 * 60 * 1000L
            while (true) {
                val now = System.currentTimeMillis()
                val diff = closesAt - now
                progress = (diff.toFloat() / totalDuration).coerceIn(0f, 1f)
                if (diff <= 0) {
                    timeRemaining = "Closed"
                    isClosed = true
                    hasVoted = true // Force show results
                    break
                } else {
                    val hours = (diff / (1000 * 60 * 60)) % 24
                    val minutes = (diff / (1000 * 60)) % 60
                    val seconds = (diff / 1000) % 60
                    timeRemaining = String.format("%02dh %02dm %02ds", hours, minutes, seconds)
                }
                delay(1000)
            }
        }
    }
    
    LaunchedEffect(isClosed, showComments) {
        if ((isClosed || showComments) && communityPulse == "Analyzing...") {
            val commentsText = pollData?.comments?.joinToString(" | ") { it.text } ?: ""
            if (commentsText.isNotBlank()) {
                val sentiment = generateContent("Analyze the sentiment of these poll comments in one short sentence: $commentsText")
                communityPulse = sentiment
            } else {
                communityPulse = "No comments yet."
            }
        }
    }

    if (showDownloadDialog) {
        AlertDialog(
            onDismissRequest = { showDownloadDialog = false },
            title = { Text("Download Results", color = ShimmeringGold) },
            text = { Text("A visual summary card of this poll has been generated and saved as a client-side object for sharing.", color = LightText) },
            confirmButton = { TextButton(onClick = { showDownloadDialog = false }) { Text("OK", color = ShimmeringGold) } },
            containerColor = WarmObsidian
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = WarmObsidian),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, ShimmeringGold.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            if (isLoading) {
                PollCardSkeleton()
            } else {
                pollData?.let { poll ->
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(4.dp).padding(bottom = 8.dp),
                        color = ShimmeringGold,
                        trackColor = Color.Gray.copy(alpha = 0.2f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = poll.question,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f).padding(end = 16.dp)
                        )
                        
                        // Timer Pill
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = if (isClosed) CosmicViolet else ShimmeringGold.copy(alpha = 0.2f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = timeRemaining,
                                    color = if (isClosed) LightText else ShimmeringGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { /* mock share */ },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Filled.Share, contentDescription = "Share Poll", tint = ShimmeringGold, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    poll.options.forEach { option ->
                        val isSelected = selectedOptionId == option.id
                        val isVotedFor = votedOptionId == option.id
                        val showResults = hasVoted || isClosed

                        val percentage = if (poll.totalVotes > 0) {
                            option.votes.toFloat() / poll.totalVotes.toFloat()
                        } else 0f

                        PollOptionRow(
                            option = option,
                            isSelected = isSelected,
                            showResults = showResults,
                            isVotedFor = isVotedFor,
                            percentage = percentage,
                            onClick = {
                                if (!hasVoted && !isClosed) {
                                    selectedOptionId = option.id
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    AnimatedVisibility(
                        visible = !hasVoted && selectedOptionId != null && !isClosed,
                        enter = fadeIn(tween(300)),
                        exit = fadeOut(tween(300))
                    ) {
                        Button(
                            onClick = {
                                scope.launch {
                                    // Simulate Supabase vote
                                    if (currentUserId != null && selectedOptionId != null) {
                                        // Update state immediately for UX
                                        val newOptions = poll.options.map { 
                                            if (it.id == selectedOptionId) it.copy(votes = it.votes + 1) else it 
                                        }
                                        pollData = poll.copy(
                                            options = newOptions,
                                            totalVotes = poll.totalVotes + 1
                                        )
                                        hasVoted = true
                                        votedOptionId = selectedOptionId
                                        
                                        showUndo = true
                                        undoTimeRemaining = 10
                                        scope.launch {
                                            while (undoTimeRemaining > 0 && showUndo) {
                                                delay(1000)
                                                undoTimeRemaining -= 1
                                            }
                                            showUndo = false
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ShimmeringGold),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Text("Vote", color = WarmObsidian, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (hasVoted || isClosed) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${poll.totalVotes} total votes",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                                if (showUndo && !isClosed) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    TextButton(
                                        onClick = {
                                            val oldOptions = poll.options.map { 
                                                if (it.id == votedOptionId) it.copy(votes = it.votes - 1) else it 
                                            }
                                            pollData = poll.copy(
                                                options = oldOptions,
                                                totalVotes = poll.totalVotes - 1
                                            )
                                            hasVoted = false
                                            votedOptionId = null
                                            selectedOptionId = null
                                            showUndo = false
                                        },
                                        modifier = Modifier.height(24.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text("Undo ($undoTimeRemaining)", color = DustyRose, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                ReactionButton(emoji = "👍", initialCount = 12)
                                ReactionButton(emoji = "❤️", initialCount = 5)
                                ReactionButton(emoji = "😲", initialCount = 2)
                            }
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { showDownloadDialog = true }) {
                                Icon(Icons.Filled.Download, contentDescription = "Download Results", tint = ShimmeringGold, modifier = Modifier.size(16.dp).padding(end = 4.dp))
                                Text("Download", color = ShimmeringGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            TextButton(onClick = { showComments = !showComments }) {
                                Text(
                                    text = if (showComments) "Hide Comments" else "View Comments (${poll.comments.size})",
                                    color = ShimmeringGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    
                    AnimatedVisibility(visible = showComments) {
                        Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                            HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f), modifier = Modifier.padding(bottom = 12.dp))
                            // Community Pulse
                            Card(colors = CardDefaults.cardColors(containerColor = ShimmeringGold.copy(alpha = 0.1f)), modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(12.dp)) {
                                    Icon(Icons.Filled.Analytics, contentDescription = "Pulse", tint = ShimmeringGold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text("Community Pulse (Gemini AI)", color = ShimmeringGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text(communityPulse ?: "Analyzing...", color = LightText, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
                                    }
                                }
                            }
                            
                            poll.comments.forEach { comment ->
                                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = comment.authorName, color = LightText, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = "•", color = Color.Gray, fontSize = 12.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = "Just now", color = Color.Gray, fontSize = 11.sp) // Mock relative time
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = comment.text, color = LightText.copy(alpha = 0.8f), fontSize = 13.sp)
                                }
                            }
                            
                            // Comment Input
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = newCommentText,
                                    onValueChange = { newCommentText = it },
                                    placeholder = { Text("Add a comment...", color = Color.Gray, fontSize = 13.sp) },
                                    modifier = Modifier.weight(1f).height(50.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = ShimmeringGold,
                                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                                        focusedTextColor = LightText,
                                        unfocusedTextColor = LightText
                                    ),
                                    shape = RoundedCornerShape(24.dp),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                VoiceInputButton(onResult = { result ->
                                    newCommentText = result
                                })
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = {
                                        if (newCommentText.isNotBlank()) {
                                            val newComment = PollComment(
                                                id = "c_new_${System.currentTimeMillis()}",
                                                authorName = "You",
                                                text = newCommentText,
                                                timestamp = System.currentTimeMillis()
                                            )
                                            pollData = poll.copy(comments = poll.comments + newComment)
                                            newCommentText = ""
                                        }
                                    },
                                    modifier = Modifier.background(ShimmeringGold, CircleShape).size(40.dp)
                                ) {
                                    Icon(Icons.Filled.CheckCircle, contentDescription = "Post Comment", tint = WarmObsidian, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PollOptionRow(
    option: PollOption,
    isSelected: Boolean,
    showResults: Boolean,
    isVotedFor: Boolean,
    percentage: Float,
    onClick: () -> Unit
) {
    val animatedProgress by animateFloatAsState(
        targetValue = if (showResults) percentage else 0f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "progress"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected && !showResults) ShimmeringGold.copy(alpha = 0.2f) else Color.Transparent)
            .clickable(enabled = !showResults, onClick = onClick)
            .then(
                if (!showResults) {
                    Modifier.padding(12.dp)
                } else Modifier
            )
    ) {
        if (showResults) {
            // Result Bar Background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(Color.DarkGray.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            )
            // Animated Result Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress.coerceAtLeast(0.02f)) // Ensure tiny bar for 0 votes so it looks rounded
                    .height(48.dp)
                    .background(if (isVotedFor) ShimmeringGold.copy(alpha = 0.8f) else Color.Gray.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (showResults) 12.dp else 0.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!showResults) {
                    // Radio button circle
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) ShimmeringGold else Color.Transparent)
                            .then(
                                if (!isSelected) Modifier.padding(2.dp).background(Color.White.copy(alpha = 0.2f), CircleShape)
                                else Modifier
                            )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                
                Text(
                    text = option.text,
                    color = Color.White,
                    fontWeight = if (isVotedFor) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 14.sp
                )
                
                if (isVotedFor && showResults) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Voted",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            if (showResults) {
                Text(
                    text = "${(percentage * 100).toInt()}%",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun PollCardSkeleton() {
    Column {
        Box(modifier = Modifier.fillMaxWidth(0.7f).height(24.dp).background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(4.dp)))
        Spacer(modifier = Modifier.height(16.dp))
        repeat(3) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
