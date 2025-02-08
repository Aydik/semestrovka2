package ru.itis.inf301.semestrovka2.client;

import lombok.Getter;

public class ClientService {
    @Getter
    private final Client client;

    public ClientService(String lobby_id) {
        client = new Client();
        client.connect();
        client.startReadingMessages();
        sendMessage(lobby_id);
    }

    private void sendMessage(String message) {
        client.sendMessage(message);
    }

    public void disconnect() {
        client.closeResources();
    }
}
