package com.example.projetus

data class ProjectCard(
    val nome: String,
    val gestor: String,
    val estado: String,
    val tarefas: Int,
    val podeEditar: Boolean
)
