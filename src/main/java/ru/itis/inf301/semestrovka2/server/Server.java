package ru.itis.inf301.semestrovka2.server;

import ru.itis.inf301.semestrovka2.client.Client;
import ru.itis.inf301.semestrovka2.client.ClientService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private static final int SERVER_PORT = 50000;
    private static final CopyOnWriteArrayList<Lobby> lobbies = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Server started on port " + SERVER_PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                Client client = new Client(clientSocket);
                ClientService clientService = new ClientService(client);
                // Создаем ClientHandler, передавая ему socket и clientService
                ClientHandler clientHandler = new ClientHandler(clientSocket, clientService);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    public static Lobby findLobbyById(int lobbyId) {
        for (Lobby lobby : lobbies) {
            if (lobby.getId() == lobbyId) {
                return lobby;
            }
        }
        return null;
    }

    public static void addLobby(Lobby lobby) {
        lobbies.add(lobby);
    }
}
