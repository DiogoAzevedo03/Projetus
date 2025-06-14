package com.example.projetus.network // Pacote do modelo de tarefa

import java.io.Serializable // Interface para permitir serialização

data class Task(
    val id: Int, // Identificador da tarefa
    val nome: String, // Nome da tarefa
    val descricao: String, // Descrição da tarefa
    val data_entrega: String, // Prazo de entrega
    val estado: String // Estado atual
) : Serializable // Permitido para intents

