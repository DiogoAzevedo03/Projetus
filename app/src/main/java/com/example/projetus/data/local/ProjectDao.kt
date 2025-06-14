package com.example.projetus.data.local // Pacote do DAO de projetos

import androidx.room.Dao // Anotação DAO
import androidx.room.Query // Permite consultas SQL
import com.example.projetus.data.model.Project // Entidade Projeto

@Dao // Interface DAO dos projetos
interface ProjectDao {
    @Query("SELECT * FROM Projetos WHERE gestor_id = :userId AND estado = 'Ativo'") // Projetos ativos do gestor
    fun getActiveProjectsByGestorId(userId: Int): List<Project>
}
