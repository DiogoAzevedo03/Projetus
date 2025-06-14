package com.example.projetus.network // Pacote da resposta de projetos

data class ProjectResponse(
    val success: Boolean, // Indica se a operação foi bem sucedida
    val projetos: List<Project> // Lista de projetos recebida
) // Resposta contendo projetos
