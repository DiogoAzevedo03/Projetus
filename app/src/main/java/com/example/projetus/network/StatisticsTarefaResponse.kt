package com.example.projetus.network // Pacote de estatísticas de tarefas

// Data classes for task statistics

data class TaskStat(
    val nome: String, // Nome da tarefa
    val projeto_nome: String, // Nome do projeto
    val estado: String, // Estado atual da tarefa
    val data_criacao: String?, // Data de criação (opcional)
    val data_conclusao: String? // Data de conclusão (opcional)
)

data class StatisticsTarefaResponse(
    val success: Boolean, // Sucesso da chamada
    val tarefas: List<TaskStat>? // Lista de estatísticas por tarefa
) // Resposta com estatísticas de tarefas
