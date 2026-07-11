package com.kedarnathdev.movieblock.ui.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kedarnathdev.movieblock.data.model.Task
import com.kedarnathdev.movieblock.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.TimeZone
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCard(
    task: Task,
    isSelected: Boolean,
    onClick: () -> Unit,
    onStop: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isActive = task.status in listOf("running", "waiting", "checking", "booked", "booking", "cooling_down", "rechecking")
    
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) SurfaceDarkElevated else SurfaceDark
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with status and actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    StatusBadge(status = task.status)
                    Text(
                        text = task.url.substringAfterLast("/"),
                        style = MaterialTheme.typography.bodySmall,
                        color = OnDarkSoft,
                        maxLines = 2,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                
                if (isActive) {
                    Button(
                        onClick = onStop,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Error
                        ),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Stop", style = MaterialTheme.typography.labelMedium)
                    }
                } else {
                    TextButton(
                        onClick = onDelete,
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Delete", color = MutedSoft, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            // Movie details
            task.movieDetails?.let { movie ->
                Spacer(modifier = Modifier.height(12.dp))
                MovieDetailsRow(movie)
            }

            // Task info grid
            Spacer(modifier = Modifier.height(12.dp))
            TaskInfoGrid(
                task = task,
                showElapsed = isActive
            )

            // Seat tags
            if (task.selectedSeats.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                SeatTags(seats = task.selectedSeats)
            }

            // Status callout
            if (task.status == "booked" || task.status == "cooling_down" || task.status == "rechecking") {
                Spacer(modifier = Modifier.height(8.dp))
                StatusCallout(status = task.status, waitEndTime = task.waitEndTime)
            }

            // Error message
            task.error?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Error: $error",
                    style = MaterialTheme.typography.bodySmall,
                    color = Error
                )
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bgColor, textColor) = when (status) {
        "running" -> Success.copy(alpha = 0.15f) to Success
        "waiting" -> AccentAmber.copy(alpha = 0.15f) to AccentAmber
        "checking" -> AccentTeal.copy(alpha = 0.15f) to AccentTeal
        "booked" -> Success.copy(alpha = 0.15f) to Success
        "booking" -> AccentTeal.copy(alpha = 0.15f) to AccentTeal
        "cooling_down" -> AccentAmber.copy(alpha = 0.15f) to AccentAmber
        "rechecking" -> AccentTeal.copy(alpha = 0.15f) to AccentTeal
        "stopped" -> Muted.copy(alpha = 0.15f) to Muted
        "error" -> Error.copy(alpha = 0.15f) to Error
        else -> MutedSoft.copy(alpha = 0.15f) to MutedSoft
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bgColor)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.replace("_", " ").uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun MovieDetailsRow(
    movie: com.kedarnathdev.movieblock.data.model.MovieDetails
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Primary.copy(alpha = 0.06f))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Poster placeholder - would use Coil in production
        Box(
            modifier = Modifier
                .size(56.dp, 80.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(SurfaceDarkElevated),
            contentAlignment = Alignment.Center
        ) {
            movie.posterUrl?.let {
                // In production: Coil image loading
                Text("🎬", style = MaterialTheme.typography.displaySmall)
            }
        }
        
        Column(modifier = Modifier.weight(1f)) {
            movie.title?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium,
                    color = OnDark,
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                movie.certificate?.let {
                    MetaTag(it)
                }
                movie.genre?.let {
                    MetaTag(it, color = AccentTeal)
                }
            }
            movie.showtime?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = AccentAmber,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun MetaTag(text: String, color: Color = Primary) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 2.dp)
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
fun TaskInfoGrid(
    task: Task,
    showElapsed: Boolean
) {
    // Cache SimpleDateFormat instances to prevent recreation on every recomposition
    val inputFormat = remember {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }
    val outputFormat = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }
    
    fun parseTimestamp(timestamp: String?): String? {
        return try {
            timestamp?.let { inputFormat.parse(it)?.let { outputFormat.format(it) } }
        } catch (e: Exception) {
            null
        }
    }
    
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            InfoItem("Started", parseTimestamp(task.startedAt) ?: "N/A")
            InfoItem("Last Checked", parseTimestamp(task.lastChecked) ?: "N/A")
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            InfoItem("Loop", "#${task.currentLoop}")
            InfoItem("Attempts", "${task.attempts}")
        }
        
        if (showElapsed && task.currentCycleStartedAt != null) {
            var elapsed by remember { mutableStateOf(0L) }
            LaunchedEffect(task.currentCycleStartedAt) {
                val start = task.currentCycleStartedAt
                while (true) {
                    elapsed = System.currentTimeMillis() - start
                    kotlinx.coroutines.delay(1000)
                }
            }
            val hours = elapsed / 3600000
            val mins = (elapsed % 3600000) / 60000
            val secs = (elapsed % 60000) / 1000
            val elapsedStr = when {
                hours > 0 -> "${hours}h ${mins}m ${secs}s"
                mins > 0 -> "${mins}m ${secs}s"
                else -> "${secs}s"
            }
            InfoItem("Cycle Time", elapsedStr, valueColor = AccentAmber)
        }
        
        if (task.rebookCount > 0) {
            InfoItem("Rebooks", "${task.rebookCount}")
        }
    }
}

