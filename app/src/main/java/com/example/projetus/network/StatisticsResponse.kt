package com.example.projetus.network // Pacote das estatísticas gerais


data class StatisticsResponse(
    val success: Boolean, // Resultado da operação
    val nome: String, // Nome do utilizador
    val tarefas_atribuidas: Int, // Número de tarefas atribuídas
    val tarefas_concluidas: Int, // Tarefas concluídas
    val taxa_conclusao: Int, // Percentual de conclusão
    val projetos_concluidos: Int, // Projetos concluídos

) // Resposta com estatísticas gerais
