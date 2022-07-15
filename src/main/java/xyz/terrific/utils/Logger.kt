package xyz.terrific.utils

class Logger(private var name: String) {

    fun init() {
        if (name.isEmpty()) {
            name = "Logger"
        }
        println("[$name] Logger initialized")
    }

    fun exit() {
        println("[$name] Logger exited")
    }


    fun log(message: String) {
        println("[${this.name}] $message")
    }
    fun log(funname: String, message: String) {
        println("[${this.name}] $funname: $message")
    }

    fun warning(message: String) {
        println("[${this.name}] [WARNING] $message")
    }
    fun warning(funname: String, message: String) {
        println("[${this.name}] [WARNING] $funname: $message")
    }

    fun error(message: String) {
        println("[${this.name}] [ERROR] $message")
    }
    fun error(funname: String, message: String) {
        println("[${this.name}] $funname: $message")
    }
}
