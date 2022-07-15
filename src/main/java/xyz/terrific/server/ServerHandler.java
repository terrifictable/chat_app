package xyz.terrific.server;

import xyz.terrific.Main;
import xyz.terrific.utils.Logger;
import xyz.terrific.utils.RSAUtils;

import java.io.*;
import java.net.Socket;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;

public class ServerHandler implements Runnable {
    public static ArrayList<ServerHandler> handlers = new ArrayList<>();

    private Socket          conn;
    public BufferedReader   reader;
    public BufferedWriter   writer;
    public PublicKey        publicKey;
    public String           name;
    public RSAUtils         rsautil;

    public Logger logger = new Logger("ServerHandler");


    public ServerHandler(Socket conn) {
        try {
            this.rsautil            = new RSAUtils();
            this.conn = conn;
            this.writer             = new BufferedWriter(new OutputStreamWriter(    this.conn.getOutputStream()   ));
            this.reader             = new BufferedReader(new InputStreamReader(     this.conn.getInputStream()    ));
            this.name               = this.reader.readLine();
            this.publicKey          = this.rsautil.getPublicKey(this.reader.readLine());
            handlers.add(this);

            this.logger.log(String.format(" [ %s ]  Connected", this.name));
            sendMessage(String.format(" [ %s ]  Connected", this.name));
        } catch (IOException e) {
            close(this.conn, this.reader, this.writer);
        }
    }


    @Override
    public void run() {
        String message;

        while (this.conn.isConnected()) {
            try {

                message = this.reader.readLine();
                System.out.printf(" [ %s ]  %s\n", this.name, message);

                sendMessage(String.format(" [ %s ]  %s\n", this.name, message));

            } catch (IOException e) {
                close(this.conn, this.reader, this.writer);
                this.logger.error("EXCEPTION ->  run()  <- " + e.getMessage());
                this.close(this.conn, this.reader, this.writer);
            }
        }
    }




    public void sendMessage(String message) {
        for (ServerHandler handler : handlers) {
            try {

                String enc_message = Base64.getEncoder().encodeToString(this.rsautil.encrypt(message, handler.publicKey));
                System.out.println("MESSAGE: " + enc_message);
                handler.writer.write(enc_message);
                handler.writer.newLine();
                handler.writer.flush();

            } catch (Exception e) {
                e.printStackTrace();
                close(this.conn, this.reader, this.writer);
            }
        }
    }


    public void removeHandler() {
        handlers.remove(this);
        System.out.printf(" [ %s ]  Closed Connection\n", this.name);
    }

    public void close(Socket socket, BufferedReader reader, BufferedWriter writer) {
        removeHandler();
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null) socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
