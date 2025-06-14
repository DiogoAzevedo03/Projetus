package com.example.projetus.network // Pacote do modelo de utilizador

import java.io.Serializable // Necessário para passar objetos entre componentes

data class Utilizador(
    val id: Int, // ID do utilizador
    val nome: String, // Nome completo
    val username: String, // Nome de usuário
    val email: String, // Email de contato
    val password: String, // Senha de acesso
    val tipo_perfil: String, // Tipo de perfil
    val foto: String = "" // Foto do utilizador
) : Serializable // Permite serialização

