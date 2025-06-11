package com.example.projetus.network

data class UpdateTaskRequest(
    val tarefa_id: Int,
    val utilizador_id: Int,
    val tempo_gasto: String,
    val observacoes: String,
    val taxa_conclusao: Int,
    val foto: String
)
