// com.example.projetus.data.local.PendingUser.kt
package com.example.projetus.data.local // Pacote das entidades locais

import androidx.room.Entity // Anotação de entidade Room
import androidx.room.PrimaryKey // Chave primária da entidade

@Entity(tableName = "pending_users") // Tabela de utilizadores pendentes
data class PendingUser(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Identificador gerado automaticamente
    val nome: String, // Nome do utilizador
    val username: String, // Nome de utilizador
    val email: String, // Endereço de e-mail
    val password: String, // Palavra-passe
    val tipo_perfil: String, // Tipo de perfil
    val foto: String? // URL ou caminho da foto
)
