package com.example.projetus.network // Pacote de resposta dos gestores

data class GestoresResponse(
    val success: Boolean, // Resultado da operação
    val gestores: List<Gestor> // Lista de gestores retornada
) // Resposta da API contendo gestores
