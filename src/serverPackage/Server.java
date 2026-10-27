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
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private static final int DEFAULT_PORT = 5000;
    private static final AtomicInteger CLIENT_COUNTER = new AtomicInteger();
    private static final AtomicInteger OPERATION_COUNTER = new AtomicInteger();

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
            System.out.println("Serveur multi-thread demarre sur le port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                int clientNumber = CLIENT_COUNTER.incrementAndGet();
                Thread clientThread = new Thread(
                        new ClientHandler(clientSocket, clientNumber),
                        "client-handler-" + clientNumber);
                clientThread.start();
            }
        } catch (IOException exception) {
            System.err.println("Erreur serveur: " + exception.getMessage());
        }
    }

    private static final class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final int clientNumber;

        private ClientHandler(Socket clientSocket, int clientNumber) {
            this.clientSocket = clientSocket;
            this.clientNumber = clientNumber;
        }

        @Override
        public void run() {
            String clientAddress = clientSocket.getRemoteSocketAddress().toString();
            System.out.println("Client n°" + clientNumber + " connecte depuis " + clientAddress);

            try (Socket socket = clientSocket;
                 ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream input = new ObjectInputStream(
                         new BufferedInputStream(socket.getInputStream()))) {

                output.writeObject("Vous êtes le client n°" + clientNumber);
                output.flush();
                processClient(input, output);
            } catch (EOFException exception) {
                System.out.println("Client n°" + clientNumber + " deconnecte.");
            } catch (IOException | ClassNotFoundException exception) {
                System.err.println("Erreur avec le client n°" + clientNumber + ": "
                        + exception.getMessage());
            }
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
            } else {
                OperationRequest request = (OperationRequest) received;
                CalculationResponse response = calculate(request);
                output.writeObject(response);
                int operationNumber = OPERATION_COUNTER.incrementAndGet();
                System.out.println("Operation n°" + operationNumber
                        + " traitee par " + Thread.currentThread().getName());
                System.out.println(response.getMessage());
            }
            output.flush();
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
