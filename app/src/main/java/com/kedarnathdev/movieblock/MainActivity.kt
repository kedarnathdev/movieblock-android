package com.kedarnathdev.movieblock

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.kedarnathdev.movieblock.ui.theme.MovieBlockTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHighRefreshRate()
        setContent {
            MovieBlockTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MovieBlockNavigation()
                }
            }
        }
    }

    /**
     * Requests high refresh rate display mode for devices running Android 11+ (API 30+).
     * This provides smoother animations and scrolling on devices with high refresh rate displays.
     */
    private fun setHighRefreshRate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.attributes = window.attributes.apply {
                // Find the display mode with the highest refresh rate
                val display = window.context.display ?: return@apply
                val supportedModes = display.supportedModes
                val highestRefreshRateMode = supportedModes.maxByOrNull { it.refreshRate }
                highestRefreshRateMode?.let {
                    preferredDisplayModeId = it.modeId
                }
            }
        }
    }
}
