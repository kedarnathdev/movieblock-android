package com.kedarnathdev.movieblock.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kedarnathdev.movieblock.ui.theme.*
import com.kedarnathdev.movieblock.ui.viewmodel.TaskViewModel

@Composable
fun CreateTaskScreen(
    viewModel: TaskViewModel = viewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var url by remember { mutableStateOf("") }
    var seatInput by remember { mutableStateOf("") }
    var checkInterval by remember { mutableStateOf(10) }
    var cooldownInterval by remember { mutableStateOf(600) }
    var validationError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Canvas)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Header - NEW TASK
        Text(
            text = "NEW TASK",
            style = MaterialTheme.typography.displaySmall,
            color = Ink
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Form Fields
        // Seat Layout URL
        FormLabel("SEAT LAYOUT URL")
        FormInputField(
            value = url,
            onValueChange = { url = it },
            placeholder = "https://www.inoxmovies.com/seatlayout/..."
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Seat IDs
        FormLabel("SEAT IDS")
        FormInputField(
            value = seatInput,
            onValueChange = { seatInput = it },
            placeholder = "B1, B2, C3, etc."
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Preview Button
        Button(
            onClick = { /* Preview functionality */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SecondaryButtonBackground
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Preview Seat Layout",
                style = MaterialTheme.typography.labelLarge,
                color = Ink,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Check Interval
        FormLabel("CHECK INTERVAL (SECONDS)")
        FormInputField(
            value = checkInterval.toString(),
            onValueChange = { checkInterval = it.toIntOrNull() ?: 10 },
            placeholder = "10",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        HelperText("Min 5s, Default 10s")

        Spacer(modifier = Modifier.height(20.dp))

        // Cooldown Period
        FormLabel("COOLDOWN PERIOD (SECONDS)")
        FormInputField(
            value = cooldownInterval.toString(),
            onValueChange = { cooldownInterval = it.toIntOrNull() ?: 600 },
            placeholder = "600",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        HelperText("Min 60s, Default 600s")

        Spacer(modifier = Modifier.height(32.dp))

        // Error display
        error?.let {
            Text(
                text = it,
                color = Error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        validationError?.let {
            Text(
                text = it,
                color = Error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Start Automation Button
        Button(
            onClick = {
                validationError = validateInputs(url, seatInput, checkInterval, cooldownInterval)

                if (validationError == null) {
                    val seats = seatInput.split(",")
                        .map { it.trim().uppercase() }
                        .filter { it.isNotEmpty() }

                    if (seats.isNotEmpty()) {
                        viewModel.createTask(
                            url = url,
                            seatIds = seats,
                            checkIntervalMs = checkInterval * 1000L,
                            cooldownMs = cooldownInterval * 1000L
                        )
                        // Clear form after successful creation
                        url = ""
                        seatInput = ""
                        checkInterval = 10
                        cooldownInterval = 600
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Primary,
                disabledContainerColor = PrimaryDisabled
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = OnPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Start Automation",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Bottom padding for navigation bar
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun FormLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = Ink,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                color = Muted,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        singleLine = true,
        keyboardOptions = keyboardOptions,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = SurfaceInput,
            unfocusedContainerColor = SurfaceInput,
            focusedTextColor = Ink,
            unfocusedTextColor = Ink,
            focusedIndicatorColor = Primary,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Primary
        ),
        textStyle = MaterialTheme.typography.bodyMedium
    )
}

@Composable
private fun HelperText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = Muted,
        modifier = Modifier.padding(top = 6.dp, start = 4.dp)
    )
}

private fun validateInputs(
    url: String,
    seatInput: String,
    checkInterval: Int,
    cooldownInterval: Int
): String? {
    return when {
        url.isEmpty() -> "Please enter a URL"
        !url.startsWith("http://") && !url.startsWith("https://") -> "URL must start with http:// or https://"
        seatInput.isEmpty() -> "Please enter at least one seat ID"
        checkInterval < 5 -> "Check interval must be at least 5 seconds"
        cooldownInterval < 60 -> "Cooldown must be at least 60 seconds"
        else -> null
    }
}
