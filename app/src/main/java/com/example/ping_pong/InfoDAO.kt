package com.example.ping_pong

import androidx.room.*

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User): Long

    // Obter o único user (ou null se ainda não existir)
    @Query("SELECT * FROM User LIMIT 1")
    suspend fun getUser(): User?

    // Atualizar nome
    @Query("UPDATE User SET nome = :nome WHERE id = :id")
    suspend fun updateNome(id: Int, nome: String)

    // Atualizar recorde1
    @Query("UPDATE User SET recorde1 = :valor WHERE id = :id")
    suspend fun updateRecorde1(id: Int, valor: Int)

    // Atualizar recorde2
    @Query("UPDATE User SET recorde2 = :valor WHERE id = :id")
    suspend fun updateRecorde2(id: Int, valor: Int)
}