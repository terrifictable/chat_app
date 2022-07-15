package xyz.terrific.client;

import xyz.terrific.utils.Logger;
import xyz.terrific.utils.RSAUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
    public static ArrayList<Client> clients = new ArrayList<>();

    public Socket socket;
    public BufferedReader reader;
    public BufferedWriter writer;
    private RSAUtils rsautil;
    public Logger logger = new Logger("Client");


    public Client(Socket socket) throws Exception {

        String hostname = InetAddress.getLocalHost().getHostName();
        this.rsautil    = new RSAUtils();
        this.socket     = socket;
        this.writer     = new BufferedWriter(new OutputStreamWriter(    this.socket.getOutputStream()   ));
        this.reader     = new BufferedReader(new InputStreamReader(     this.socket.getInputStream()    ));
        this.rsautil.generateKeyPair(1024 * 4);


        this.writer.write(hostname);
        this.writer.newLine();
        this.writer.flush();
        this.writer.write(this.rsautil._getStringPublicKey());
        this.writer.newLine();
        this.writer.flush();

        clients.add(this);
    }
    public Client(Socket socket, String name) throws Exception {

        String hostname = InetAddress.getLocalHost().getHostName();
        this.rsautil    = new RSAUtils();
        this.socket     = socket;
        this.writer     = new BufferedWriter(new OutputStreamWriter(    this.socket.getOutputStream()   ));
        this.reader     = new BufferedReader(new InputStreamReader(     this.socket.getInputStream()    ));
        this.rsautil.generateKeyPair(1024 * 4);


        this.writer.write(name);
        this.writer.newLine();
        this.writer.flush();
        this.writer.write(this.rsautil._getStringPublicKey());
        this.writer.newLine();
        this.writer.flush();

        clients.add(this);
    }

    public void start() {
        try {

            new Thread(() -> {
                try {
                    while (this.socket.isConnected()) {
                        String message = this.reader.readLine();
                        if (message != null) {
                            this.logger.log(this.rsautil.decrypt(message, this.rsautil._getStringPrivateKey()));
                        }
                    }
                } catch (Exception e) {
                    close(this.socket, this.reader, this.writer);
                    this.logger.error("EXCEPTION ->  start()  <- " + e.getMessage());
                }
            }).start();


            Scanner scanner = new Scanner(System.in);
            while (this.socket.isConnected()) {

                String message = scanner.nextLine().strip().replace("\n", "");

                if (message.equals(".exit")) {
                    close(this.socket, this.reader, this.writer);
                    break;
                }

                sendMessage(message);
            }

        } catch (Exception e) {
            close(this.socket, this.reader, this.writer);
            this.logger.error("EXCEPTION ->  inputMessage()  <- " + e.getMessage());
        }
    }


    public void sendMessage(String message) {
        try {

            this.writer.write(message);
            this.writer.newLine();
            this.writer.flush();

        } catch (Exception e) {
            close(this.socket, this.reader, this.writer);
            System.out.print("EXCEPTION ->  sendMessage()  <- " + e.getMessage());
        }
    }



    public void removeClient() {
        clients.remove(this);
        System.out.println("Closed Connection");
    }

    public void close(Socket socket, BufferedReader reader, BufferedWriter writer) {
        removeClient();
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null) socket.close();
        } catch (Exception e) {
            System.out.print("EXCEPTION ->  close()  <- " + e.getMessage());
        }
    }

}
