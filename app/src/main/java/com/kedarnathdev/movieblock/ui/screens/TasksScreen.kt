package com.kedarnathdev.movieblock.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
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
                // Task List - Using LazyColumn for better performance with many tasks
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(tasks, key = { it.id }) { task ->
                        TaskCard(
                            task = task,
                            isSelected = selectedTaskId.value == task.id,
                            onSelect = {
                                selectedTaskId.value = if (selectedTaskId.value == task.id) null else task.id
                            },
                            onUpdateSeats = { seats ->
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

@Composable
fun TaskCard(
    task: Task,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onUpdateSeats: (List<String>) -> Unit,
    onStop: () -> Unit,
    onDelete: () -> Unit
) {
    val isActive = task.status in listOf("running", "waiting", "checking", "booked", "booking", "cooling_down", "rechecking")
    
    // Per-task edit state
    var editSeatInput by remember(task.id) { mutableStateOf(task.selectedSeats.joinToString(", ")) }
    
    // Status color
    val statusColor = when (task.status) {
        "running", "booked" -> Success
        "waiting", "cooling_down" -> AccentAmber
        "checking", "booking", "rechecking" -> AccentTeal
        "error" -> Error
        else -> Muted
    }
    
    // Date formatters
    val inputFormat = remember {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }
    val outputFormat = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }
    
    fun parseTimestamp(timestamp: String?): String {
        return try {
            timestamp?.let { inputFormat.parse(it)?.let { outputFormat.format(it) } } ?: "N/A"
        } catch (e: Exception) {
            "N/A"
        }
    }
    
    fun formatDuration(ms: Long?): String {
        if (ms == null) return "N/A"
        val mins = ms / 60000
        val secs = (ms % 60000) / 1000
        return if (mins > 0) "${mins}m ${secs}s" else "${secs}s"
    }
    
    fun formatElapsed(startTime: Long?): String {
        if (startTime == null) return "N/A"
        val diff = System.currentTimeMillis() - startTime
        return formatDuration(diff)
    }
    
    fun getEstimatedUnlockTime(lastBookedAt: Long?, lastBlockDuration: Long?): String {
        if (lastBookedAt == null) return "N/A"
        val blockMs = lastBlockDuration ?: (20 * 60 * 1000)
        val unlockTime = Date(lastBookedAt + blockMs)
        return outputFormat.format(unlockTime)
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 4.dp,
                color = statusColor,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        onClick = onSelect
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Status Badge + Stop Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status Badge
                Surface(
                    shape = RoundedCornerShape(50),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = task.status.replace("_", " ").uppercase(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // Stop/Delete Button
                if (isActive) {
                    Button(
                        onClick = onStop,
                        colors = ButtonDefaults.buttonColors(containerColor = Error),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            "Stop",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Error),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            "Delete",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            // URL
            Text(
                text = task.url,
                style = MaterialTheme.typography.bodySmall,
                color = Muted,
                maxLines = 2,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            // Movie Section
            if (task.movieDetails != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // Poster
                    task.movieDetails.posterUrl?.let { posterUrl ->
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(posterUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Movie Poster",
                            modifier = Modifier
                                .width(60.dp)
                                .height(90.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceSoft),
                            contentScale = ContentScale.Crop
                        )
                    }
                    
                    // Movie Info
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Title
                        task.movieDetails.title?.let { title ->
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium,
                                color = Ink,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                        
                        // Tags (Certificate, Genre, Language)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            task.movieDetails.certificate?.let {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = AccentTeal.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = it,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AccentTeal,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                            task.movieDetails.genre?.let {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = AccentTeal.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = it,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AccentTeal,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                            task.movieDetails.language?.let {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = AccentTeal.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = it,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AccentTeal,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                        
                        // Showtime
                        task.movieDetails.showtime?.let { showtime ->
                            Text(
                                text = showtime,
                                style = MaterialTheme.typography.bodySmall,
                                color = AccentAmber,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        // Theater
                        task.movieDetails.theater?.let { theater ->
                            Text(
                                text = theater,
                                style = MaterialTheme.typography.bodySmall,
                                color = Muted,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
            
            // Statistics Grid (2 columns)
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Row 1: STARTED | LAST CHECKED
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatItem(label = "STARTED", value = parseTimestamp(task.startedAt), modifier = Modifier.weight(1f))
                    StatItem(label = "LAST CHECKED", value = parseTimestamp(task.lastChecked), modifier = Modifier.weight(1f))
                }
                
                // Row 2: LOOP | ATTEMPTS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatItem(label = "LOOP", value = "#${task.currentLoop}", modifier = Modifier.weight(1f))
                    StatItem(label = "ATTEMPTS", value = "${task.attempts}", modifier = Modifier.weight(1f))
                }
                
                // Row 3: CYCLE TIME | LAST BLOCK
                if (isActive || task.lastBlockDuration != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatItem(
                            label = "CYCLE TIME",
                            value = formatElapsed(task.currentCycleStartedAt),
                            modifier = Modifier.weight(1f),
                            valueColor = AccentAmber
                        )
                        StatItem(
                            label = "LAST BLOCK",
                            value = formatDuration(task.lastBlockDuration),
                            modifier = Modifier.weight(1f),
                            valueColor = AccentAmber
                        )
                    }
                }
                
                // Row 4: LAST REBOOKED | REBOOKS
                if (task.lastBookedAt != null || task.rebookCount > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatItem(
                            label = "LAST REBOOKED",
                            value = if (task.lastBookedAt != null) outputFormat.format(Date(task.lastBookedAt)) else "N/A",
                            modifier = Modifier.weight(1f)
                        )
                        StatItem(label = "REBOOKS", value = "${task.rebookCount}", modifier = Modifier.weight(1f))
                    }
                }
                
                // Row 5: EST. UNLOCK
                if (isActive) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatItem(
                            label = "EST. UNLOCK",
                            value = "~${getEstimatedUnlockTime(task.lastBookedAt, task.lastBlockDuration)}",
                            modifier = Modifier.weight(1f),
                            valueColor = AccentAmber
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            
            // Seats Row
            if (task.selectedSeats.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    task.selectedSeats.forEach { seat ->
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = SeatTagBackground
                        ) {
                            Text(
                                text = seat,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = SeatTagText,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
            
            // Footer: Rechecking indicator
            if (task.status == "rechecking" && task.checkIntervalMs != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SurfaceDarkElevated,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = AccentTeal,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Rechecking every ${task.checkIntervalMs / 1000}s",
                            style = MaterialTheme.typography.bodySmall,
                            color = AccentTeal,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            // Expanded Details (Edit Seats)
            if (isSelected) {
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Hairline, thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Update Seats",
                    style = MaterialTheme.typography.titleSmall,
                    color = Ink,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = editSeatInput,
                    onValueChange = { editSeatInput = it },
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
                    onClick = {
                        val seats = editSeatInput.split(",")
                            .map { s -> s.trim().uppercase() }
                            .filter { it.isNotEmpty() }
                        onUpdateSeats(seats)
                    },
                    enabled = editSeatInput.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Update Seats")
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

@Composable
private fun StatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: androidx.compose.ui.graphics.Color = OnDark
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MutedSoft,
            fontWeight = FontWeight.Normal
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}
