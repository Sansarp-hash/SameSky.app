package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.testTag

@Composable
fun ToggleIconWidget(
    modifier: Modifier = Modifier,
    initialOpen: Boolean = false,
    content: @Composable () -> Unit = {
        Text(
            text = "This content opens and exits on tap!",
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontSize = 14.sp
        )
    }
) {
    var isOpen by remember { mutableStateOf(initialOpen) }

    // Smooth color animation for the icon (Red when open, Blue when closed)
    val iconColor by animateColorAsState(
        targetValue = if (isOpen) Color(0xFFE57373) else Color(0xFF64B5F6),
        animationSpec = spring(),
        label = "IconColor"
    )

    // Smooth rotation animation for the icon button to make transition playful
    val rotationAngle by animateFloatAsState(
        targetValue = if (isOpen) 90f else 0f,
        animationSpec = spring(),
        label = "IconRotation"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // The Toggle Icon Button
        IconButton(
            onClick = { isOpen = !isOpen },
            modifier = Modifier
                .size(48.dp)
                .rotate(rotationAngle)
                .testTag("toggle_icon_button")
        ) {
            Icon(
                imageVector = if (isOpen) Icons.Default.Close else Icons.Default.Menu,
                contentDescription = if (isOpen) "Close menu" else "Open menu",
                tint = iconColor,
                modifier = Modifier.size(30.dp)
            )
        }

        // Smoothly animated expanding/collapsing content
        AnimatedVisibility(
            visible = isOpen,
            enter = fadeIn(animationSpec = spring()) + expandVertically(animationSpec = spring()),
            exit = fadeOut(animationSpec = spring()) + shrinkVertically(animationSpec = spring())
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        }
    }
}
