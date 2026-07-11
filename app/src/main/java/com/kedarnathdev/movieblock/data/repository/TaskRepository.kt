package com.kedarnathdev.movieblock.data.repository

import com.kedarnathdev.movieblock.data.api.ApiClient
import com.kedarnathdev.movieblock.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class TaskRepository {
    // Lazy initialization to prevent creating multiple OkHttpClient instances
    private val api by lazy { ApiClient.getApi() }

    suspend fun getTasks(): Result<List<Task>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getTasks()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.tasks)
            } else {
                Result.failure(Exception("Failed to fetch tasks: ${response.code()}"))
            }
        } catch (e: UnknownHostException) {
            Result.failure(Exception("No internet connection"))
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("Connection timeout"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTask(taskId: String): Result<Task> = withContext(Dispatchers.IO) {
        try {
            val response = api.getTask(taskId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.task)
            } else {
                Result.failure(Exception("Task not found"))
            }
        } catch (e: UnknownHostException) {
            Result.failure(Exception("No internet connection"))
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("Connection timeout"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createTask(
        url: String,
        seatIds: List<String>,
        checkIntervalMs: Long? = null,
        cooldownMs: Long? = null
    ): Result<Task> = withContext(Dispatchers.IO) {
        try {
            val request = CreateTaskRequest(url, seatIds, checkIntervalMs, cooldownMs)
            val response = api.createTask(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.task)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to create task: $errorBody"))
            }
        } catch (e: UnknownHostException) {
            Result.failure(Exception("No internet connection"))
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("Connection timeout"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateSeats(taskId: String, seats: List<String>): Result<Task> = withContext(Dispatchers.IO) {
        try {
            val response = api.updateSeats(taskId, UpdateSeatsRequest(seats))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.task)
            } else {
                Result.failure(Exception("Failed to update seats"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun stopTask(taskId: String): Result<Task> = withContext(Dispatchers.IO) {
        try {
            val response = api.stopTask(taskId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.task)
            } else {
                Result.failure(Exception("Failed to stop task"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTask(taskId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val response = api.deleteTask(taskId)
            Result.success(response.isSuccessful)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNotificationHistory(
        taskId: String,
        limit: Int = 100,
        type: String? = null
    ): Result<List<Notification>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getNotificationHistory(taskId, limit, 0, type)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.notifications)
            } else {
                Result.failure(Exception("Failed to fetch notifications"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNotificationStats(taskId: String): Result<NotificationStatsResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.getNotificationStats(taskId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch notification stats"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearNotificationHistory(taskId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val response = api.clearNotificationHistory(taskId)
            Result.success(response.isSuccessful)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
