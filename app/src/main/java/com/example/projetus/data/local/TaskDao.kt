package com.example.projetus.data.local

import androidx.room.Dao
import androidx.room.Query
import com.example.projetus.data.model.Task

@Dao
interface TaskDao {

    @Query("""
        SELECT * FROM Tarefas 
        WHERE projeto_id IN (SELECT id FROM Projetos WHERE gestor_id = :userId) 
        AND estado = 'Pendente'
    """)
    fun getPendingTasksByUserId(userId: Int): List<Task>

    @Query("""
        SELECT * FROM Tarefas 
        WHERE projeto_id IN (SELECT id FROM Projetos WHERE gestor_id = :userId) 
        AND estado = 'Pendente' 
        ORDER BY data_entrega ASC 
        LIMIT 1
    """)
    fun getNextTaskByUserId(userId: Int): Task?
}
