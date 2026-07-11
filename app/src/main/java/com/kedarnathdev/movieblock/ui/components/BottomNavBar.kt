package com.kedarnathdev.movieblock.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kedarnathdev.movieblock.ui.theme.*

/**
 * Pill-shaped bottom navigation bar with smooth "liquid glass" sliding animation.
 */
@Composable
fun BottomNavBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(SurfaceInput)
                .padding(4.dp)
                .height(48.dp)
                .fillMaxWidth(0.8f) // constraint the overall width to make it look like a pill
        ) {
            val tabWidth = maxWidth / 2

            // Animated background pill
            val offset by animateDpAsState(
                targetValue = if (selectedTab == 0) 0.dp else tabWidth,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "pill_offset"
            )

            // The sliding coral pill
            Box(
                modifier = Modifier
                    .offset(x = offset)
                    .width(tabWidth)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(50))
                    .background(Primary)
            )

            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                // Home Tab
                NavTab(
                    label = "Home",
                    isSelected = selectedTab == 0,
                    onClick = { onTabSelected(0) },
                    modifier = Modifier.weight(1f)
                )

                // Tasks Tab
                NavTab(
                    label = "Tasks",
                    isSelected = selectedTab == 1,
                    onClick = { onTabSelected(1) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun NavTab(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Smoothly animate the text color
    val textColor by animateColorAsState(
        targetValue = if (isSelected) OnPrimary else Muted,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "text_color"
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(50))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = textColor,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}
