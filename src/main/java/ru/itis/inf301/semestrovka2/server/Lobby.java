package ru.itis.inf301.semestrovka2.server;

import lombok.Getter;
import ru.itis.inf301.semestrovka2.model.Board;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Lobby implements Runnable {
    private final int id;
    private final List<ClientHandler> clients;
    private volatile boolean started;
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
            // Передаем общее игровое поле всем клиентам через их ClientService
            for (ClientHandler ch : clients) {
                if (ch.getClientService() != null) {
                    ch.getClientService().setBoard(board);
                }
            }
            // Запускаем игровой процесс в отдельном потоке
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
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            while (!clients.isEmpty()) {
                curClientIndex = board.getHod();
                sendMessage("Очередь игрока " + (curClientIndex));
                sendMessageToCurrentPlayer(curClientIndex, "Ваш ход");
                try {
                    String message = clients.get(curClientIndex).getMessage();
                    if (message == null || message.trim().isEmpty() || message.equalsIgnoreCase("exit")) {
                        // Обработка выхода игрока
                        clients.get(curClientIndex).sendMessage("Вы покинули игру.");
                        removeClient(clients.get(curClientIndex));
                        if (clients.isEmpty()) {
                            sendMessage("Игра завершена. Все игроки покинули лобби.");
                            break;
                        } else {
                            sendMessage("Игрок " + (curClientIndex + 1) + " покинул игру. Осталось " + clients.size() + " игроков.");
                        }
                    } else {
                        sendMessage("Игрок " + (curClientIndex + 1) + ": " + message);
                        curClientIndex = (curClientIndex + 1) % clients.size();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendMessage(String message) {
        System.out.println("Sending message to " + clients.size() + " clients: " + message);
        for (ClientHandler ch : clients) {
            // Отправляем сообщение через ClientService объекта клиента
            if (ch.getClientService() != null && ch.getClientService().getClient() != null) {
                ch.getClientService().getClient().addMessage(message);
            }
        }
    }

    public void sendMessageToCurrentPlayer(int playerIndex, String message) {
        clients.get(playerIndex).sendMessage(message);
    }
    public int getHod() {
        return board.getHod();
    }
}
