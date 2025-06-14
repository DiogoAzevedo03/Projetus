data class AvaliacoesResponse(
    val success: Boolean, // Indica se a operacao foi bem sucedida
    val avaliacoes: List<AvaliacaoItem> // Lista de avaliacoes retornada
) // Resposta com as avaliacoes

data class AvaliacaoItem(
    val tarefa_id: Int, // ID da tarefa
    val tarefa_nome: String, // Nome da tarefa
    val utilizador_nome: String, // Nome do utilizador
    val utilizador_id: Int, // ID do utilizador
    val avaliacao_nota: Int?, // Nota da avaliacao (pode ser null)
    val avaliacao_comentario: String? // Comentario (pode ser null)
) // Item individual de avaliacao
