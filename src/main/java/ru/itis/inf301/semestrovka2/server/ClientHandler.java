package ru.itis.inf301.semestrovka2.server;

import ru.itis.inf301.semestrovka2.client.Client;
import ru.itis.inf301.semestrovka2.client.ClientService;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private Lobby lobby;
    private ClientService clientService;

    public ClientHandler(Socket socket, ClientService clientService) {
        this.socket = socket;
        this.clientService = clientService;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.err.println("Error setting up streams: " + e.getMessage());
            close();
        }
    }


    @Override
    public void run() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                if (message.startsWith("CREATE_LOBBY") || message.startsWith("JOIN_LOBBY")) {
                    handleLobbyCommands(message);
                } else if (message.startsWith("MOVE")) {
                    handleMoveCommand(message);
                } else if (message.startsWith("VERTICAL_WALL")) {
                    handleVerticalWallCommand(message);
                } else if (message.startsWith("HORIZONTAL_WALL")) {
                    handleHorizontalWallCommand(message);
                }
            }
        } catch (IOException e) {
            System.out.println("Client disconnected.");
        } finally {
            close();
        }
    }

    private void handleLobbyCommands(String message) throws IOException {
        if (message.startsWith("CREATE_LOBBY")) {
            int lobbyId = Integer.parseInt(message.split(" ")[1]);
            lobby = new Lobby(lobbyId);
            Server.addLobby(lobby);
            lobby.addClient(this);
            sendMessage("Lobby created with ID: " + lobbyId);
        } else if (message.startsWith("JOIN_LOBBY")) {
            int lobbyId = Integer.parseInt(message.split(" ")[1]);
            lobby = Server.findLobbyById(lobbyId);
            if (lobby != null) {
                lobby.addClient(this);
                sendMessage("Joined lobby: " + lobbyId);
            } else {
                sendMessage("Lobby not found.");
            }
        }
    }

    private void handleMoveCommand(String message) {
        if (clientService == null || clientService.getBoard() == null) {
            System.err.println("ClientService or Board is not initialized!");
            return;
        }
        String[] parts = message.split(" ");
        int user = lobby.getHod();
        int x = Integer.parseInt(parts[1]);
        int y = Integer.parseInt(parts[2]);
        int currentPlayer = Integer.parseInt(parts[3]);
        if (user != currentPlayer) {
            System.err.println("Invalid move: wrong player!");
            sendMessage("Invalid move: wrong player!");
            return;
        }
        if (clientService.getBoard().move(user, x, y)) {
            lobby.sendMessage("MOVE " + x + " " + y + " " + user);
            System.out.println("Processed move: MOVE " + x + " " + y + " " + user);
        } else {
            sendMessage("Invalid move: invalid position!");
        }
    }

    private void handleVerticalWallCommand(String message) {
        if (clientService == null || clientService.getBoard() == null) {
            System.err.println("ClientService or Board is not initialized!");
            return;
        }
        String[] parts = message.split(" ");
        int user = lobby.getHod();
        int x = Integer.parseInt(parts[1]);
        int y = Integer.parseInt(parts[2]);
        if (user != Integer.parseInt(parts[3])) {
            System.err.println("Invalid wall placement: wrong player!");
            sendMessage("Invalid wall placement: wrong player!");
            return;
        }
        if (clientService.getBoard().putVerticalWall(user, x, y)) {
            lobby.sendMessage("VERTICAL_WALL " + x + " " + y);
            System.out.println("Processed vertical wall: VERTICAL_WALL " + x + " " + y);
        } else {
            sendMessage("Invalid wall placement: invalid position!");
        }
    }

    private void handleHorizontalWallCommand(String message) {
        if (clientService == null || clientService.getBoard() == null) {
            System.err.println("ClientService or Board is not initialized!");
            return;
        }
        String[] parts = message.split(" ");
        int user = lobby.getHod();
        int x = Integer.parseInt(parts[1]);
        int y = Integer.parseInt(parts[2]);
        if (user != Integer.parseInt(parts[3])) {
            System.err.println("Invalid wall placement: wrong player!");
            sendMessage("Invalid wall placement: wrong player!");
            return;
        }
        if (clientService.getBoard().putHorizontalWall(user, x, y)) {
            lobby.sendMessage("HORIZONTAL_WALL " + x + " " + y);
            System.out.println("Processed horizontal wall: HORIZONTAL_WALL " + x + " " + y);
        } else {
            sendMessage("Invalid wall placement: invalid position!");
        }
    }

    public void sendMessage(String message) {
        try {
            out.write(message + "\n");
            out.flush();
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }

    // Метод для получения сообщения от клиента (блокирующий вызов)
    public String getMessage() {

            return this.clientService.getClient().readMessage();
//        } catch (IOException e) {
//            System.err.println("Error reading message in ClientHandler.getMessage(): " + e.getMessage());
//            return null;
//        }

    }


    public void close() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }

    public ClientService getClientService() {
        return clientService;
    }
}
