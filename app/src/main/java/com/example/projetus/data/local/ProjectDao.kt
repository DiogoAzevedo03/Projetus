package com.example.projetus.data.local

import androidx.room.Dao
import androidx.room.Query
import com.example.projetus.data.model.Project

@Dao
interface ProjectDao {
    @Query("SELECT * FROM Projetos WHERE gestor_id = :userId AND estado = 'Ativo'")
    fun getActiveProjectsByGestorId(userId: Int): List<Project>
}
