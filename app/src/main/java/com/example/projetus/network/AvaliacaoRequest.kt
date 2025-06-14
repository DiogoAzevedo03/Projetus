package com.example.projetus.network // Pacote do modelo

data class AvaliacaoRequest(
    val tarefa_id: Int, // ID da tarefa avaliada
    val utilizador_id: Int, // ID do utilizador que avalia
    val nota: Int, // Nota atribuída
    val comentario: String // Comentário da avaliação
) // Corpo de requisição para avaliar tarefa