@Composable
fun InfoItem(label: String, value: String, valueColor: Color = OnDark) {
    Column {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MutedSoft
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor,
            fontFamily = com.kedarnathdev.movieblock.ui.theme.CodeFontFamily
        )
    }
}

@Composable
fun SeatTags(seats: List<String>) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.wrapContentWidth()
    ) {
        seats.forEach { seat ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Primary.copy(alpha = 0.15f))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = seat,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Primary,
                    fontFamily = CodeFontFamily,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun StatusCallout(status: String, waitEndTime: Long?) {
    val (bgColor, textColor) = when (status) {
        "booked" -> Success.copy(alpha = 0.1f) to Success
        "cooling_down" -> AccentAmber.copy(alpha = 0.1f) to AccentAmber
        "rechecking" -> AccentTeal.copy(alpha = 0.1f) to AccentTeal
        else -> Muted.copy(alpha = 0.1f) to Muted
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (status) {
            "booked" -> Text("✓", style = MaterialTheme.typography.bodyLarge, color = textColor)
            "cooling_down" -> Text("⏳", style = MaterialTheme.typography.bodyLarge)
            "rechecking" -> Text("🔄", style = MaterialTheme.typography.bodyLarge)
        }
        
        Text(
            text = when (status) {
                "booked" -> "Seats Selected"
                "cooling_down" -> "Cooldown"
                "rechecking" -> "Rechecking every 10s"
                else -> status.replace("_", " ").capitalize()
            },
            style = MaterialTheme.typography.bodyMedium,
            color = textColor
        )
        
        if (status == "cooling_down" && waitEndTime != null) {
            Spacer(modifier = Modifier.weight(1f))
            CountdownTimer(endTime = waitEndTime)
        }
    }
}

@Composable
fun CountdownTimer(endTime: Long) {
    var remaining by remember { mutableStateOf(max(0L, endTime - System.currentTimeMillis())) }
    
    LaunchedEffect(endTime) {
        while (true) {
            remaining = max(0L, endTime - System.currentTimeMillis())
            if (remaining <= 0) break
            kotlinx.coroutines.delay(1000)
        }
    }
    
    val mins = remaining / 60000
    val secs = (remaining % 60000) / 1000
    
    Text(
        text = "${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}",
        style = MaterialTheme.typography.displaySmall,
        color = Primary,
        fontFamily = CodeFontFamily
    )
}

@Composable
fun NotificationItem(message: String, type: String) {
    val (icon, color) = when (type) {
        "success" -> "✓" to Success
        "error" -> "✗" to Error
        "warning" -> "⚠" to AccentAmber
        else -> "•" to Muted
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.08f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.bodySmall,
            color = color
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = OnDark
        )
    }
}
