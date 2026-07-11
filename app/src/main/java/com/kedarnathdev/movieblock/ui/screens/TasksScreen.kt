package com.kedarnathdev.movieblock.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kedarnathdev.movieblock.ui.components.TaskCard
import com.kedarnathdev.movieblock.ui.theme.*
import com.kedarnathdev.movieblock.ui.viewmodel.TaskViewModel

/**
 * Tasks screen with redesigned layout.
 * Shows "ACTIVE TASKS" header and list of task cards.
 */
@Composable
fun TasksScreen(
    viewModel: TaskViewModel = viewModel()
) {
    val tasks by viewModel.tasks.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Canvas)
    ) {
        // Header
        Text(
            text = "ACTIVE TASKS",
            style = MaterialTheme.typography.displaySmall,
            color = Ink,
            modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 16.dp)
        )

        // Error banner
        error?.let { errMsg ->
            Text(
                text = errMsg,
                color = Error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }

        if (tasks.isEmpty()) {
            // Empty state
            EmptyTasksState()
        } else {
            // Task List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(tasks, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        onStop = { viewModel.stopTask(task.id) },
                        onDelete = { viewModel.deleteTask(task.id) }
                    )
                }

                // Bottom padding for navigation bar
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}

@Composable
private fun EmptyTasksState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Muted
            )

            Text(
                text = "No Active Tasks",
                style = MaterialTheme.typography.headlineMedium,
                color = Muted
            )

            Text(
                text = "Tasks you create will appear here",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedSoft,
                textAlign = TextAlign.Center
            )

            // Bottom padding for navigation
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
