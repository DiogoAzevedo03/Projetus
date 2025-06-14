package com.example.projetus.network // Pacote onde o modelo está localizado

data class AssociarRequest(
    val projeto_id: Int, // ID do projeto a associar
    val utilizador_id: Int // ID do utilizador a ser associado
) // Representa o corpo da requisição de associação
