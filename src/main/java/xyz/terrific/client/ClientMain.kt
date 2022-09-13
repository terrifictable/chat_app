package xyz.terrific.client

import java.net.Socket

enum class ClientMain {
    INSTANCE;

    @Throws(Exception::class)
    fun init(ip: String?, port: Int) {
        val socket = Socket(ip, port)
        val client = Client(socket)
        client.start()
    }

    @Throws(Exception::class)
    fun init(name: String?, ip: String?, port: Int) {
        val socket = Socket(ip, port)
        val client = Client(socket, name)
        client.start()
    }
}
