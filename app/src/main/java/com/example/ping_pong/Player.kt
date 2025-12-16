package com.example.ping_pong

import android.content.Intent
import android.os.Bundle
import android.text.*
import android.util.Log
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class Player : AppCompatActivity() {

    private val func by lazy { UserRepository(this) }
    var quant: String = "1"
    private lateinit var nome: EditText
    private lateinit var btn1: Button
    private lateinit var btn2: Button
    private lateinit var R1: TextView
    private lateinit var R2: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.player_main)

        // Inicializar views
        nome = findViewById(R.id.nome)
        btn1 = findViewById(R.id.btn1)
        btn2 = findViewById(R.id.btn2)
        R1 = findViewById(R.id.R1)
        R2 = findViewById(R.id.R2)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lifecycleScope.launch {
            func.InUser(nome.text.toString())
        }

        nome.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // antes de alterar o texto
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // dorante da alteração do texto
            }

            override fun afterTextChanged(s: Editable?) {
                // depois da alteração do texto
                val textoAtual = s.toString()
                lifecycleScope.launch {
                    func.UserNameUp(textoAtual)
                }
            }
        })

        lifecycleScope.launch {
            val tabela = func.User()
            val rec1 = tabela?.recorde1.toString()
            val rec2 = tabela?.recorde2.toString()
            R1.text = "$rec1 Pts"
            R2.text = "$rec2 Pts"
        }

        btn1.setOnClickListener {
            quant = "1"
            teste()
        }

        btn2.setOnClickListener {
            quant = "2"
            teste()
        }
    }

    private fun teste() {
        val nome1 = nome.text.toString()

        // Criar intent aqui evita o conflito
        val jogoIntent = Intent(this, game::class.java)
        jogoIntent.putExtra("quantidade", quant)

        startActivity(jogoIntent)
    }
}
