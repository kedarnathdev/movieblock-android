package com.kedarnathdev.movieblock.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kedarnathdev.movieblock.data.model.Task
import com.kedarnathdev.movieblock.ui.components.*
import com.kedarnathdev.movieblock.ui.theme.*
import com.kedarnathdev.movieblock.ui.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    viewModel: TaskViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val tasks by viewModel.tasks.collectAsState()
    val selectedTask by viewModel.selectedTask.collectAsState()
    val error by viewModel.error.collectAsState()
    
    var editSeatInput by remember { mutableStateOf("") }
    
    LaunchedEffect(selectedTask) {
        selectedTask?.let { task ->
            editSeatInput = task.selectedSeats.joinToString(", ")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Canvas)
    ) {
        // Top Bar
        TopAppBar(
            title = { 
                Text(
                    "Active Tasks (${tasks.size})",
                    color = Ink,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = SurfaceCard
            ),
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Ink
                    )
                }
            }
        )

        // Error banner
        error?.let { errMsg ->
            Snackbar(
                modifier = Modifier.padding(16.dp),
                action = {
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text("Dismiss", color = OnPrimary)
                    }
                }
            ) {
                Text(errMsg, color = OnPrimary)
            }
        }

        if (tasks.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = Muted
                    )
                    Text(
                        "No Active Tasks",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Muted
                    )
                    Text(
                        "Tasks you create will appear here",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedSoft,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Left: Task List
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    tasks.forEach { task ->
                        TaskListItem(
                            task = task,
                            isSelected = selectedTask?.id == task.id,
                            onClick = { viewModel.selectTask(task) }
                        )
                    }
                }

                // Right: Selected Task Details
                selectedTask?.let { task ->
                    Column(
                        modifier = Modifier
                            .weight(0.4f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                    ) {
                        TaskDetailPanel(
                            task = task,
                            editSeatInput = editSeatInput,
                            onEditSeatChange = { editSeatInput = it },
                            onUpdateSeats = {
                                val seats = editSeatInput.split(",")
                                    .map { s -> s.trim().uppercase() }
                                    .filter { it.isNotEmpty() }
                                if (seats.isNotEmpty()) {
                                    viewModel.updateSeats(task.id, seats)
                                }
                            },
                            onStop = { viewModel.stopTask(task.id) },
                            onDelete = { viewModel.deleteTask(task.id) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListItem(
    task: Task,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isActive = task.status in listOf("running", "waiting", "checking", "booked", "booking", "cooling_down", "rechecking")
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Primary.copy(alpha = 0.1f) else SurfaceCard
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    StatusBadge(status = task.status)
                    Spacer(modifier = Modifier.height(8.dp))
                    task.movieDetails?.title?.let { title ->
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            color = Ink,
                            maxLines = 1
                        )
                    }
                    task.movieDetails?.showtime?.let { showtime ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = showtime,
                            style = MaterialTheme.typography.bodySmall,
                            color = AccentAmber,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                Icon(
                    if (isSelected) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Muted
                )
            }

            // Seats
            if (task.selectedSeats.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                SeatTags(seats = task.selectedSeats)
            }

            // Status message
            task.message?.let { msg ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = msg,
                    style = MaterialTheme.typography.bodySmall,
                    color = when (task.status) {
                        "cooling_down" -> AccentAmber
                        "rechecking" -> AccentTeal
                        "booked" -> Success
                        else -> Muted
                    }
                )
            }
        }
    }
}

@Composable
fun TaskDetailPanel(
    task: Task,
    editSeatInput: String,
    onEditSeatChange: (String) -> Unit,
    onUpdateSeats: () -> Unit,
    onStop: () -> Unit,
    onDelete: () -> Unit
) {
    val isActive = task.status in listOf("running", "waiting", "checking", "booked", "booking", "cooling_down", "rechecking")
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCard)
            .padding(24.dp)
    ) {
        // Title
        Text(
            text = "Task Details",
            style = MaterialTheme.typography.titleLarge,
            color = Ink
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Movie Details
        task.movieDetails?.let { movie ->
            MovieDetailsRow(movie)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Task Info
        TaskInfoGrid(
            task = task,
            showElapsed = isActive
        )

        // Seats
        if (task.selectedSeats.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Selected Seats",
                style = MaterialTheme.typography.labelSmall,
                color = Muted
            )
            Spacer(modifier = Modifier.height(8.dp))
            SeatTags(seats = task.selectedSeats)
        }

        // Notifications
        if (task.notifications.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Recent Notifications",
                style = MaterialTheme.typography.labelSmall,
                color = Muted
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier.height(200.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                task.notifications.takeLast(10).forEach { notif ->
                    NotificationItem(notif.message, notif.type)
                }
            }
        }

        // Actions
        Spacer(modifier = Modifier.height(24.dp))
        
        // Edit seats
        OutlinedTextField(
            value = editSeatInput,
            onValueChange = onEditSeatChange,
            label = { Text("Edit Seat IDs") },
            placeholder = { Text("B5, B6, C3...") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = Hairline
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onUpdateSeats,
                enabled = editSeatInput.isNotBlank(),
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Update Seats")
            }
        }

        if (isActive) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onStop,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Error),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Stop, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Stop Automation")
            }
        } else {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = onDelete,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Error),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Delete Task")
            }
        }

        // Error
        task.error?.let { err ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Error: $err",
                style = MaterialTheme.typography.bodySmall,
                color = Error
            )
        }
    }
}
