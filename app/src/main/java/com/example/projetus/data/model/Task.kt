package com.example.projetus.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Tarefas")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,
    val descricao: String,
    val projeto_id: Int,
    val data_entrega: String, // <-- O nome correto
    val estado: String
)
