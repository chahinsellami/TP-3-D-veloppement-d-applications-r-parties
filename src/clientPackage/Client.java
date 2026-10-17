package clientPackage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final int DEFAULT_PORT = 5000;

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_PORT;

        try (Socket socket = new Socket(host, port);
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream());
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("=== TP2 - Addition TCP ===");
            System.out.println("Entrez une operation comme: 55 + 25");
            System.out.println("Entrez exit pour quitter.");

            while (true) {
                System.out.print("Operation: ");
                String expression = scanner.nextLine().trim();
                if (expression.equalsIgnoreCase("exit")) {
                    break;
                }

                if (!expression.matches("-?\\d+\\s*\\+\\s*-?\\d+")) {
                    System.out.println("Expression invalide. Utilisez: nombre + nombre");
                    continue;
                }

                output.writeUTF(expression);
                output.flush();
                String response = input.readUTF();
                System.out.println("Resultat: " + response);
            }
        } catch (IOException exception) {
            System.err.println("Erreur client: " + exception.getMessage());
        } catch (NumberFormatException exception) {
            System.err.println("Port invalide.");
        }
    }
}
