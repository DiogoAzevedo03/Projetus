package com.example.projetus // Pacote da aplicação

data class ProjectCard(
    val nome: String,      // Nome do projeto
    val gestor: String,    // Nome do gestor
    val estado: String,    // Estado atual do projeto
    val tarefas: Int,      // Número de tarefas
    val podeEditar: Boolean // Indica se pode ser editado
)
