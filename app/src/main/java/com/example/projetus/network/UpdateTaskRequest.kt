package com.example.projetus.network // Pacote do request de atualização de tarefa

data class UpdateTaskRequest(
    val tarefa_id: Int, // ID da tarefa
    val utilizador_id: Int, // ID do utilizador
    val tempo_gasto: String, // Tempo gasto
    val observacoes: String, // Observações adicionais
    val taxa_conclusao: Int, // Percentual de conclusão
    val foto: String // Foto atual da tarefa
) // Estrutura para atualizar tarefa
