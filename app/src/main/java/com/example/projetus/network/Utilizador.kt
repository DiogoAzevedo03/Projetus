package com.example.projetus.network

import java.io.Serializable

data class Utilizador(
    val id: Int,
    val nome: String,
    val username: String,
    val email: String,
    val password: String,
    val tipo_perfil: String
) : Serializable

