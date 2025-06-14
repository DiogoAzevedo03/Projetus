// com.example.projetus.data.local.PendingUserDao.kt
package com.example.projetus.data.local

import androidx.room.*
import com.example.projetus.data.local.PendingUser

@Dao
interface PendingUserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: PendingUser)

    @Query("SELECT * FROM pending_users")
    suspend fun getAll(): List<PendingUser>

    @Delete
    suspend fun delete(user: PendingUser)
}
