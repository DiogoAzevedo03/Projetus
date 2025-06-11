package com.example.projetus.network

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val user: UserData?
)

data class UserData(
    val id: Int,
    val nome: String,
    val tipo_perfil: String
)
