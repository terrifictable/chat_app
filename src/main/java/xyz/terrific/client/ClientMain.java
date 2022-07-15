package xyz.terrific.client;

import java.net.Socket;

public enum ClientMain {
    INSTANCE;

    public void init(String ip, int port) throws Exception {
        Socket socket = new Socket(ip, port);
        Client client = new Client(socket);
        client.start();

    }
    public void init(String name, String ip, int port) throws Exception {
        Socket socket = new Socket(ip, port);
        Client client = new Client(socket, name);
        client.start();

    }

}
