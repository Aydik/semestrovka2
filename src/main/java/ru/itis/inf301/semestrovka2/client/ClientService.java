package ru.itis.inf301.semestrovka2.client;

import lombok.Getter;
import ru.itis.inf301.semestrovka2.model.Board;

@Getter
public class ClientService {
    private final Client client;
    private Board board;
    private boolean connectedToLobby;

    public ClientService(Client client) {
        this.client = client;
        this.board = new Board();
        this.connectedToLobby = false;
    }

    /**
     * Подключается к серверу и отправляет команду:
     * если isCreate==true, отправляет "CREATE_LOBBY <lobbyId>",
     * иначе "JOIN_LOBBY <lobbyId>".
     */
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
        connectedToLobby = true;
    }

    public void disconnect() {
        client.closeResources();
        connectedToLobby = false;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}
