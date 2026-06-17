package com.kedarnathdev.movieblock.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

    Scaffold(
        topBar = {
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
                    IconButton(onClick = {
                        viewModel.clearSelectedTask()
                        onNavigateBack()
                    }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Ink
                        )
                    }
                }
            )
        },
        containerColor = Canvas
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
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
                // Task List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(tasks.size) { index ->
                        val task = tasks[index]
                        TaskCardExpanded(
                            task = task,
                            isExpanded = selectedTask?.id == task.id,
                            onToggle = {
                                if (selectedTask?.id == task.id) {
                                    viewModel.clearSelectedTask()
                                } else {
                                    viewModel.selectTask(task)
                                }
                            },
                            editSeatInput = if (selectedTask?.id == task.id) editSeatInput else "",
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
fun TaskCardExpanded(
    task: Task,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    editSeatInput: String,
    onEditSeatChange: (String) -> Unit,
    onUpdateSeats: () -> Unit,
    onStop: () -> Unit,
    onDelete: () -> Unit
) {
    val isActive = task.status in listOf("running", "waiting", "checking", "booked", "booking", "cooling_down", "rechecking")
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isExpanded) Primary.copy(alpha = 0.1f) else SurfaceCard
        ),
        onClick = onToggle
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row
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
                    if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
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

            // Expanded Details
            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Hairline, thickness = 1.dp)
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
                        modifier = Modifier
                            .heightIn(max = 200.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        task.notifications.takeLast(10).forEach { notif ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        when (notif.type) {
                                            "success" -> Success.copy(alpha = 0.08f)
                                            "error" -> Error.copy(alpha = 0.08f)
                                            "warning" -> AccentAmber.copy(alpha = 0.08f)
                                            else -> Muted.copy(alpha = 0.08f)
                                        }
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = when (notif.type) {
                                        "success" -> "✓"
                                        "error" -> "✗"
                                        "warning" -> "⚠"
                                        else -> "•"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = when (notif.type) {
                                        "success" -> Success
                                        "error" -> Error
                                        "warning" -> AccentAmber
                                        else -> Muted
                                    }
                                )
                                Text(
                                    text = notif.message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnDark
                                )
                            }
                        }
                    }
                }

                // Actions
                Spacer(modifier = Modifier.height(16.dp))
                
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

                Button(
                    onClick = onUpdateSeats,
                    enabled = editSeatInput.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Update Seats")
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
    }
}
