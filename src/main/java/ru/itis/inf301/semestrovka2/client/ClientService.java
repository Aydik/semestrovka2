package ru.itis.inf301.semestrovka2.client;

import javafx.application.Platform;
import lombok.Getter;
import lombok.Setter;
import ru.itis.inf301.semestrovka2.controller.util.FXMLLoaderUtil;
import ru.itis.inf301.semestrovka2.model.Board;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.lang.Thread.sleep;

@Getter @Setter
public class ClientService {
    private final Client client;
    private final int lobby_id;
    private int client_index;
    private Board board;

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
