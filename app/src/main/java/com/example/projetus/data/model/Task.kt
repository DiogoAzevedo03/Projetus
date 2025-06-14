package com.example.projetus.data.model // Pacote das entidades de dados

import androidx.room.Entity // Anotação de entidade Room
import androidx.room.PrimaryKey // Chave primária

@Entity(tableName = "Tarefas") // Tabela de tarefas
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Identificador gerado automaticamente
    val nome: String, // Nome da tarefa
    val descricao: String, // Descrição da tarefa
    val projeto_id: Int, // Identificador do projeto associado
    val data_entrega: String, // Data de entrega
    val estado: String // Estado atual
)
