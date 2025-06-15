package com.example.projetus.network

data class HelpRequest(
    val mensagem: String,
    val user_id: Int? = null
)