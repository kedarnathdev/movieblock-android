package com.kedarnathdev.movieblock.data.api

import com.kedarnathdev.movieblock.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface MovieBlockApi {
    @GET("tasks")
    suspend fun getTasks(): Response<TasksResponse>

    @GET("tasks/{taskId}")
    suspend fun getTask(@Path("taskId") taskId: String): Response<TaskResponse>

    @POST("tasks")
    suspend fun createTask(@Body request: CreateTaskRequest): Response<TaskResponse>

    @POST("tasks/{taskId}/seats")
    suspend fun updateSeats(
        @Path("taskId") taskId: String,
        @Body request: UpdateSeatsRequest
    ): Response<TaskResponse>

    @POST("tasks/{taskId}/stop")
    suspend fun stopTask(@Path("taskId") taskId: String): Response<TaskResponse>

    @DELETE("tasks/{taskId}")
    suspend fun deleteTask(@Path("taskId") taskId: String): Response<Map<String, Boolean>>

    @GET("tasks/{taskId}/notifications")
    suspend fun getNotificationHistory(
        @Path("taskId") taskId: String,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0,
        @Query("type") type: String? = null
    ): Response<NotificationHistoryResponse>

    @GET("tasks/{taskId}/notifications/stats")
    suspend fun getNotificationStats(@Path("taskId") taskId: String): Response<NotificationStatsResponse>

    @DELETE("tasks/{taskId}/notifications")
    suspend fun clearNotificationHistory(@Path("taskId") taskId: String): Response<Map<String, Boolean>>

    @GET("tasks/{taskId}/cooldown-recommendation")
    suspend fun getCooldownRecommendation(@Path("taskId") taskId: String): Response<CooldownRecommendationResponse>
}
