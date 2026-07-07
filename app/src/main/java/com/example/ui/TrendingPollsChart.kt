package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ShimmeringGold
import com.example.ui.theme.WarmObsidian

@Composable
fun TrendingPollsChart() {
    val data = listOf(20f, 45f, 30f, 80f, 65f, 90f, 100f) // Mock growth data
    val labels = listOf("12a", "4a", "8a", "12p", "4p", "8p", "Now")
    
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text("Trending Polls Activity (24h)", color = ShimmeringGold, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(bottom = 16.dp))
        
        Box(modifier = Modifier.fillMaxWidth().height(120.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val maxData = data.maxOrNull() ?: 100f
                val barWidth = (size.width / (data.size * 2))
                val spacing = barWidth
                
                data.forEachIndexed { index, value ->
                    val barHeight = (value / maxData) * size.height
                    val x = (index * (barWidth + spacing)) + (spacing / 2)
                    val y = size.height - barHeight
                    
                    drawRoundRect(
                        color = ShimmeringGold.copy(alpha = if (index == data.size - 1) 1f else 0.5f),
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(4.dp.toPx())
                    )
                }
            }
        }
        
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            labels.forEach { label ->
                Text(text = label, color = Color.Gray, fontSize = 10.sp)
            }
        }
    }
}
