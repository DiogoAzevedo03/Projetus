package com.example.projetus.network // Pacote do modelo de detalhes de tarefa

import java.io.Serializable // Permite serialização do objeto

data class TarefaDetalhes(
    val id: Int, // Identificador da tarefa
    val nome: String, // Nome da tarefa
    val descricao: String, // Descrição detalhada
    val data_entrega: String, // Data de entrega
    val tempo_gasto: String, // Tempo gasto na tarefa
    val taxa_conclusao: Int, // Percentual concluído
    val observacoes: String, // Observações da tarefa
    val foto: String // Foto associada
) : Serializable // Permite passagem entre componentes
