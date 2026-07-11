package com.kedarnathdev.movieblock.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kedarnathdev.movieblock.ui.theme.*

/**
 * Pill-shaped bottom navigation bar with segmented control.
 * Matches the design with "Home" and "Tasks" tabs.
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
        // Pill-shaped container
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(SurfaceInput)
                .padding(4.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            // Home Tab
            NavTab(
                label = "Home",
                isSelected = selectedTab == 0,
                onClick = { onTabSelected(0) }
            )

            // Tasks Tab
            NavTab(
                label = "Tasks",
                isSelected = selectedTab == 1,
                onClick = { onTabSelected(1) }
            )
        }
    }
}

@Composable
private fun NavTab(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (isSelected) Primary else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 32.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (isSelected) OnPrimary else Muted,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}
