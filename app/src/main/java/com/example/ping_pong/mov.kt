package com.example.ping_pong

import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var acelerometro: Sensor? = null

    private lateinit var velocidadeTv: TextView
    private lateinit var maxTv: TextView
    private lateinit var back: LinearLayout

    private var maxValor: Double = 0.0
    private var zAnterior: Float = 0f   // para comparar a rotação

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializa views
        back = findViewById(R.id.meuLayout)
        velocidadeTv = findViewById(R.id.velocidade)
        maxTv = findViewById(R.id.top)

        // Sensor
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onResume() {
        super.onResume()
        acelerometro?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return

        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            // Calcular magnitude total
            val aceleracao = sqrt((x * x + y * y + z * z).toDouble())

            // Atualizar máximo
            if (aceleracao > maxValor) {
                maxValor = aceleracao
                maxTv.text = "Máx: %.2f m/s²".format(maxValor)
            }

            // Atualizar valor atual
            velocidadeTv.text = "Atual: %.2f m/s²".format(aceleracao)

            // DETETAR movimento: parte de trás → parte da frente
            if (zAnterior > 0 && z < 0) {
                // Mudou de trás para frente → fica vermelho
                back.setBackgroundColor(Color.RED)
            } else if (zAnterior < 0 && z > 0) {
                // Opcional: voltou de frente para trás → fica verde
                back.setBackgroundColor(Color.GREEN)
            }

            // Atualiza zAnterior para próxima leitura
            zAnterior = z
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Não usado
    }
}