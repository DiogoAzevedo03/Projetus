package com.example.projetus.network // Pacote do modelo de projeto

import java.io.Serializable // Necessário para passar o objeto entre activities

data class Project(
    val id: Int, // Identificador do projeto
    val nome: String, // Nome do projeto
    val descricao: String, // Descrição do projeto
    val data_inicio: String, // Data de início
    val data_fim: String, // Data de conclusão
    val gestor_nome: String, // Nome do gestor responsável
    val tempo_gasto: String, // Tempo já gasto no projeto
    val progresso: Double, // Percentual de progresso
    val estado: String, // Estado atual do projeto
    val total_tarefas: Int, // Número total de tarefas
    val tempo_total_segundos: Int // Tempo total previsto em segundos
) : Serializable // Permite serialização do objeto

