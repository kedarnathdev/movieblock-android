package com.kedarnathdev.movieblock.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kedarnathdev.movieblock.data.model.MovieDetails
import com.kedarnathdev.movieblock.data.model.Task
import com.kedarnathdev.movieblock.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Redesigned Task Card matching the new design.
 */
@Composable
fun TaskCard(
    task: Task,
    onStop: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isActive = task.status in listOf("running", "waiting", "checking", "booked", "booking", "cooling_down", "rechecking")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceCard)
            .padding(16.dp)
    ) {
        // Header: Status badge, Delete button (if inactive), Last Checked
        TaskCardHeader(
            task = task,
            isActive = isActive,
            onDelete = onDelete
        )

        // Movie details section
        task.movieDetails?.let { movie ->
            Spacer(modifier = Modifier.height(16.dp))
            MovieDetailsSection(movie = movie)
        }

        // Stats grid
        Spacer(modifier = Modifier.height(16.dp))
        TaskStatsGrid(task = task)

        // Action section (URL/Seats and Stop button)
        Spacer(modifier = Modifier.height(12.dp))
        TaskActionSection(
            task = task,
            isActive = isActive,
            onStop = onStop
        )

        // Cooldown timer (if applicable)
        if (task.status == "cooling_down" && task.waitEndTime != null) {
            Spacer(modifier = Modifier.height(12.dp))
            CooldownSection(endTime = task.waitEndTime)
        }

        // Error message
        task.error?.let { error ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Error: $error",
                style = MaterialTheme.typography.bodySmall,
                color = Error
            )
        }
    }
}

@Composable
private fun TaskCardHeader(
    task: Task,
    isActive: Boolean,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Status badge
        StatusBadge(status = task.status)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Last Checked
            val lastCheckedFormatted = formatLastChecked(task.lastChecked)
            Text(
                text = "Last Checked: $lastCheckedFormatted",
                style = MaterialTheme.typography.bodySmall,
                color = Muted
            )

            // Delete button when not active
            if (!isActive) {
                TextButton(
                    onClick = onDelete,
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        text = "Delete",
                        color = MutedSoft,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun MovieDetailsSection(movie: MovieDetails) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Movie poster
        Box(
            modifier = Modifier
                .size(60.dp, 90.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(SurfaceInput)
        ) {
            movie.posterUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = movie.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Movie info
        Column(modifier = Modifier.weight(1f)) {
            movie.title?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium,
                    color = Ink,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Tags row
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                movie.certificate?.let { MovieTag(text = it) }
                movie.genre?.let { MovieTag(text = it) }
                movie.language?.let { MovieTag(text = it) }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Showtime
            movie.showtime?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Muted
                )
            }

            // Theater
            movie.theater?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Muted
                )
            }
        }
    }
}

@Composable
private fun TaskStatsGrid(task: Task) {
    val cycleTime = calculateCycleTime(task)
    val lastBlock = formatDuration(task.lastBlockDuration)
    val lastRebooked = task.lastBookedAt?.let { formatTimestamp(it) } ?: "—"
    val estUnlock = calculateEstimatedUnlock(task)

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "Loop: ${task.currentLoop}  |  Attempts: ${task.attempts}  |  Cycle Time: $cycleTime",
            style = MaterialTheme.typography.bodySmall,
            color = Muted
        )
        Text(
            text = "Last Block: $lastBlock  |  Last Rebooked: $lastRebooked",
            style = MaterialTheme.typography.bodySmall,
            color = Muted
        )
        val unlockText = estUnlock?.let { "  |  Est. Unlock: $it" } ?: ""
        Text(
            text = "Rebooks: ${task.rebookCount}$unlockText",
            style = MaterialTheme.typography.bodySmall,
            color = Muted
        )
    }
}

@Composable
private fun TaskActionSection(
    task: Task,
    isActive: Boolean,
    onStop: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceInput)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // URL
            Text(
                text = "URL: ${truncateUrl(task.url)}",
                style = MaterialTheme.typography.bodySmall,
                color = Muted,
                maxLines = 1
            )

            // Seat IDs
            if (task.selectedSeats.isNotEmpty()) {
                Text(
                    text = "Seat IDs: ${task.selectedSeats.joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Muted
                )
            } else {
                Text(
                    text = "Total full checks on this seat map",
                    style = MaterialTheme.typography.bodySmall,
                    color = Muted
                )
            }
        }

        if (isActive) {
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = onStop,
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                shape = RoundedCornerShape(50),
                modifier = Modifier.height(32.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
            ) {
                Text(
                    text = "Stop",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun CooldownSection(endTime: Long) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Progress Line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(Primary.copy(alpha = 0.3f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f) // Static placeholder for visual indicator
                    .height(2.dp)
                    .background(Primary)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    tint = Muted,
                    modifier = Modifier.size(16.dp)
                )
            }

            CountdownTimer(endTime = endTime)
        }
    }
}

// Helper functions

private fun formatLastChecked(timestamp: String?): String {
    if (timestamp == null) return "—"
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(timestamp)
        val outputFormat = SimpleDateFormat("h:mm:ss a", Locale.getDefault())
        outputFormat.format(date!!)
    } catch (e: Exception) {
        "—"
    }
}

private fun calculateCycleTime(task: Task): String {
    val start = task.currentCycleStartedAt ?: return "N/A"
    val diff = System.currentTimeMillis() - start
    val seconds = diff / 1000.0
    return "${String.format("%.1f", seconds)}s"
}

private fun formatDuration(ms: Long?): String {
    if (ms == null) return "—"
    val mins = ms / 60000
    val secs = (ms % 60000) / 1000
    return if (mins > 0) "${mins}m ${secs}s" else "${secs}s"
}

private fun formatTimestamp(ms: Long): String {
    return try {
        val outputFormat = SimpleDateFormat("h:mm:ss a", Locale.getDefault())
        outputFormat.format(Date(ms))
    } catch (e: Exception) {
        "—"
    }
}

private fun calculateEstimatedUnlock(task: Task): String? {
    val booked = task.lastBookedAt ?: return null
    val duration = task.lastBlockDuration ?: return null
    val unlockTime = booked + duration
    return formatTimestamp(unlockTime)
}

private fun truncateUrl(url: String): String {
    return if (url.length > 40) url.take(37) + "..." else url
}
