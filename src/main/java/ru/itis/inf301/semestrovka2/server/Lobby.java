package ru.itis.inf301.semestrovka2.server;

import lombok.Getter;
import ru.itis.inf301.semestrovka2.model.Board;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Lobby implements Runnable {
    @Getter
    private final int id;
    @Getter
    private final List<ClientHandler> clients;
    private volatile boolean started;
    private Board board;

    public Lobby(int id) {
        this.id = id;
        this.clients = new ArrayList<>();
        this.started = false;
    }

    public void addClient(ClientHandler client) {
        if (clients.size() < 2) {
            clients.add(client);
            client.sendMessage("Waiting for another player...");
            if (clients.size() == 2) {
                startLobby();
            }
        } else {
            throw new RuntimeException("Lobby is full.");
        }
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    public boolean isEmpty() {
        return clients.isEmpty();
    }

    @Override
    public void run() {
        board = new Board();
        if (started) {
            sendMessage("Game started!");
            int curClientIndex = board.getStep();
            String message;
            while (clients.size() == 2) {
                sendMessage("Очередь игрока " + (curClientIndex + 1));
                try {
                    message = clients.get(curClientIndex).getMessage();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                if (message.trim().isEmpty() || message.equalsIgnoreCase("exit")) {
                    // Обработка выхода игрока
                    clients.get(curClientIndex).sendMessage("Вы покинули игру.");
                    removeClient(clients.get(curClientIndex));  // Убираем игрока из лобби
                    if (clients.isEmpty()) {
                        sendMessage("Игра завершена. Все игроки покинули лобби.");
                        break;
                    } else {
                        sendMessage("Игрок покинул игру.");
                    }
                } else {
                    if(message.startsWith("STEP ")) {
                        String step = message.substring("STEP ".length());
                        board = new Board(step);
                        curClientIndex = board.getStep();
                        sendStepToCurrentPlayer(curClientIndex, board.toString());
                    }
                }
            }
        }
    }

    public void startLobby() {
        if (!started) {
            started = true;
            new Thread(this).start();
        }
    }

    public void sendMessage(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public void sendStepToCurrentPlayer(int playerIndex, String step) {
        clients.get(playerIndex).sendStep(step);
    }
}
