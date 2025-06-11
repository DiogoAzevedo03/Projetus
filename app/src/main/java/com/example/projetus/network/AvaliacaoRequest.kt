package com.example.projetus.network

data class AvaliacaoRequest(
    val tarefa_id: Int,
    val utilizador_id: Int,
    val nota: Int,
    val comentario: String
)
