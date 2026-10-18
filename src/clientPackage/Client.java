package clientPackage;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client {
    private static final int DEFAULT_PORT = 5000;
    private static final Pattern OPERATION_PATTERN =
            Pattern.compile("(-?\\d+)\\s*([+\\-*/])\\s*(-?\\d+)");

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port;
        try {
            port = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_PORT;
        } catch (NumberFormatException exception) {
            System.err.println("Port invalide.");
            return;
        }

        try (Socket socket = new Socket(host, port);
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(
                     new BufferedInputStream(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("=== TP2 - Calculatrice TCP par objets ===");
            System.out.println("Entrez une operation comme: 55 + 25");
            System.out.println("Operateurs acceptes: +, -, *, /");
            System.out.println("Entrez exit pour quitter.");

            while (true) {
                System.out.print("Operation: ");
                if (!scanner.hasNextLine()) {
                    break;
                }
                String expression = scanner.nextLine().trim();
                if (expression.equalsIgnoreCase("exit")) {
                    break;
                }

                OperationRequest request = parseOperation(expression);
                if (request == null) {
                    System.out.println("Expression invalide. Utilisez: nombre operateur nombre");
                    continue;
                }

                output.writeObject(request);
                output.flush();

                CalculationResponse response = readResponse(input);
                System.out.println(response.getMessage());
            }
        } catch (IOException exception) {
            System.err.println("Erreur client: " + exception.getMessage());
        } catch (ClassNotFoundException exception) {
            System.err.println("Objet recu inconnu: " + exception.getMessage());
        }
    }

    private static OperationRequest parseOperation(String expression) {
        Matcher matcher = OPERATION_PATTERN.matcher(expression);
        if (!matcher.matches()) {
            return null;
        }

        try {
            return new OperationRequest(
                    Integer.parseInt(matcher.group(1)),
                    matcher.group(2),
                    Integer.parseInt(matcher.group(3)));
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private static CalculationResponse readResponse(ObjectInputStream input)
            throws IOException, ClassNotFoundException {
        try {
            Object response = input.readObject();
            if (!(response instanceof CalculationResponse)) {
                throw new IOException("Reponse serveur invalide.");
            }
            return (CalculationResponse) response;
        } catch (EOFException exception) {
            throw new IOException("Le serveur a ferme la connexion.", exception);
        }
    }
}
