package com.example.ping_pong

import android.content.Context
import android.os.Bundle
import android.widget.*
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.DialogInterface
import android.text.InputType

class game : AppCompatActivity() {

    private val udp = wifi(8888)
    private lateinit var dialogEscolha: AlertDialog

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
        val recAtual = findViewById<TextView>(R.id.recAtual)

        val quant = intent.getStringExtra("quantidade")

        // Coisas para o wifi
        val ip = udp.getLocalIpAddress()
        val n = mutableMapOf(
            "ip" to ip,
            "n" to 0,
            "p" to 0,
            "chave" to null
        )

        // iniciar receiver
        udp.iniciarReceiver { oip, msg ->
            runOnUiThread {

                val resc = msg

                if(n["chave"] == resc["chave"]){
                    if(resc["ip"] != ip && resc["p"] == 1 && n["p"] == 0){
                        dialogEscolha.dismiss()
                        udp.enviarBroadcast(n)
                        img.setImageResource(R.drawable.fundop2)
                        n["p"] = 2
                    }else if(resc["ip"] != ip && resc["p"] == 2 && n["p"] == 0){
                        dialogEscolha.dismiss()
                        udp.enviarBroadcast(n)
                        img.setImageResource(R.drawable.fundop1)
                        n["p"] = 1
                    }

                    // receção para o jogo

                }
            }
        }

        if(quant.equals("1")){
            img.setImageResource(R.drawable.fundop1)
        }else if(quant.equals("2")){
            popupChave(this) { chave ->
                n["chave"] = chave
                popupEscolha(this) { player ->
                    if (player == 1) {
                        img.setImageResource(R.drawable.fundop1)
                        n["p"] = 1
                        udp.enviarBroadcast(n)
                    } else {
                        img.setImageResource(R.drawable.fundop2)
                        n["p"] = 2
                        udp.enviarBroadcast(n)
                    }
                }
            }
        }

    }
    fun popupEscolha(context: Context, callback: (Int) -> Unit) {
        val opcoes = arrayOf("Player 1", "Player 2")

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Escolhe o teu player")
        builder.setCancelable(false)
        builder.setItems(opcoes) { dialog, which ->
            callback(which + 1)
            dialog.dismiss()
        }

        dialogEscolha = builder.create()
        dialogEscolha.show()
    }
    fun popupChave(context: Context, callback: (Int) -> Unit) {
        val editText = EditText(context)
        editText.inputType = InputType.TYPE_CLASS_NUMBER  // apenas números

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Insere um número.")
        builder.setMessage("⚠️ Aviso o outro jogador tem de escrever o mesmo número!")
        builder.setView(editText)
        builder.setCancelable(false)

        builder.setPositiveButton("OK") { dialog, _ ->
            val valor = editText.text.toString().toIntOrNull()
            if (valor != null) {
                callback(valor)
            }
            dialog.dismiss()
        }

        dialogEscolha = builder.create()
        dialogEscolha.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        udp.pararReceiver()
    }
}