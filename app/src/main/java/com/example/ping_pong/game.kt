package com.example.ping_pong

import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.text.InputType
import android.util.Log
import android.view.View
import com.example.ping_pong.SoundPlayer.playPop
import com.example.ping_pong.SoundPlayer.playPop2
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private var n = mutableMapOf(
    "n" to 0,
    "p" to 0,
    "chave" to null
)
private val udp = wifi(8888)
private var ContextSound: Any? = null
private var speed: Double = 0.0
private var recAtual2: TextView? = null

class game : AppCompatActivity() {
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
        val tudo = findViewById<View>(R.id.main)
        recAtual2 = recAtual
        recAtual.text = "0"
        val quant = intent.getStringExtra("quantidade")
        // Coisas para o wifi
        val ip = udp.getLocalIpAddress()
        var teste: Long = 0
        ContextSound = this

        // iniciar receiver
        udp.iniciarReceiver { oip, msg ->
            runOnUiThread {
                val resc = msg

                if(n["chave"] == resc["chave"]){
                    if(oip != ip && resc["p"] == 1 && n["p"] == 0){
                        dialogEscolha.dismiss()
                        udp.enviarBroadcast(n)
                        img.setImageResource(R.drawable.fundop2)
                        n["p"] = 2
                    }else if(oip != ip && resc["p"] == 2 && n["p"] == 0){
                        dialogEscolha.dismiss()
                        udp.enviarBroadcast(n)
                        img.setImageResource(R.drawable.fundop1)
                        n["p"] = 1
                    }
                    if(((resc["n"] as Int).toLong() < 0) && oip != ip){
                        // receção para o jogo
                        teste = (resc["n"] as Int).toLong()
                        teste *= -1
                        teste -= (teste - 1)
                        game(teste, recAtual)
                    }
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

        tudo.setOnClickListener {
            if (quant.equals("2") && (n["p"] == 1) && (n["n"] == 0)){
                retorno(-5.0)
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

    fun game(teste: Long, recAtual: TextView){
        lifecycleScope.launch{
            val pausa = teste / 2
            delay(pausa)
            playPop(ContextSound as Context)
        }
    }
}

fun retorno(passe: Double){
    n["n"] = passe.toInt()
    mov.callback = object : AceleracaoCallback {
        override fun onAceleracao(valor: Double) {
            // Aqui recebes o valor da aceleração
            println("Aceleração recebida: $valor")
            speed = valor
        }
    }
    playPop2(ContextSound as Context)
    recAtual2?.text = (recAtual2?.text.toString().toInt() + 1).toString()
    udp.enviarBroadcast(n)
}