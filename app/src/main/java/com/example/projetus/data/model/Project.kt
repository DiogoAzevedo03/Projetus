package com.example.projetus.data.model // Pacote das entidades de dados

import androidx.room.Entity // Indica que é uma entidade Room
import androidx.room.PrimaryKey // Chave primária da entidade

@Entity(tableName = "Projetos") // Tabela de projetos
data class Project(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Identificador gerado automaticamente
    val nome: String, // Nome do projeto
    val descricao: String, // Descrição do projeto
    val gestor_id: Int, // Identificador do gestor
    val data_inicio: String, // Data de início
    val data_fim: String, // Data de fim
    val estado: String // Estado atual
)
