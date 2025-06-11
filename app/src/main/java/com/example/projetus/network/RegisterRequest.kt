package com.example.projetus.network

data class RegisterRequest(
    val nome: String,
    val email: String,
    val password: String,
    val tipo_perfil: String = "utilizador"
)
