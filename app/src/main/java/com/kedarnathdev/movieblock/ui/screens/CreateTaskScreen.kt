package com.kedarnathdev.movieblock.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kedarnathdev.movieblock.ui.theme.*
import com.kedarnathdev.movieblock.ui.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    viewModel: TaskViewModel = viewModel(),
    onNavigateToTasks: () -> Unit
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    var url by remember { mutableStateOf("") }
    var seatInput by remember { mutableStateOf("") }
    var checkInterval by remember { mutableStateOf(10) }
    var cooldownInterval by remember { mutableStateOf(600) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Canvas)
    ) {
        // Top Bar with Tasks button
        TopAppBar(
            title = { 
                Text(
                    "MovieBlock",
                    color = Ink,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = SurfaceCard
            ),
            actions = {
                TextButton(onClick = onNavigateToTasks) {
                    Icon(
                        Icons.Default.List,
                        contentDescription = null,
                        tint = Primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "View Tasks",
                        color = Primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                "Create New Task",
                style = MaterialTheme.typography.headlineMedium,
                color = Ink
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                "Automate seat booking for INOXMovies",
                style = MaterialTheme.typography.bodyMedium,
                color = Muted
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Form Card
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceCard)
                    .padding(24.dp)
            ) {
                // Error banner
                error?.let { errMsg ->
                    Snackbar(
                        modifier = Modifier.padding(bottom = 16.dp),
                        action = {
                            TextButton(onClick = { viewModel.clearError() }) {
                                Text("Dismiss")
                            }
                        }
                    ) {
                        Text(errMsg)
                    }
                }

                // URL Input
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("Seat Layout URL") },
                    placeholder = { Text("https://www.inoxmovies.com/seatlayout/...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = Hairline,
                        focusedLabelColor = Primary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Seat IDs Input
                OutlinedTextField(
                    value = seatInput,
                    onValueChange = { seatInput = it },
                    label = { Text("Seat IDs (comma separated)") },
                    placeholder = { Text("B1, B2, C3...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = Hairline,
                        focusedLabelColor = Primary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Check Interval
                    OutlinedTextField(
                        value = checkInterval.toString(),
                        onValueChange = { checkInterval = it.toIntOrNull() ?: 10 },
                        label = { Text("Check Interval (sec)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = Hairline,
                            focusedLabelColor = Primary
                        )
                    )

                    // Cooldown Interval
                    OutlinedTextField(
                        value = cooldownInterval.toString(),
                        onValueChange = { cooldownInterval = it.toIntOrNull() ?: 600 },
                        label = { Text("Cooldown (sec)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = Hairline,
                            focusedLabelColor = Primary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Start Button
                Button(
                    onClick = {
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
                    },
                    enabled = !isLoading && url.isNotEmpty() && seatInput.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
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
                            "Start Automation",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick tip
            Text(
                "💡 Tip: After creating a task, use 'View Tasks' to monitor and manage it",
                style = MaterialTheme.typography.bodySmall,
                color = MutedSoft
            )
        }
    }
}
