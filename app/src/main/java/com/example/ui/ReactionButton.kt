package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ShimmeringGold

@Composable
fun ReactionButton(emoji: String, initialCount: Int) {
    var count by remember { mutableIntStateOf(initialCount) }
    var reacted by remember { mutableStateOf(false) }
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (reacted) ShimmeringGold.copy(alpha = 0.2f) else Color.Transparent)
            .clickable { 
                reacted = !reacted
                count += if (reacted) 1 else -1 
            }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = emoji, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = count.toString(), color = if (reacted) ShimmeringGold else Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
