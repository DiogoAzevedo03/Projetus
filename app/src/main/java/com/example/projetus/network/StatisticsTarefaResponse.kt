package com.example.projetus.network

// Data classes for task statistics

data class TaskStat(
    val nome: String,
    val projeto_nome: String,
    val estado: String,
    val data_criacao: String?,
    val data_conclusao: String?
)

data class StatisticsTarefaResponse(
    val success: Boolean,
    val tarefas: List<TaskStat>?
)