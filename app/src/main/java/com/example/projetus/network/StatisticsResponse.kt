package com.example.projetus.network


data class StatisticsResponse(
    val success: Boolean,
    val nome: String,
    val tarefas_atribuidas: Int,
    val tarefas_concluidas: Int,
    val taxa_conclusao: Int,
    val projetos_concluidos: Int,

)
