package ru.itis.inf301.semestrovka2.client;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ClientService {
    private final Client client;
    private final int lobby_id;
    private int client_index;

    public ClientService(String lobby_id) {
        client = new Client();
        client.connect();
        sendMessage(lobby_id);
        this.lobby_id = Integer.parseInt(lobby_id);
    }

    private void sendMessage(String message) {
        client.sendMessage(message);
    }

    public void disconnect() {
        client.closeResources();
    }
}
