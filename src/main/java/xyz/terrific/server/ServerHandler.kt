package xyz.terrific.server

import xyz.terrific.utils.Logger
import xyz.terrific.utils.RSAUtils
import java.io.*
import java.net.Socket
import java.security.PublicKey
import java.util.*

class ServerHandler(conn: Socket?) : Runnable {
    private var conn: Socket? = null
    var reader: BufferedReader? = null
    var writer: BufferedWriter? = null
    var publicKey: PublicKey? = null
    var name: String? = null
    var rsautil: RSAUtils? = null
    var logger = Logger("ServerHandler")


    init {
        try {
            rsautil = RSAUtils()
            this.conn = conn
            writer = BufferedWriter(OutputStreamWriter(this.conn!!.getOutputStream()))
            reader = BufferedReader(InputStreamReader(this.conn!!.getInputStream()))
            name = reader!!.readLine()
            publicKey = rsautil!!.getPublicKey(reader!!.readLine())
            handlers.add(this)
            logger.log(String.format(" [ %s ]  Connected", name))
            sendMessage(String.format(" [ %s ]  Connected", name))
        } catch (e: IOException) {
            close(this.conn, reader, writer)
        }
    }


    override fun run() {
        var message: String?
        while (conn!!.isConnected) {
            try {
                message = reader!!.readLine()
                // System.out.printf(" [ %s ]  %s\n", this.name, message);
                sendMessage(String.format(" [ %s ]  %s\n", name, message))
            } catch (e: IOException) {
                logger.error("EXCEPTION ->  run()  <- " + e.message)
                close(conn, reader, writer)
                return
            }
        }
    }

    fun sendMessage(message: String?) {
        for (handler in handlers) {
            try {
                handler.writer!!.write(
                    Base64.getEncoder().encodeToString(rsautil!!.encrypt(message!!, handler.publicKey))
                )
                handler.writer!!.newLine()
                handler.writer!!.flush()
            } catch (e: Exception) {
                logger.error("EXCEPTION ->  sendMessage()  <- " + e.message)
                close(conn, reader, writer)
            }
        }
    }

    fun removeHandler() {
        handlers.remove(this)
        sendMessage(String.format(" [ %s ]  Closed Connection\n", name))
    }

    fun close(socket: Socket?, reader: BufferedReader?, writer: BufferedWriter?) {
        removeHandler()
        try {
            reader?.close()
            writer?.close()
            socket?.close()
        } catch (e: Exception) {
            logger.error("EXCEPTION ->  close()  <- " + e.message)
        }
    }

    companion object {
        var handlers = ArrayList<ServerHandler>()
    }
}
