package com.example.projetus.network

data class DashboardResponse(
    val success: Boolean,
    val projetos_ativos: Int,
    val tarefas_pendentes: Int,
    val proxima_tarefa: ProximaTarefa?
)

data class ProximaTarefa(
    val nome: String,
    val data_entrega: String
)
