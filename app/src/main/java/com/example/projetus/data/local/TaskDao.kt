package com.example.projetus.data.local // Pacote do DAO de tarefas

import androidx.room.Dao // Anotação DAO
import androidx.room.Query // Permite executar consultas
import com.example.projetus.data.model.Task // Entidade Tarefa

@Dao // Interface DAO das tarefas
interface TaskDao {

    @Query("""
        SELECT * FROM Tarefas
        WHERE projeto_id IN (SELECT id FROM Projetos WHERE gestor_id = :userId)
        AND estado = 'Pendente'
    """) // Lista tarefas pendentes do gestor
    fun getPendingTasksByUserId(userId: Int): List<Task>

    @Query("""
        SELECT * FROM Tarefas
        WHERE projeto_id IN (SELECT id FROM Projetos WHERE gestor_id = :userId)
        AND estado = 'Pendente'
        ORDER BY data_entrega ASC
        LIMIT 1
    """) // Tarefa pendente com data de entrega mais próxima
    fun getNextTaskByUserId(userId: Int): Task?
}
