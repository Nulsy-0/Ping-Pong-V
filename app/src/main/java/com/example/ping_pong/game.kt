package com.example.ping_pong

import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class game : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val img = findViewById<ImageView>(R.id.img)
        val quant = intent.getStringExtra("quant")
        val nome1 = intent.getStringExtra("nome1")

        if(quant.equals("1")){
            img.setImageResource(R.drawable.fundop1)
        }else if(quant.equals("2")){
            img.setImageResource(R.drawable.fundop2)
        }
    }
}