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
import com.kedarnathdev.movieblock.ui.theme.*
import com.kedarnathdev.movieblock.ui.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    viewModel: TaskViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val tasks by viewModel.tasks.collectAsState()
    val selectedTaskId = remember { mutableStateOf<String?>(null) }
    val error by viewModel.error.collectAsState()
    
    var editSeatInput by remember { mutableStateOf("") }

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
                    IconButton(onClick = onNavigateBack) {
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
            if (error != null) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("Dismiss", color = OnPrimary)
                        }
                    }
                ) {
                    Text(error ?: "Error", color = OnPrimary)
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    tasks.forEach { task ->
                        TaskCardSimple(
                            task = task,
                            isExpanded = selectedTaskId.value == task.id,
                            onToggle = {
                                selectedTaskId.value = if (selectedTaskId.value == task.id) null else task.id
                            },
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
fun TaskCardSimple(
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
            // Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status
                Surface(
                    shape = RoundedCornerShape(50),
                    color = when (task.status) {
                        "running", "booked" -> Success.copy(alpha = 0.15f)
                        "waiting", "cooling_down" -> AccentAmber.copy(alpha = 0.15f)
                        "checking", "booking", "rechecking" -> AccentTeal.copy(alpha = 0.15f)
                        "error" -> Error.copy(alpha = 0.15f)
                        else -> Muted.copy(alpha = 0.15f)
                    }
                ) {
                    Text(
                        text = task.status.replace("_", " ").uppercase(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = when (task.status) {
                            "running", "booked" -> Success
                            "waiting", "cooling_down" -> AccentAmber
                            "checking", "booking", "rechecking" -> AccentTeal
                            "error" -> Error
                            else -> Muted
                        },
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Icon(
                    if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Muted
                )
            }

            // Movie Title
            task.movieDetails?.title?.let { title ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Ink,
                    maxLines = 1
                )
            }
            
            // Showtime
            task.movieDetails?.showtime?.let { showtime ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = showtime,
                    style = MaterialTheme.typography.bodySmall,
                    color = AccentAmber,
                    fontWeight = FontWeight.Medium
                )
            }

            // Seats
            if (task.selectedSeats.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    task.selectedSeats.forEach { seat ->
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Primary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = seat,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = Primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
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

                // Task Info
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                val outputFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                
                fun parseTimestamp(timestamp: String?): String {
                    return try {
                        timestamp?.let { inputFormat.parse(it)?.let { outputFormat.format(it) } } ?: "N/A"
                    } catch (e: Exception) {
                        "N/A"
                    }
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column {
                        Text(
                            text = "STARTED",
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedSoft
                        )
                        Text(
                            text = parseTimestamp(task.startedAt),
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnDark
                        )
                    }
                    Column {
                        Text(
                            text = "LAST CHECKED",
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedSoft
                        )
                        Text(
                            text = parseTimestamp(task.lastChecked),
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnDark
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column {
                        Text(
                            text = "LOOP",
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedSoft
                        )
                        Text(
                            text = "#${task.currentLoop}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnDark
                        )
                    }
                    Column {
                        Text(
                            text = "ATTEMPTS",
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedSoft
                        )
                        Text(
                            text = "${task.attempts}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnDark
                        )
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
