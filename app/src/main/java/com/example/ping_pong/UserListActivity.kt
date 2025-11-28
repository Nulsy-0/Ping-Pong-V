package com.example.ping_pong
/*
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class UserListActivity : AppCompatActivity() {

    private lateinit var repository: UserRepository
    private lateinit var textViewUsers: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        repository = UserRepository(this)
        textViewUsers = findViewById(R.id.textViewUsers)

        mostrarTodosOsUsers()
    }

    private fun mostrarTodosOsUsers() {
        lifecycleScope.launch {
            val lista = repository.listarUsers()

            if (lista.isEmpty()) {
                textViewUsers.text = "Não existem utilizadores registados."
            } else {
                val builder = StringBuilder()
                for (user in lista) {
                    builder.append("ID: ${user.id}\n")
                    builder.append("Nome: ${user.nome}\n")
                    builder.append("Recorde 1: ${user.recorde1}\n")
                    builder.append("Recorde 2: ${user.recorde2}\n")
                    builder.append("Recorde 4: ${user.recorde4}\n\n")
                }
                textViewUsers.text = builder.toString()
            }
        }
    }
}
*/