package com.example.projetus.network

data class SuggestionRequest(
    val mensagem: String,
    val user_id: Int? = null
)
