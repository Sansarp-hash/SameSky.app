package com.example.ui

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.ui.theme.*

/**
 * A beautiful, glowing linear gradient brush that pulses softly between
 * celestial romantic colors: WarmObsidian, DustyRose, SoftViolet, and BlushPink.
 */
@Composable
fun romanticShimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "romantic_shimmer")
    
    val color1 by transition.animateColor(
        initialValue = WarmObsidian,
        targetValue = SoftViolet.copy(alpha = 0.25f),
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "color1"
    )
    
    val color2 by transition.animateColor(
        initialValue = DustyRose.copy(alpha = 0.15f),
        targetValue = WarmObsidian,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "color2"
    )

    val color3 by transition.animateColor(
        initialValue = SoftViolet.copy(alpha = 0.15f),
        targetValue = BlushPink.copy(alpha = 0.2f),
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "color3"
    )

    return Brush.linearGradient(
        colors = listOf(color1, color2, color3, color1)
    )
}

/**
 * Basic block loader with our beautiful pulsing romantic gradient
 */
@Composable
fun RomanticSkeletonBlock(
    modifier: Modifier = Modifier,
    height: Dp = 20.dp,
    cornerRadius: Dp = 6.dp
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(romanticShimmerBrush())
    )
}

/**
 * Skeleton Loader representing a single community post item
 */
@Composable
fun PostFeedSkeletonItem() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Obsidian)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular Avatar skeleton
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(romanticShimmerBrush())
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                // Name block
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(romanticShimmerBrush())
                )
                Spacer(modifier = Modifier.height(6.dp))
                // Sign / MBTI block
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(10.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(romanticShimmerBrush())
                )
            }
            // Action badge skeleton
            Box(
                modifier = Modifier
                    .size(width = 50.dp, height = 18.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(romanticShimmerBrush())
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Post body text lines
        RomanticSkeletonBlock(height = 14.dp, modifier = Modifier.fillMaxWidth(0.95f))
        Spacer(modifier = Modifier.height(8.dp))
        RomanticSkeletonBlock(height = 14.dp, modifier = Modifier.fillMaxWidth(0.85f))
        Spacer(modifier = Modifier.height(8.dp))
        RomanticSkeletonBlock(height = 14.dp, modifier = Modifier.fillMaxWidth(0.4f))
        Spacer(modifier = Modifier.height(16.dp))
        // Bottom interaction bar (Likes/Comments placeholders)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(width = 60.dp, height = 24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(romanticShimmerBrush())
            )
            Box(
                modifier = Modifier
                    .size(width = 60.dp, height = 24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(romanticShimmerBrush())
            )
        }
    }
}

/**
 * Skeleton loader for horizontal recommendation carousels
 */
@Composable
fun RecommendationCarouselSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Section title skeleton
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .width(160.dp)
                .height(18.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(romanticShimmerBrush())
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(2) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(WarmObsidian)
                        .padding(12.dp)
                ) {
                    // Image thumbnail placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(romanticShimmerBrush())
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    // Title skeleton line 1
                    RomanticSkeletonBlock(height = 12.dp, modifier = Modifier.fillMaxWidth(0.9f))
                    Spacer(modifier = Modifier.height(6.dp))
                    // Title skeleton line 2
                    RomanticSkeletonBlock(height = 12.dp, modifier = Modifier.fillMaxWidth(0.6f))
                }
            }
        }
    }
}

/**
 * A reusable, beautifully styled circular progress loader that is used to
 * indicate active Supabase data fetching.
 */
@Composable
fun SupabaseLoadingSpinner(
    modifier: Modifier = Modifier,
    statusText: String = "Syncing celestial series from Supabase..."
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = ShimmeringGold,
                trackColor = DustyRose.copy(alpha = 0.2f),
                strokeWidth = 4.dp,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = statusText,
                color = BlushPink,
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }
}

