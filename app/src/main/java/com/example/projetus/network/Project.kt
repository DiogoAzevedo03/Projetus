package com.example.projetus.network

import java.io.Serializable

data class Project(
    val id: Int,
    val nome: String,
    val descricao: String,
    val data_inicio: String,
    val data_fim: String,
    val gestor_nome: String,
    val tempo_gasto: String,
    val progresso: Double,
    val estado: String,
    val total_tarefas: Int,
    val tempo_total_segundos: Int
) : Serializable

