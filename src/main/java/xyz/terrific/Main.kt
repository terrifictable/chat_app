package xyz.terrific

import xyz.terrific.client.ClientMain
import xyz.terrific.server.ServerMain
import xyz.terrific.utils.Logger
import java.io.IOException
import java.util.*

object Main {
    var logger = Logger("IRC-Main")


    @JvmStatic
    fun main(args: Array<String>) {
        logger.init()
        val scanner = Scanner(System.`in`)
        println("`.server <port>` to start server `.client <ip> <port>` to start client")
        print("> ")
        val input = scanner.nextLine()
        if (input.strip().startsWith(".server")) {
            try {
                ServerMain.INSTANCE.init(input.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()[1].toInt())
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
        else if (input.strip().startsWith(".client")) {
            try {
                if (input.contains(":")) {
                    ClientMain.INSTANCE.init(
                        input.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()[1].split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0],
                        input.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()[1].split(":".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()[1].toInt()
                    )
                } else {
                    ClientMain.INSTANCE.init(input.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()[1],
                        input.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[2].toInt())
                }
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
        else if (input.strip().startsWith(".cclient")) {
            try {
                ClientMain.INSTANCE.init(
                    "TestUser",
                    input.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()[1].split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0],
                    input.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()[1].split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1].toInt()
                )
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
        else {
            println(" -> Unknown command!")
        }


        logger.exit()
    }
}
