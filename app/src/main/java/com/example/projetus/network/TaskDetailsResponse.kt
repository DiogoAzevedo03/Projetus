package com.example.projetus.network // Pacote da resposta de detalhes de tarefa

data class TaskDetailsResponse(
    val success: Boolean, // Resultado da operação
    val tarefa: Map<String, Any> // Informações detalhadas da tarefa
) // Resposta contendo detalhes de uma tarefa
