data class AvaliacoesResponse(
    val success: Boolean,
    val avaliacoes: List<AvaliacaoItem>
)

data class AvaliacaoItem(
    val tarefa_id: Int,
    val tarefa_nome: String,
    val utilizador_nome: String,
    val utilizador_id: Int,
    val avaliacao_nota: Int?, // <- pode ser null
    val avaliacao_comentario: String? // <- pode ser null
)
