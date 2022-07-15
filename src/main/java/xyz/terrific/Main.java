package xyz.terrific;

import xyz.terrific.client.ClientMain;
import xyz.terrific.server.ServerMain;
import xyz.terrific.utils.Logger;

import java.io.IOException;
import java.util.Scanner;


public class Main {

    public static Logger logger = new Logger("IRC-Main");

    public static void main(String[] args) {
        logger.init();

        Scanner scanner = new Scanner(System.in);
        System.out.println("`.server <port>` to start server `.client <ip> <port>` to start client");
        System.out.print("> ");
        String input = scanner.nextLine();
        if (input.strip().startsWith(".server")) {
            try {
                ServerMain.INSTANCE.init(Integer.parseInt(input.split(" ")[1]));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (input.strip().startsWith(".client")) {
            try {
                if (input.contains(":")) {
                    ClientMain.INSTANCE.init(input.split(" ")[1].split(":")[0], Integer.parseInt(input.split(" ")[1].split(":")[1]));
                } else {
                    ClientMain.INSTANCE.init(input.split(" ")[1], Integer.parseInt(input.split(" ")[2]));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (input.strip().startsWith(".cclient")) {
            try {
                ClientMain.INSTANCE.init("TestUser", input.split(" ")[1].split(":")[0], Integer.parseInt(input.split(" ")[1].split(":")[1]));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println(" -> Unknown command!");
        }

        logger.exit();
    }

}
