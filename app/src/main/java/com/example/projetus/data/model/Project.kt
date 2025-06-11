package com.example.projetus.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Projetos")
data class Project(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,
    val descricao: String,
    val gestor_id: Int,
    val data_inicio: String,
    val data_fim: String,
    val estado: String
)
