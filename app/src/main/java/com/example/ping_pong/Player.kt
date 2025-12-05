package com.example.ping_pong

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class Player : AppCompatActivity() {

    private val func by lazy { UserRepository(this) }
    var quant = 1
    private lateinit var nome: EditText
    private lateinit var btn1: Button
    private lateinit var btn2: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.player_main)

        // Inicializar views
        nome = findViewById(R.id.nome)
        btn1 = findViewById(R.id.btn1)
        btn2 = findViewById(R.id.btn2)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lifecycleScope.launch {
            func.InUser(nome.text.toString())
        }

        lifecycleScope.launch {
            val tabela = func.User()
            Log.d("teste", tabela.toString())
            println(tabela)
        }

        btn1.setOnClickListener {
            quant = 1
            teste()
        }

        btn2.setOnClickListener {
            quant = 2
            teste()
        }
    }

    private fun teste() {
        val nome1 = nome.text.toString()

        // Criar intent aqui evita o conflito
        val jogoIntent = Intent(this, game::class.java)
        jogoIntent.putExtra("quantidade", quant)
        jogoIntent.putExtra("nome1", nome1)

        startActivity(jogoIntent)
    }
}
