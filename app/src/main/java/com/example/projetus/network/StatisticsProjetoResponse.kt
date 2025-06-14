package com.example.projetus.network // Pacote de estatísticas de projeto

data class ProjectStat(
    val nome: String, // Nome do projeto
    val tarefas_atribuidas: Int, // Total de tarefas atribuídas
    val tarefas_concluidas: Int, // Tarefas concluídas
    val taxa_conclusao: Int // Percentual de conclusão
)

data class StatisticsProjetoResponse(
    val success: Boolean, // Indica sucesso na consulta
    val projetos: List<ProjectStat>? // Lista de estatísticas por projeto
) // Resposta com estatísticas de projetos
