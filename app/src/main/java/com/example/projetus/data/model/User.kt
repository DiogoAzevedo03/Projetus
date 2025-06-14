package com.example.projetus.data.model // Pacote das entidades de dados

import androidx.room.Entity // Anotação da entidade Room
import androidx.room.PrimaryKey // Identificador único

@Entity(tableName = "utilizadores") // Tabela de utilizadores
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Identificador gerado automaticamente
    val nome: String, // Nome do utilizador
    val username: String, // Nome de utilizador
    val email: String, // Endereço de e-mail
    val password: String, // Palavra-passe
    val tipo_perfil: String, // Tipo de perfil
    val estado: String, // Estado do utilizador
    val foto: String = "" // URL ou caminho da foto
)
