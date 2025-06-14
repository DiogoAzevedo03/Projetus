package com.example.projetus.network // Pacote onde está definida a resposta genérica

data class GenericResponse(
    val success: Boolean, // Indica sucesso ou falha da operação
    val message: String // Mensagem retornada pela API
) // Estrutura de resposta simples
