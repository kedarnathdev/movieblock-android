package com.kedarnathdev.movieblock.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kedarnathdev.movieblock.data.model.Task
import com.kedarnathdev.movieblock.data.repository.TaskRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskViewModel : ViewModel() {
    private val repository = TaskRepository()
    
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()
    
    private val _selectedTask = MutableStateFlow<Task?>(null)
    val selectedTask: StateFlow<Task?> = _selectedTask.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private var refreshJob: Job? = null

    init {
        startPolling()
    }

    private fun startPolling() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            while (true) {
                refreshTasks()
                delay(2000) // Poll every 2 seconds
            }
        }
    }

    private suspend fun refreshTasks() {
        repository.getTasks().fold(
            onSuccess = { taskList ->
                _tasks.value = taskList
                // Update selected task if it exists
                _selectedTask.value?.let { selected ->
                    _selectedTask.value = taskList.find { it.id == selected.id }
                }
            },
            onFailure = { /* Silent failure during polling */ }
        )
    }

    fun selectTask(task: Task) {
        _selectedTask.value = task
    }

    fun createTask(
        url: String,
        seatIds: List<String>,
        checkIntervalMs: Long?,
        cooldownMs: Long?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.createTask(url, seatIds, checkIntervalMs, cooldownMs).fold(
                onSuccess = { task ->
                    _selectedTask.value = task
                    refreshTasks()
                },
                onFailure = { e ->
                    _error.value = e.message ?: "Failed to create task"
                }
            )
            _isLoading.value = false
        }
    }

    fun updateSeats(taskId: String, seats: List<String>) {
        viewModelScope.launch {
            repository.updateSeats(taskId, seats).fold(
                onSuccess = { task ->
                    _selectedTask.value = task
                    refreshTasks()
                },
                onFailure = { e ->
                    _error.value = e.message ?: "Failed to update seats"
                }
            )
        }
    }

    fun stopTask(taskId: String) {
        viewModelScope.launch {
            repository.stopTask(taskId).fold(
                onSuccess = { task ->
                    _selectedTask.value = task
                    refreshTasks()
                },
                onFailure = { e ->
                    _error.value = e.message ?: "Failed to stop task"
                }
            )
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            repository.deleteTask(taskId).fold(
                onSuccess = {
                    if (_selectedTask.value?.id == taskId) {
                        _selectedTask.value = null
                    }
                    refreshTasks()
                },
                onFailure = { e ->
                    _error.value = e.message ?: "Failed to delete task"
                }
            )
        }
    }

    fun clearError() {
        _error.value = null
    }

    override fun onCleared() {
        super.onCleared()
        refreshJob?.cancel()
    }
}
