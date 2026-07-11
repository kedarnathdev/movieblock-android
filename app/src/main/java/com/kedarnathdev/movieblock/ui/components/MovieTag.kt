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
import androidx.compose.ui.unit.dp
import com.kedarnathdev.movieblock.ui.theme.Ink
import com.kedarnathdev.movieblock.ui.theme.SeatTagBackground

/**
 * Movie metadata tag (certificate, genre, language).
 */
@Composable
fun MovieTag(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(SeatTagBackground)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = Ink
        )
    }
}
