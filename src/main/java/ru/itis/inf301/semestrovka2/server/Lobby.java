package ru.itis.inf301.semestrovka2.server;

import lombok.Getter;
import ru.itis.inf301.semestrovka2.client.ClientService;
import ru.itis.inf301.semestrovka2.model.Board;

import java.util.ArrayList;
import java.util.List;

public class Lobby implements Runnable {
    @Getter
    private final int id;
    @Getter
    private final List<ClientHandler> clients;
    private volatile boolean started;
    @Getter
    private final Board board;

    public Lobby(int id) {
        this.id = id;
        this.clients = new ArrayList<>();
        this.started = false;
        this.board = new Board();
    }

    public synchronized void addClient(ClientHandler clientHandler) {
        if (clients.size() < 2) {
            clients.add(clientHandler);
            clientHandler.sendMessage("Waiting for another player...");
            if (clients.size() == 2) {
                startLobby();
            }
        } else {
            clientHandler.sendMessage("Lobby is full.");
        }
    }

    public void startLobby() {
        if (!started) {
            started = true;
//            sendMessage("GAME_START");
            for (ClientHandler clientHandler : clients) {
                ClientService clientService = clientHandler.getClientService();
                if (clientService != null) {
                    clientService.setBoard(board);
                }
            }
            new Thread(this).start();
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
        if (started) {
            sendMessage("Game started!");
            int curClientIndex = 0;
            while (!clients.isEmpty()) {
                curClientIndex = board.getHod();
                sendMessage("Очередь игрока " + (curClientIndex));
                sendMessageToCurrentPlayer(curClientIndex, "Ваш ход");
                try {
                    String message = clients.get(curClientIndex).getMessage();
                    if (message == null || message.trim().isEmpty() || message.equalsIgnoreCase("exit")) {
                        // Обработка выхода игрока
                        clients.get(curClientIndex).sendMessage("Вы покинули игру.");
                        removeClient(clients.get(curClientIndex));  // Убираем игрока из лобби
                        if (clients.isEmpty()) {
                            sendMessage("Игра завершена. Все игроки покинули лобби.");
                            break;
                        } else {
                            sendMessage("Игрок " + (curClientIndex + 1) + " покинул игру. Осталось " + clients.size() + " игроков.");
                        }
                    } else {
                        sendMessage("Игрок " + (curClientIndex + 1) + ": " + message);
                        curClientIndex = (curClientIndex + 1) % clients.size();  // Переводим очередь на следующего игрока
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendMessage(String message) {
        for (ClientHandler client : clients) {
            System.out.println("Sending message to client: " + message);
            client.sendMessage(message);
        }
    }

    public void sendMessageToCurrentPlayer(int playerIndex, String message) {
        clients.get(playerIndex).sendMessage(message);
    }

    public int getHod() {
        return board.getHod(); // Получаем текущий ход из игрового поля
    }

    public Board getBoard() {
        return board;
    }
}