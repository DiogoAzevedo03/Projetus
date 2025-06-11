package com.example.projetus.network

import java.io.Serializable

data class Task(
    val id: Int,
    val nome: String,
    val descricao: String,
    val data_entrega: String,
    val estado: String
) : Serializable

