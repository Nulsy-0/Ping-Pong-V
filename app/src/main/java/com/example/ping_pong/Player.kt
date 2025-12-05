package com.example.ping_pong

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class Player : AppCompatActivity() {


    val func = UserRepository(this)
    val intent = Intent(this@Player,game::class.java)
    var quant = 1
    val nome = findViewById<EditText>(R.id.nome)
    val btn1 = findViewById<Button>(R.id.btn1)
    val btn2 = findViewById<Button>(R.id.btn2)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.player_main)
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
    fun teste(){
        val nome1 = nome.text.toString()
        intent.putExtra("quantidade", quant)
        intent.putExtra("nome1", nome1)

        startActivity(intent)
    }
}