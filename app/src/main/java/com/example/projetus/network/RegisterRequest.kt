package com.example.projetus.network // Pacote do request de registro

data class RegisterRequest(
    val nome: String, // Nome do novo utilizador
    val email: String, // Email do utilizador
    val password: String, // Password escolhida
    val tipo_perfil: String = "utilizador" // Perfil padrão
) // Estrutura enviada para registrar
