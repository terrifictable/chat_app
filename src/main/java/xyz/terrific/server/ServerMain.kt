package xyz.terrific.server

import xyz.terrific.utils.Logger
import java.io.IOException
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket

enum class ServerMain {
    INSTANCE;

    var isRunning = false
    var logger = Logger("ServerMain")
    @Throws(IOException::class)
    fun init(port: Int) {
        if (isRunning) {
            println("[ ERROR | Server ]  Server is already running!")
            return
        }
        val socket = ServerSocket(port)
        isRunning = true
        if (!isRunning) return
        logger.log("[ INFO | Server ]  Server started { " + InetAddress.getLocalHost().hostAddress.trim { it <= ' ' } + ":" + socket.localPort + " }!")
        while (!socket.isClosed) {
            val conn = socket.accept()
            System.out.printf(" [ SERVER | %s ] Connected\n", conn.remoteSocketAddress.toString())
            val handler = ServerHandler(conn)
            val thread = Thread(handler)
            thread.start()
        }
        socket.close()
    }
}
