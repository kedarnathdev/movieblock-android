package com.kedarnathdev.movieblock.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kedarnathdev.movieblock.data.model.Task
import com.kedarnathdev.movieblock.ui.components.*
import com.kedarnathdev.movieblock.ui.theme.*
import com.kedarnathdev.movieblock.ui.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: TaskViewModel = viewModel()
) {
    val tasks by viewModel.tasks.collectAsState()
    val selectedTask by viewModel.selectedTask.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    var url by remember { mutableStateOf("") }
    var seatInput by remember { mutableStateOf("") }
    var checkInterval by remember { mutableStateOf(10) }
    var cooldownInterval by remember { mutableStateOf(600) }
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
            .padding(24.dp)
    ) {
        // Header
        HeaderSection()

        // Error banner
        error?.let { errMsg ->
            Spacer(modifier = Modifier.height(16.dp))
            ErrorBanner(message = errMsg, onDismiss = { viewModel.clearError() })
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Main content grid
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Left: Control Panel
            Column(
                modifier = Modifier
                    .width(400.dp)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
            ) {
                ControlPanel(
                    url = url,
                    onUrlChange = { url = it },
                    seatInput = seatInput,
                    onSeatInputChange = { seatInput = it },
                    checkInterval = checkInterval,
                    onCheckIntervalChange = { checkInterval = it },
                    cooldownInterval = cooldownInterval,
                    onCooldownChange = { cooldownInterval = it },
                    isLoading = isLoading,
                    onStart = {
                        val seats = seatInput.split(",")
                            .map { it.trim().uppercase() }
                            .filter { it.isNotEmpty() }
                        if (url.isNotEmpty() && seats.isNotEmpty()) {
                            viewModel.createTask(
                                url = url,
                                seatIds = seats,
                                checkIntervalMs = checkInterval * 1000L,
                                cooldownMs = cooldownInterval * 1000L
                            )
                            url = ""
                            seatInput = ""
                        }
                    }
                )

                // Selected task details
                selectedTask?.let { task ->
                    Spacer(modifier = Modifier.height(24.dp))
                    SelectedTaskPanel(
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
                        onStop = { viewModel.stopTask(task.id) }
                    )
                }
            }

            // Right: Tasks Panel
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                TasksPanel(
                    tasks = tasks,
                    selectedTask = selectedTask,
                    onSelectTask = { viewModel.selectTask(it) },
                    onStopTask = { viewModel.stopTask(it) },
                    onDeleteTask = { viewModel.deleteTask(it) }
                )
            }
        }
    }
}

@Composable
fun HeaderSection() {
    Column {
        Text(
            text = "MovieBlock",
            style = MaterialTheme.typography.displayLarge,
            color = Ink
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Automated seat booking for INOXMovies",
            style = MaterialTheme.typography.bodyLarge,
            color = Muted
        )
    }
}

