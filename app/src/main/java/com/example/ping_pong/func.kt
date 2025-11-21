package com.example.ping_pong

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(context: Context) {

    private val userDao = DatabaseProvider.getDatabase(context).userDao()

    // Inserir novo utilizador
    suspend fun inserirUser(nome: String, recorde1: Int, recorde2: Int, recorde4: Int) {
        withContext(Dispatchers.IO) {
            val novoUser = User(
                nome = nome,
                recorde1 = recorde1,
                recorde2 = recorde2,
                recorde4 = recorde4
            )
            userDao.insert(novoUser)
        }
    }

    // Atualizar dados de um utilizador
    suspend fun atualizarUser(user: User) {
        withContext(Dispatchers.IO) {
            userDao.update(user)
        }
    }

    // Obter todos os utilizadores
    suspend fun listarUsers(): List<User> {
        return withContext(Dispatchers.IO) {
            userDao.getAll()
        }
    }

    // Obter um utilizador pelo nome
    suspend fun obterUserPorNome(nome: String): User? {
        return withContext(Dispatchers.IO) {
            userDao.getByName(nome)
        }
    }
}