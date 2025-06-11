package com.example.projetus.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "utilizadores")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,
    val username: String,
    val email: String,
    val password: String,
    val tipo_perfil: String,
    val estado: String
)
