// com.example.projetus.data.local.PendingUserDao.kt
package com.example.projetus.data.local // Pacote do DAO de utilizadores pendentes

import androidx.room.* // Importa anotações do Room
import com.example.projetus.data.local.PendingUser // Entidade de utilizador pendente

@Dao // Indica que esta interface é um DAO do Room
interface PendingUserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Insere ou substitui um utilizador pendente
    suspend fun insert(user: PendingUser)

    @Query("SELECT * FROM pending_users") // Obtém todos os utilizadores pendentes
    suspend fun getAll(): List<PendingUser>

    @Delete // Remove um utilizador pendente
    suspend fun delete(user: PendingUser)
}
