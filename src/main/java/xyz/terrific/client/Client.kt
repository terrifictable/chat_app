package xyz.terrific.client

import xyz.terrific.utils.Logger
import xyz.terrific.utils.RSAUtils
import java.io.*
import java.net.InetAddress
import java.net.Socket
import java.util.*

class Client {
    var socket: Socket
    var reader: BufferedReader
    var writer: BufferedWriter
    private var rsautil: RSAUtils
    var logger = Logger("Client")

    constructor(socket: Socket) {
        val hostname = InetAddress.getLocalHost().hostName
        rsautil = RSAUtils()
        this.socket = socket
        writer = BufferedWriter(OutputStreamWriter(this.socket.getOutputStream()))
        reader = BufferedReader(InputStreamReader(this.socket.getInputStream()))
        rsautil.generateKeyPair(1024 * 4)
        writer.write(hostname)
        writer.newLine()
        writer.flush()
        writer.write(rsautil._getStringPublicKey())
        writer.newLine()
        writer.flush()
        clients.add(this)
    }

    constructor(socket: Socket, name: String?) {
        val hostname = InetAddress.getLocalHost().hostName
        rsautil = RSAUtils()
        this.socket = socket
        writer = BufferedWriter(OutputStreamWriter(this.socket.getOutputStream()))
        reader = BufferedReader(InputStreamReader(this.socket.getInputStream()))
        rsautil.generateKeyPair(1024 * 4)
        writer.write(name)
        writer.newLine()
        writer.flush()
        writer.write(rsautil._getStringPublicKey())
        writer.newLine()
        writer.flush()
        clients.add(this)
    }

    fun start() {
        try {
            Thread {
                try {
                    while (socket.isConnected) {
                        val message = reader.readLine()
                        if (message != null)
                            if (rsautil.decrypt(message, rsautil._getStringPrivateKey())
                                .strip().trim { it <= ' ' }
                                .endsWith("null") && !rsautil.decrypt(message, rsautil._getStringPrivateKey())
                                .strip().trim { it <= ' ' }
                                .endsWith("null")
                        ) logger.log(rsautil.decrypt(message, rsautil._getStringPrivateKey()))
                    }
                } catch (e: Exception) {
                    logger.error("EXCEPTION ->  start()  <- " + e.message)
                    close(socket, reader, writer)
                }
            }.start()
            val scanner = Scanner(System.`in`)
            while (socket.isConnected) {
                val message = scanner.nextLine().strip().replace("\n", "")
                if (message == ".exit") {
                    close(socket, reader, writer)
                    break
                }
                sendMessage(message)
            }
        } catch (e: Exception) {
            logger.error("EXCEPTION ->  inputMessage()  <- " + e.message)
            close(socket, reader, writer)
        }
    }

    fun sendMessage(message: String) {
        if (message == "" || message == " " || message == "\n" || message == "\b") return
        try {
            writer.write(message)
            writer.newLine()
            writer.flush()
        } catch (e: Exception) {
            logger.error("EXCEPTION ->  sendMessage()  <- " + e.message)
            close(socket, reader, writer)
        }
    }

    fun removeClient() {
        clients.remove(this)
        try {
            writer.write("Closed Connection!")
            writer.newLine()
            writer.flush()
        } catch (e: IOException) {
            logger.error("EXCEPTION ->  removeClient()  <- " + e.message)
        }
    }

    fun close(socket: Socket?, reader: BufferedReader?, writer: BufferedWriter?) {
        removeClient()
        try {
            reader?.close()
            writer?.close()
            socket?.close()
        } catch (e: Exception) {
            logger.error("EXCEPTION ->  close()  <- " + e.message)
        }
    }

    companion object {
        var clients = ArrayList<Client>()
    }
}