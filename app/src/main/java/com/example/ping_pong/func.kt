package com.example.ping_pong

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(context: Context) {

    /*
    val repo = UserRepository(this)
    lifecycleScope.launch {
        repo.InUser("João")
    }
    */

    private val userDao = DatabaseProvider.getDatabase(context).userDao()

    // Criar user
    suspend fun InUser(nome: String) {
        withContext(Dispatchers.IO) {
            val existente = userDao.getUser()
            if (existente != null) {
                val novoUser = User(
                    nome = nome,
                    recorde1 = 0,
                    recorde2 = 0
                )
                userDao.insert(novoUser)
            }
        }
    }

    // Obter user
    suspend fun User(){
        return withContext(Dispatchers.IO) {
            userDao.getUser()
        }
    }

    // Update ao nome
    suspend fun UserNameUp(nomeN: String){
        return withContext(Dispatchers.IO) {
            userDao.updateNome(id = 1, nome = nomeN)
        }
    }

    // Update ao recorde1
    suspend fun UserR1Up(Rec: Int){
        return withContext(Dispatchers.IO) {
            userDao.updateRecorde1(id = 1, valor = Rec)
        }
    }

    // Update ao recorde2
    suspend fun UserR2Up(Rec: Int){
        return withContext(Dispatchers.IO) {
            userDao.updateRecorde1(id = 1, valor = Rec)
        }
    }
}