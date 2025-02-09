package ru.itis.inf301.semestrovka2.client;

import lombok.Getter;
import ru.itis.inf301.semestrovka2.model.Board;

public class ClientService {
    @Getter
    private final Client client;
    private Board board;
    private boolean isConnectedToLobby;

    public ClientService(Client client) {
        this.client = client;
        isConnectedToLobby = false;
        this.board = new Board();
    }

    public void connect(String lobbyId, boolean isCreate) {
        client.connect();
        if (client.getClientSocket() == null) {
            System.err.println("Failed to establish connection to the server.");
            return;
        }
        if (isCreate) {
            client.sendMessage("CREATE_LOBBY " + lobbyId);
        } else {
            client.sendMessage("JOIN_LOBBY " + lobbyId);
        }
        isConnectedToLobby = true;
    }

    public void disconnect() {
        client.closeResources();
        isConnectedToLobby = false;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }

    public boolean isConnectedToLobby() {
        return isConnectedToLobby;
    }
}