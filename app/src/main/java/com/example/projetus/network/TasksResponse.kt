package com.example.projetus.network

data class TasksResponse(
    val success: Boolean,
    val tarefas: List<Task>
)