@Composable
fun ErrorBanner(message: String, onDismiss: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Error.copy(alpha = 0.1f))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = Error,
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = onDismiss) {
            Text("✕", color = Error)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlPanel(
    url: String,
    onUrlChange: (String) -> Unit,
    seatInput: String,
    onSeatInputChange: (String) -> Unit,
    checkInterval: Int,
    onCheckIntervalChange: (Int) -> Unit,
    cooldownInterval: Int,
    onCooldownChange: (Int) -> Unit,
    isLoading: Boolean,
    onStart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCard)
            .padding(24.dp)
    ) {
        Text(
            text = "New Task",
            style = MaterialTheme.typography.displaySmall,
            color = Ink
        )

        Spacer(modifier = Modifier.height(20.dp))

        // URL Input
        OutlinedTextField(
            value = url,
            onValueChange = onUrlChange,
            label = { Text("Seat Layout URL", style = MaterialTheme.typography.labelSmall) },
            placeholder = { Text("https://www.inoxmovies.com/seatlayout/...") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = SurfaceDark,
                focusedBorderColor = Primary,
                unfocusedBorderColor = Hairline,
                focusedLabelColor = Primary,
                unfocusedLabelColor = Muted
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Seat IDs Input
        OutlinedTextField(
            value = seatInput,
            onValueChange = onSeatInputChange,
            label = { Text("Seat IDs", style = MaterialTheme.typography.labelSmall) },
            placeholder = { Text("B1, B2, C3...") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = SurfaceDark,
                focusedBorderColor = Primary,
                unfocusedBorderColor = Hairline,
                focusedLabelColor = Primary,
                unfocusedLabelColor = Muted
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Check Interval
        OutlinedTextField(
            value = checkInterval.toString(),
            onValueChange = { onCheckIntervalChange(it.toIntOrNull() ?: 10) },
            label = { Text("Check Interval (seconds)", style = MaterialTheme.typography.labelSmall) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = SurfaceDark,
                focusedBorderColor = Primary,
                unfocusedBorderColor = Hairline,
                focusedLabelColor = Primary,
                unfocusedLabelColor = Muted
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Cooldown Interval
        OutlinedTextField(
            value = cooldownInterval.toString(),
            onValueChange = { onCooldownChange(it.toIntOrNull() ?: 600) },
            label = { Text("Cooldown Period (seconds)", style = MaterialTheme.typography.labelSmall) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = SurfaceDark,
                focusedBorderColor = Primary,
                unfocusedBorderColor = Hairline,
                focusedLabelColor = Primary,
                unfocusedLabelColor = Muted
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Start Button
        Button(
            onClick = onStart,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = OnPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Start Automation", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectedTaskPanel(
    task: Task,
    editSeatInput: String,
    onEditSeatChange: (String) -> Unit,
    onUpdateSeats: () -> Unit,
    onStop: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCard)
            .padding(24.dp)
    ) {
        Text(
            text = "Update Seats",
            style = MaterialTheme.typography.titleLarge,
            color = Ink
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = editSeatInput,
            onValueChange = onEditSeatChange,
            label = { Text("Edit Seat IDs", style = MaterialTheme.typography.labelSmall) },
            placeholder = { Text("B5, B6, C3...") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = SurfaceDark,
                focusedBorderColor = Primary,
                unfocusedBorderColor = Hairline
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onUpdateSeats,
            enabled = editSeatInput.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Update Seats")
        }

        if (task.status in listOf("running", "waiting", "checking", "booked", "booking", "cooling_down", "rechecking")) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onStop,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Error),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Stop Automation")
            }
        }

        // Movie Details
        task.movieDetails?.let { movie ->
            Spacer(modifier = Modifier.height(16.dp))
            MovieDetailsRow(movie)
        }

        // Notifications
        if (task.notifications.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Column {
                Text(
                    text = "Recent Notifications",
                    style = MaterialTheme.typography.labelSmall,
                    color = Muted
                )
                Spacer(modifier = Modifier.height(8.dp))
                task.notifications.takeLast(5).forEach { notif ->
                    NotificationItem(notif.message, notif.type)
                }
            }
        }
    }
}

@Composable
fun NotificationItem(message: String, type: String) {
    val bgColor = when (type) {
        "success" -> Success.copy(alpha = 0.08f)
        "error" -> Error.copy(alpha = 0.08f)
        "warning" -> Warning.copy(alpha = 0.08f)
        else -> SurfaceDarkElevated
    }
    val borderColor = when (type) {
        "success" -> Success
        "error" -> Error
        "warning" -> Warning
        else -> Hairline
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = OnDarkSoft
        )
    }
}

@Composable
fun TasksPanel(
    tasks: List<Task>,
    selectedTask: Task?,
    onSelectTask: (Task) -> Unit,
    onStopTask: (String) -> Unit,
    onDeleteTask: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceDark)
            .padding(24.dp)
    ) {
        Text(
            text = "Active Tasks (${tasks.size})",
            style = MaterialTheme.typography.displaySmall,
            color = OnDark
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (tasks.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No tasks yet.\nEnter a seat layout URL and seat IDs to begin.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedSoft,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                tasks.forEach { task ->
                    TaskCard(
                        task = task,
                        isSelected = selectedTask?.id == task.id,
                        onClick = { onSelectTask(task) },
                        onStop = { onStopTask(task.id) },
                        onDelete = { onDeleteTask(task.id) }
                    )
                }
            }
        }
    }
}
