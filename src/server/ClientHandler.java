package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;


public class ClientHandler implements Runnable {

    private Server server;
    private PrintWriter outMessage;
    private Scanner inMessage;
    private static final String HOST = "localhost";
    private static final int PORT = 3443;
    private Socket clientSocket = null;
    private static int clients_count = 0;


    public ClientHandler(Socket clientSocket, Server server) {
        try {
            clients_count++;
            this.server = server;
            this.clientSocket = clientSocket;
            this.outMessage = new PrintWriter(clientSocket.getOutputStream());
            this.inMessage = new Scanner(clientSocket.getInputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {

                server.sendMessageToAllClients("Новый участник вошёл в чат!");
               server.sendMessageToAllClients("Клиентов в чате = " + clients_count);
                break;
            }

            while (true) {

                if (inMessage.hasNext()) {
                    String clientMessage = inMessage.nextLine();

                   if (clientMessage.equalsIgnoreCase("##session##end##")) {
                        break;
                   }

                    System.out.println(clientMessage);
                    server.sendMessageToAllClients(clientMessage);
                }
               Thread.sleep(100);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            this.close();
        }
    }

    public void sendMsg(String msg) {
        try {
            outMessage.println(msg);
            outMessage.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void close() {
        server.removeClient(this);
        clients_count--;
        server.sendMessageToAllClients("Клиентов в чате = " + clients_count);
    }
}
