package com.example.ping_pong

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.net.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : AppCompatActivity() {

    // porta UDP usada por todas as instâncias
    private val UDP_PORT = 8888
    // mensagem de discovery
    private val DISCOVER_MSG = "UDP_PEER_DISCOVER"
    private val DISCOVER_REPLY_PREFIX = "UDP_PEER_HERE:"
    // intervalo de rediscovery (ms)
    private val DISCOVER_INTERVAL_MS = 4000L

    private lateinit var tvStatus: TextView
    private lateinit var tvPeers: TextView
    private lateinit var tvReceived: TextView
    private lateinit var etVariable: EditText
    private lateinit var btnSend: Button

    // thread pool
    private val exec = Executors.newCachedThreadPool()
    private var running = AtomicBoolean(false)

    // peers conhecidos: ip -> lastSeenMillis
    private val peers = ConcurrentHashMap<String, Long>()

    // socket de recepção (reutilizado)
    private var receiveSocket: DatagramSocket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvStatus = findViewById(R.id.tvStatus)
        tvPeers = findViewById(R.id.tvPeers)
        tvReceived = findViewById(R.id.tvReceived)
        etVariable = findViewById(R.id.etVariable)
        btnSend = findViewById(R.id.btnSend)

        btnSend.setOnClickListener {
            val value = etVariable.text.toString()
            if (value.isNotEmpty()) {
                sendToAllPeers(value)
            }
        }

        startNetworking()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopNetworking()
        exec.shutdownNow()
    }

    private fun startNetworking() {
        running.set(true)
        tvStatus.text = "Status: iniciando sockets..."
        exec.execute { startReceiver() }
        exec.execute { discoveryLoop() }
        // limpeza periódica de peers inativos
        exec.execute { peerCleanupLoop() }
    }

    private fun stopNetworking() {
        running.set(false)
        receiveSocket?.close()
    }

    // Receiver: escuta mensagens UDP na porta definida
    private fun startReceiver() {
        try {
            receiveSocket = DatagramSocket(UDP_PORT)
            receiveSocket?.soTimeout = 2000
            val buf = ByteArray(1024)
            tvStatus.post { tvStatus.text = "Status: a escutar UDP na porta $UDP_PORT" }
            while (running.get()) {
                try {
                    val packet = DatagramPacket(buf, buf.size)
                    receiveSocket?.receive(packet)
                    val msg = String(packet.data, 0, packet.length, Charsets.UTF_8)
                    val senderIp = packet.address.hostAddress
                    handleIncomingMessage(msg, senderIp)
                } catch (t: SocketTimeoutException) {
                    // sem dados — continua
                } catch (t: Exception) {
                    // socket fechado possivelmente
                    break
                }
            }
        } catch (e: Exception) {
            tvStatus.post { tvStatus.text = "Erro receiver: ${e.localizedMessage}" }
        } finally {
            receiveSocket?.close()
        }
    }

    // Gerir mensagens recebidas
    private fun handleIncomingMessage(msg: String, ip: String) {
        when {
            msg == DISCOVER_MSG -> {
                // responder com uma mensagem indicando que existe (unicast reply direto ao sender)
                sendUnicast("$DISCOVER_REPLY_PREFIX${getLocalAddress() ?: "unknown"}", ip)
            }
            msg.startsWith(DISCOVER_REPLY_PREFIX) -> {
                // novo peer encontrado
                peers[ip] = System.currentTimeMillis()
                updatePeersView()
            }
            msg.startsWith("DATA:") -> {
                val payload = msg.removePrefix("DATA:")
                peers[ip] = System.currentTimeMillis()
                tvReceived.post { tvReceived.text = "Último recebido de $ip: $payload" }
                updatePeersView()
            }
            else -> {
                // mensagens desconhecidas - opcional
            }
        }
    }

    // envia a variável (payload) a todos os peers conhecidos; se não houver peers, usa broadcast
    private fun sendToAllPeers(value: String) {
        exec.execute {
            val msg = "DATA:$value"
            val peerList = peers.keys.toList()
            if (peerList.isEmpty()) {
                // broadcast se não houver peers conhecidos
                broadcast(msg)
            } else {
                peerList.forEach { ip ->
                    sendUnicast(msg, ip)
                }
            }
        }
    }

    // envia msg UDP unicast para ip:UDP_PORT
    private fun sendUnicast(message: String, destIp: String) {
        try {
            val socket = DatagramSocket()
            socket.broadcast = false
            val data = message.toByteArray(Charsets.UTF_8)
            val addr = InetAddress.getByName(destIp)
            val packet = DatagramPacket(data, data.size, addr, UDP_PORT)
            socket.send(packet)
            socket.close()
        } catch (e: Exception) {
            // falha no envio
        }
    }

    // envia broadcast para a rede local
    private fun broadcast(message: String) {
        try {
            val socket = DatagramSocket()
            socket.broadcast = true
            val data = message.toByteArray(Charsets.UTF_8)
            // tenta o broadcast universal e 255.255.255.255
            val addrs = listOf(
                InetAddress.getByName("255.255.255.255")
            ) + getBroadcastAddresses()
            for (addr in addrs) {
                try {
                    val packet = DatagramPacket(data, data.size, addr, UDP_PORT)
                    socket.send(packet)
                } catch (_: Exception) { /* ignora */ }
            }
            socket.close()
        } catch (_: Exception) { /* ignora */ }
    }

    // loop que periodicamente envia discovery broadcast
    private fun discoveryLoop() {
        while (running.get()) {
            try {
                broadcast(DISCOVER_MSG)
                Thread.sleep(DISCOVER_INTERVAL_MS)
            } catch (_: InterruptedException) { break }
        }
    }

    // remove peers antigos
    private fun peerCleanupLoop() {
        val TIMEOUT = 15000L
        while (running.get()) {
            val now = System.currentTimeMillis()
            val toRemove = peers.filter { now - it.value > TIMEOUT }.keys
            toRemove.forEach { peers.remove(it) }
            updatePeersView()
            try { Thread.sleep(3000) } catch (_: InterruptedException) { break }
        }
    }

    // atualiza contador de peers na UI
    private fun updatePeersView() {
        tvPeers.post { tvPeers.text = "Peers encontrados: ${peers.size}" }
    }

    // Tenta obter broadcasts das interfaces (endereços de broadcast locais)
    private fun getBroadcastAddresses(): List<InetAddress> {
        val result = mutableListOf<InetAddress>()
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            for (intf in interfaces) {
                if (!intf.isUp || intf.isLoopback) continue
                for (ifaceAddr in intf.interfaceAddresses) {
                    val broadcast = ifaceAddr.broadcast
                    if (broadcast != null) result.add(broadcast)
                }
            }
        } catch (_: Exception) { /* ignora */ }
        return result
    }

    // tenta obter o endereço IP local (não garantido em todas as redes)
    private fun getLocalAddress(): String? {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val addrs = intf.inetAddresses
                while (addrs.hasMoreElements()) {
                    val addr = addrs.nextElement()
                    if (!addr.isLoopbackAddress && addr is Inet4Address) {
                        return addr.hostAddress
                    }
                }
            }
        } catch (_: Exception) {}
        return null
    }
}
