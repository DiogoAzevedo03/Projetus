package com.example.projetus.network // Pacote do request de login

data class LoginRequest(
    val email: String, // Email do utilizador
    val password: String // Password do utilizador
) // Estrutura enviada no login
