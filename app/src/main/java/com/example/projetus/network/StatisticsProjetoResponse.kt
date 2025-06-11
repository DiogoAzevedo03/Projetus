package com.example.projetus.network

data class ProjectStat(
    val nome: String,
    val tarefas_atribuidas: Int,
    val tarefas_concluidas: Int,
    val taxa_conclusao: Int
)

data class StatisticsProjetoResponse(
    val success: Boolean,
    val projetos: List<ProjectStat>?
)
