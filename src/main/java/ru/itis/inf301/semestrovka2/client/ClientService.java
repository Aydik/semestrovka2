package ru.itis.inf301.semestrovka2.client;

import lombok.Getter;
import lombok.Setter;

import java.util.Random;

@Getter @Setter
public class ClientService {
    private final Client client;
    private final int lobby_id;

    public ClientService() {
        Random random = new Random();
        String lobby_id = Integer.toString(random.nextInt(1000000));

        client = new Client();
        client.connect();

        sendMessage(lobby_id);
        this.lobby_id = Integer.parseInt(lobby_id);
    }

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
