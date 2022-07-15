package xyz.terrific.server;

import xyz.terrific.Main;
import xyz.terrific.utils.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public enum ServerMain {
    INSTANCE;

    public boolean running;
    public Logger logger = new Logger("ServerMain");

    public void init(int port) throws IOException {
        if (ServerMain.INSTANCE.isRunning()) {
            System.out.println("[ ERROR | Server ]  Server is already running!");
            return;
        }

        ServerSocket socket = new ServerSocket(port);
        ServerMain.INSTANCE.setRunning(true);
        if (!ServerMain.INSTANCE.isRunning()) return;
        this.logger.log("[ INFO | Server ]  Server started { " + InetAddress.getLocalHost().getHostAddress().trim() + ":" + socket.getLocalPort() + " }!");

        while (!socket.isClosed()) {
            Socket conn = socket.accept();

            System.out.printf(" [ SERVER | %s ] Connected\n", conn.getRemoteSocketAddress().toString());
            ServerHandler handler = new ServerHandler(conn);

            Thread thread = new Thread(handler);
            thread.start();
        }
        socket.close();

    }



    public boolean isRunning() {
        return running;
    }
    public void setRunning(boolean running) {
        this.running = running;
    }
}
