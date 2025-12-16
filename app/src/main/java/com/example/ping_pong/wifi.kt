package com.example.ping_pong

import android.util.Log
import org.json.JSONObject
import java.net.*
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

class wifi(private val port: Int = 8888) {

    private val exec = Executors.newCachedThreadPool()
    private var running = AtomicBoolean(false)
    private var socketReceiver: DatagramSocket? = null

    // Envia um Map (array) em broadcast convertendo para JSON
    fun enviarBroadcast(mensagem: Map<String, Any?>) {
        exec.execute {
            try {
                val socket = DatagramSocket()
                socket.broadcast = true

                // Converter MAP para JSON
                val json = JSONObject(mensagem).toString()
                val data = json.toByteArray()

                val addrs = mutableSetOf<InetAddress>()
                addrs.add(InetAddress.getByName("255.255.255.255"))

                val interfaces = NetworkInterface.getNetworkInterfaces()
                for (intf in interfaces) {
                    if (!intf.isUp || intf.isLoopback) continue
                    for (ifaceAddr in intf.interfaceAddresses) {
                        ifaceAddr.broadcast?.let { addrs.add(it) }
                    }
                }

                for (addr in addrs) {
                    try {
                        val packet = DatagramPacket(data, data.size, addr, port)
                        socket.send(packet)
                    } catch (_: Exception) {}
                }

                socket.close()
            } catch (e: Exception) {
                Log.e("UDP", "Erro ao enviar broadcast: ${e.localizedMessage}")
            }
        }
    }

    // Receiver que devolve Map em vez de String
    fun iniciarReceiver(callback: (String, Map<String, Any?>) -> Unit) {
        exec.execute {
            try {
                socketReceiver = DatagramSocket(port)
                socketReceiver?.soTimeout = 1000
                running.set(true)

                val buffer = ByteArray(2048)

                while (running.get()) {
                    try {
                        val packet = DatagramPacket(buffer, buffer.size)
                        socketReceiver?.receive(packet)

                        val msg = String(packet.data, 0, packet.length)
                        val ip = packet.address.hostAddress

                        // Converter JSON → Map
                        val json = JSONObject(msg)
                        val map = json.toMap()

                        callback(ip, map)

                    } catch (_: SocketTimeoutException) {}
                    catch (e: Exception) { break }
                }

            } catch (e: Exception) {
                Log.e("UDP", "Erro no receiver: ${e.localizedMessage}")
            }
        }
    }

    // Converter JSONObject para Map<String, Any?>
    private fun JSONObject.toMap(): Map<String, Any?> {
        val result = mutableMapOf<String, Any?>()
        val keys = this.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            result[key] = this.get(key)
        }
        return result
    }

    fun getLocalIpAddress(): String? {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val intf = interfaces.nextElement()
                if (!intf.isUp || intf.isLoopback) continue

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

    fun pararReceiver() {
        running.set(false)
        socketReceiver?.close()
    }
}