package com.example.projetus.network // Pacote da resposta de login

data class LoginResponse(
    val success: Boolean, // Indica se o login foi bem sucedido
    val message: String, // Mensagem retornada pela API
    val user: UserData? // Dados do utilizador autenticado
) // Estrutura da resposta de login

data class UserData(
    val id: Int, // Identificador do utilizador
    val nome: String, // Nome do utilizador
    val tipo_perfil: String // Perfil do utilizador
) // Dados do utilizador
