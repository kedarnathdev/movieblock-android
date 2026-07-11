package com.kedarnathdev.movieblock

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.kedarnathdev.movieblock.ui.components.BottomNavBar
import com.kedarnathdev.movieblock.ui.screens.CreateTaskScreen
import com.kedarnathdev.movieblock.ui.screens.TasksScreen

/**
 * Main navigation with bottom tab bar.
 * Uses Crossfade for smooth transitions between tabs.
 */
@Composable
fun MovieBlockNavigation() {
    var selectedTab by remember { mutableIntStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Content area
        Crossfade(
            targetState = selectedTab,
            label = "tab_switch",
            modifier = Modifier.fillMaxSize()
        ) { tab ->
            when (tab) {
                0 -> CreateTaskScreen()
                1 -> TasksScreen()
            }
        }

        // Bottom navigation bar
        BottomNavBar(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            modifier = Modifier.align(androidx.compose.ui.Alignment.BottomCenter)
        )
    }
}
