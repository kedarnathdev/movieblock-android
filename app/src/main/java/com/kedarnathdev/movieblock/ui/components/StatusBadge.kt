package com.kedarnathdev.movieblock.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kedarnathdev.movieblock.ui.theme.*

/**
 * Status badge component for task cards.
 * Shows the current status with appropriate color coding.
 */
@Composable
fun StatusBadge(status: String) {
    val (bgColor, textColor) = when (status.lowercase()) {
        "running", "started" -> AccentTeal.copy(alpha = 0.15f) to AccentTeal
        "checking", "booking", "rechecking" -> AccentTeal.copy(alpha = 0.15f) to AccentTeal
        "booked" -> Success.copy(alpha = 0.15f) to Success
        "waiting" -> AccentAmber.copy(alpha = 0.15f) to AccentAmber
        "cooling_down" -> AccentAmber.copy(alpha = 0.15f) to AccentAmber
        "stopped" -> Muted.copy(alpha = 0.15f) to Muted
        "error" -> Error.copy(alpha = 0.15f) to Error
        else -> MutedSoft.copy(alpha = 0.15f) to MutedSoft
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bgColor)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = status.replace("_", " ").uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}
