package serverPackage;

import clientPackage.CalculationResponse;
import clientPackage.OperationRequest;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int DEFAULT_PORT = 5000;

    public static void main(String[] args) {
        int port;
        try {
            port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        } catch (NumberFormatException exception) {
            System.err.println("Port invalide. Utilisez: java serverPackage.Server [port]");
            return;
        }

        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            System.out.println("Serveur de calculatrice en attente sur " + host + ":" + port);
        } catch (IOException exception) {
            System.err.println("Adresse locale indisponible: " + exception.getMessage());
        }

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     ObjectOutputStream output =
                             new ObjectOutputStream(clientSocket.getOutputStream());
                     ObjectInputStream input =
                             new ObjectInputStream(
                                     new BufferedInputStream(clientSocket.getInputStream()))) {

                    System.out.println("Client connecte: "
                            + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
                    processClient(input, output);
                } catch (IOException | ClassNotFoundException exception) {
                    System.err.println("Erreur avec le client: " + exception.getMessage());
                }
            }
        } catch (IOException exception) {
            System.err.println("Erreur serveur: " + exception.getMessage());
        }
    }

    private static void processClient(ObjectInputStream input, ObjectOutputStream output)
            throws IOException, ClassNotFoundException {
        while (true) {
            Object received;
            try {
                received = input.readObject();
            } catch (EOFException exception) {
                return;
            }

            if (!(received instanceof OperationRequest)) {
                output.writeObject(CalculationResponse.error("Objet de requete invalide."));
                output.flush();
                continue;
            }

            OperationRequest request = (OperationRequest) received;
            CalculationResponse response = calculate(request);
            output.writeObject(response);
            output.flush();
            System.out.println(response.getMessage());
        }
    }

    private static CalculationResponse calculate(OperationRequest request) {
        double firstOperand = request.getFirstOperand();
        double secondOperand = request.getSecondOperand();
        double result;

        switch (request.getOperator()) {
            case "+":
                result = firstOperand + secondOperand;
                break;
            case "-":
                result = firstOperand - secondOperand;
                break;
            case "*":
                result = firstOperand * secondOperand;
                break;
            case "/":
                if (secondOperand == 0) {
                    return CalculationResponse.error("Division par zero impossible.");
                }
                result = firstOperand / secondOperand;
                break;
            default:
                return CalculationResponse.error("Operateur invalide.");
        }

        return CalculationResponse.success(request, result);
    }
}
