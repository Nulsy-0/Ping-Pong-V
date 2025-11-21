package com.example.ping_pong

import androidx.room.*

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Insert
    fun insert(user: User)

    @Update
    fun update(user: User)

    @Delete
    fun delete(user: User)

    // Extra: obter um utilizador pelo nome
    @Query("SELECT * FROM user WHERE nome = :nome LIMIT 1")
    fun getByName(nome: String): User?
}