package com.example.projetus.network // Pacote do modelo de dashboard

data class DashboardResponse(
    val success: Boolean, // Indica sucesso da requisicao
    val projetos_ativos: Int, // Numero de projetos ativos
    val tarefas_pendentes: Int, // Total de tarefas pendentes
    val proxima_tarefa: ProximaTarefa? // Informacao da proxima tarefa
) // Modelo da resposta principal

data class ProximaTarefa(
    val nome: String, // Nome da tarefa
    val data_entrega: String // Data de entrega
) // Estrutura da proxima tarefa
