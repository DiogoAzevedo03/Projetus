package com.example.projetus.network // Pacote da resposta de utilizadores

data class UtilizadoresResponse(
    val success: Boolean, // Resultado da operação
    val utilizadores: List<Utilizador> // Lista de utilizadores recebida
) // Resposta com os utilizadores
