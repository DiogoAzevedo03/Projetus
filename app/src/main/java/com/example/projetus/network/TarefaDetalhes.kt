package com.example.projetus.network

import java.io.Serializable

data class TarefaDetalhes(
    val id: Int,
    val nome: String,
    val descricao: String,
    val data_entrega: String,
    val tempo_gasto: String,
    val taxa_conclusao: Int,
    val observacoes: String,
    val foto: String
) : Serializable
