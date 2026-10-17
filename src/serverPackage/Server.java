package serverPackage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int DEFAULT_PORT = 5000;

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Port invalide. Utilise: java Server [port]");
                return;
            }
        }

        InetAddress localAddress = null;
        try {
            localAddress = InetAddress.getLocalHost();
        } catch (IOException e) {
            System.err.println("Erreur adresse locale: " + e.getMessage());
            return;
        }

        String host = localAddress.getHostAddress();
        System.out.println("Serveur de calculatrice en attente sur " + host + ":" + port);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    System.out.println("Client connecté: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

                    DataInputStream input = new DataInputStream(clientSocket.getInputStream());
                    DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());

                    while (true) {
                        String expression;
                        try {
                            expression = input.readUTF();
                        } catch (IOException exception) {
                            break;
                        }
                        if (expression.equalsIgnoreCase("exit")) {
                            break;
                        }

                        String[] operands = expression.trim().split("\\s*\\+\\s*");
                        if (operands.length != 2) {
                            output.writeUTF("Expression invalide");
                            output.flush();
                            continue;
                        }

                        try {
                            int firstOperand = Integer.parseInt(operands[0]);
                            int secondOperand = Integer.parseInt(operands[1]);
                            int result = firstOperand + secondOperand;
                            output.writeUTF(firstOperand + " + " + secondOperand + " = " + result);
                            output.flush();
                            System.out.println(expression + " = " + result);
                        } catch (NumberFormatException exception) {
                            output.writeUTF("Expression invalide");
                            output.flush();
                        }
                    }
                } catch (IOException e) {
                    // Accept loop error
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur serveur: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
