// com.example.projetus.data.local.PendingUser.kt
package com.example.projetus.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_users")
data class PendingUser(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,
    val username: String,
    val email: String,
    val password: String,
    val tipo_perfil: String,
    val foto: String?
)
