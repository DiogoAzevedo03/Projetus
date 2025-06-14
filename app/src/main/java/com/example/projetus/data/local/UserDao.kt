package com.example.projetus.data.local // Pacote do DAO de utilizadores

import androidx.room.Dao // Marca esta interface como um DAO do Room
import androidx.room.Insert // Anotação para inserções
import androidx.room.OnConflictStrategy // Estratégia de conflito de inserção
import androidx.room.Query // Anotação para consultas
import com.example.projetus.data.model.User // Entidade Utilizador

@Dao // Interface de acesso a dados dos utilizadores
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE) // Insere ou substitui um utilizador
    suspend fun insert(user: User)

    @Query("SELECT * FROM utilizadores WHERE username = :username AND password = :password LIMIT 1") // Autentica o utilizador
    suspend fun login(username: String, password: String): User?

    @Query("SELECT * FROM utilizadores WHERE username = :username LIMIT 1") // Obtém por username
    suspend fun getByUsername(username: String): User?

    @Query("SELECT * FROM Utilizadores WHERE id = :userId LIMIT 1") // Obtém por id
    fun getById(userId: Int): User?

    @Query("SELECT * FROM Utilizadores WHERE email = :email") // Procura por e-mail
    suspend fun getByEmail(email: String): User?

}
