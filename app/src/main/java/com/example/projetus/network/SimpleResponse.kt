package com.example.projetus.network // Pacote da resposta simples

data class SimpleResponse(
    val success: Boolean, // Resultado da operação
    val message: String? // Mensagem opcional retornada
) // Estrutura utilizada em respostas básicas
