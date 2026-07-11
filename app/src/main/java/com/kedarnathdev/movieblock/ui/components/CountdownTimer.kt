package com.kedarnathdev.movieblock.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.kedarnathdev.movieblock.ui.theme.AccentAmber
import com.kedarnathdev.movieblock.ui.theme.CodeFontFamily
import kotlinx.coroutines.delay
import kotlin.math.max

/**
 * Countdown timer component for cooldown display.
 * Updates every second and shows MM:SS format.
 */
@Composable
fun CountdownTimer(
    endTime: Long,
    modifier: Modifier = Modifier
) {
    var remaining by remember { mutableLongStateOf(max(0L, endTime - System.currentTimeMillis())) }

    LaunchedEffect(endTime) {
        while (remaining > 0) {
            delay(1000)
            remaining = max(0L, endTime - System.currentTimeMillis())
        }
    }

    val mins = remaining / 60000
    val secs = (remaining % 60000) / 1000
    val timeString = "${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"

    Text(
        text = timeString,
        style = MaterialTheme.typography.titleLarge,
        color = AccentAmber,
        fontFamily = CodeFontFamily,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
    )
}

/**
 * Compact countdown for inline display.
 */
@Composable
fun CompactCountdownTimer(
    endTime: Long,
    modifier: Modifier = Modifier
) {
    var remaining by remember { mutableLongStateOf(max(0L, endTime - System.currentTimeMillis())) }

    LaunchedEffect(endTime) {
        while (remaining > 0) {
            delay(1000)
            remaining = max(0L, endTime - System.currentTimeMillis())
        }
    }

    val mins = remaining / 60000
    val secs = (remaining % 60000) / 1000
    val timeString = "${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"

    Text(
        text = timeString,
        style = MaterialTheme.typography.bodyLarge,
        color = AccentAmber,
        fontFamily = CodeFontFamily,
        fontWeight = FontWeight.Medium,
        modifier = modifier
    )
}
