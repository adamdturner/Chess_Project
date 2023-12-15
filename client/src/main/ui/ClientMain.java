package ui;

import java.util.Scanner;

public class ClientMain {

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            ChessClient client = new ChessClient(scanner);
            System.out.println("👑 Welcome to 240 chess. Type Help to get started. 👑");

            client.run();

            scanner.close();

        } catch (Exception e) {
            System.out.println("Failed to connect to the server (clientMain) " + e.getMessage());
        }
    }
}
