package com.kedarnathdev.movieblock.data.model

import com.google.gson.annotations.SerializedName

data class Task(
    val id: String,
    val url: String,
    val status: String,
    val selectedSeats: List<String> = emptyList(),
    val startedAt: String? = null,
    val stoppedAt: String? = null,
    val lastChecked: String? = null,
    val attempts: Int = 0,
    val error: String? = null,
    val notifications: List<Notification> = emptyList(),
    val waitEndTime: Long? = null,
    val currentLoop: Int = 0,
    val seatBlocked: Boolean = false,
    val movieDetails: MovieDetails? = null,
    val lastBookedAt: Long? = null,
    val lastBlockDuration: Long? = null,
    val currentCycleStartedAt: Long? = null,
    val rebookCount: Int = 0,
    val checkIntervalMs: Long? = null,
    val cooldownMs: Long? = null,
    val recommendedCooldownMs: Long? = null,
    val cooldownRecommendationMeta: CooldownRecommendationMeta? = null,
    val message: String? = null,
    val taskId: String? = null
)

data class MovieDetails(
    val title: String? = null,
    val certificate: String? = null,
    val genre: String? = null,
    val language: String? = null,
    val showtime: String? = null,
    val theater: String? = null,
    val posterUrl: String? = null
)

data class Notification(
    val message: String,
    val type: String = "info",
    val timestamp: String
)

data class CooldownRecommendationMeta(
    val averageMs: Long? = null,
    val sampleSize: Int,
    val calculatedAt: String
)

data class TasksResponse(
    val tasks: List<Task>
)

data class TaskResponse(
    val task: Task
)

data class CreateTaskRequest(
    val url: String,
    val seatIds: List<String>,
    val checkIntervalMs: Long? = null,
    val cooldownMs: Long? = null
)

data class UpdateSeatsRequest(
    val seats: List<String>
)

data class NotificationHistoryResponse(
    val notifications: List<Notification>,
    val total: Int,
    val hasMore: Boolean
)

data class NotificationStatsResponse(
    val total: Int,
    val types: Map<String, Int>
)

data class CooldownRecommendationResponse(
    val recommendedMs: Long? = null,
    val basedOnHistory: Boolean,
    val averageMs: Long? = null,
    val sampleSize: Int,
    val message: String
)
