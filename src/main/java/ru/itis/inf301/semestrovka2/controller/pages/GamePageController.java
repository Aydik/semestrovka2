package ru.itis.inf301.semestrovka2.controller.pages;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import lombok.Setter;
import ru.itis.inf301.semestrovka2.client.ClientService;
import ru.itis.inf301.semestrovka2.model.Board;
import ru.itis.inf301.semestrovka2.server.ClientHandler;
import ru.itis.inf301.semestrovka2.server.Lobby;
import ru.itis.inf301.semestrovka2.server.Server;

public class GamePageController implements RootPane {
    @Setter
    private ClientService clientService;
    @FXML
    public Pane rootPane;
    @FXML
    public Text textHod;
    @FXML
    public GridPane gridPane;
    private Board board;
    public GamePageController() {
        // Конструктор по умолчанию
    }
    public GamePageController(ClientService clientService) {
        this.clientService = clientService;
    }

    @FXML
    public void initialize() {
        if (clientService != null && clientService.isConnectedToLobby()) {
            board = clientService.getBoard(); // Используем переданный Board из ClientService
            if (board == null) {
                System.err.println("Board is not initialized in initialize()");
                return;
            }
            renderBoard();
            startListeningForOpponentMoves();
        } else {
            System.err.println("ClientService is not initialized or not connected to lobby in initialize()");
        }
    }

    public void renderBoard() {
        gridPane.getChildren().clear();
        textHod.setText("Ходит player " + board.getHod());
        for (int row = 0; row < 17; row++) {
            for (int col = 0; col < 17; col++) {
                Rectangle rect;
                Circle circle = null;
                if (row % 2 == 1 && col % 2 == 1) {
                    rect = new Rectangle(10, 10, Color.valueOf("#FAE7B5"));
                } else if (row % 2 == 1) {
                    int finalRow = row / 2;
                    int finalCol = col / 2;
                    if (board.getHorizontal()[finalRow][finalCol] == 1) {
                        rect = new Rectangle(60, 10, Color.valueOf("#79553D"));
                    } else {
                        rect = new Rectangle(60, 10, Color.valueOf("#FAE7B5"));
                    }
                    rect.setOnMouseClicked(event -> putHorizontalWall(finalRow, finalCol));
                } else if (col % 2 == 1) {
                    int finalRow = row / 2;
                    int finalCol = col / 2;
                    if (board.getVertical()[finalRow][finalCol] == 1) {
                        rect = new Rectangle(10, 60, Color.valueOf("#79553D"));
                    } else {
                        rect = new Rectangle(10, 60, Color.valueOf("#FAE7B5"));
                    }
                    rect.setOnMouseClicked(event -> putVerticalWall(finalRow, finalCol));
                } else {
                    int finalRow = row / 2;
                    int finalCol = col / 2;
                    rect = new Rectangle(60, 60, Color.valueOf("#FFCA86"));
                    if (board.getUser0_x() == finalRow && board.getUser0_y() == finalCol) {
                        circle = new Circle(30, 30, 25, Color.valueOf("#007FFF"));
                    } else if (board.getUser1_x() == finalRow && board.getUser1_y() == finalCol) {
                        circle = new Circle(30, 30, 25, Color.valueOf("#C41E3A"));
                    }
                    rect.setOnMouseClicked(event -> move(finalRow, finalCol));
                }
                gridPane.add(rect, col, row);
                if (circle != null) {
                    gridPane.add(circle, col, row);
                    GridPane.setHalignment(circle, HPos.CENTER);
                    GridPane.setValignment(circle, VPos.CENTER);
                }
            }
        }
    }

    public void move(int finalRow, int finalCol) {
        if (clientService == null || clientService.getClient() == null) {
            System.err.println("ClientService is not initialized!");
            return;
        }
        if (board.getHod() != getPlayerNumber(clientService)) {
            System.out.println("It's not your turn yet!");
            return;
        }
        if (board.move(board.getHod(), finalRow, finalCol)) {
            clientService.getClient().sendMessage("MOVE " + finalRow + " " + finalCol + " " + board.getHod());
            renderBoard();
        }
    }

    public void putVerticalWall(int finalRow, int finalCol) {
        if (clientService == null || clientService.getClient() == null) {
            System.err.println("ClientService is not initialized!");
            return;
        }
        if (board.getHod() != getPlayerNumber(clientService)) {
            System.out.println("It's not your turn yet!");
            return;
        }
        if (board.putVerticalWall(board.getHod(), finalRow, finalCol)) {
            clientService.getClient().sendMessage("VERTICAL_WALL " + finalRow + " " + finalCol + " " + board.getHod());
            renderBoard();
        }
    }

    public void putHorizontalWall(int finalRow, int finalCol) {
        if (clientService == null || clientService.getClient() == null) {
            System.err.println("ClientService is not initialized!");
            return;
        }
        if (board.getHod() != getPlayerNumber(clientService)) {
            System.out.println("It's not your turn yet!");
            return;
        }
        if (board.putHorizontalWall(board.getHod(), finalRow, finalCol)) {
            clientService.getClient().sendMessage("HORIZONTAL_WALL " + finalRow + " " + finalCol + " " + board.getHod());
            renderBoard();
        }
    }

    @Override
    public void setRootPane(Pane pane) {
        this.rootPane = pane;
    }

    private void startListeningForOpponentMoves() {
        if (clientService == null || clientService.getClient() == null) {
            System.err.println("ClientService is not initialized!");
            return;
        }
        new Thread(() -> {
            try {
                while (true) {
                    String message = clientService.getClient().readMessage();
                    if (message == null) continue;
                    // Убираем префикс "[Server]: " из сообщения
                    message = message.replace("[Server]: ", "");
                    System.out.println("Received message: " + message); // Отладочное сообщение
                    if (message.startsWith("MOVE")) {
                        String[] parts = message.split(" ");
                        int x = Integer.parseInt(parts[1]);
                        int y = Integer.parseInt(parts[2]);
                        int player = Integer.parseInt(parts[3]); // Получаем номер игрока
                        clientService.getBoard().doMove(player, x, y); // Обновляем состояние доски
                        Platform.runLater(() -> {
                            System.out.println("Rendering board after MOVE...");
                            renderBoard(); // Отрисовываем доску
                        });
                    } else if (message.startsWith("VERTICAL_WALL")) {
                        String[] parts = message.split(" ");
                        int x = Integer.parseInt(parts[1]);
                        int y = Integer.parseInt(parts[2]);
                        int player = Integer.parseInt(parts[3]); // Получаем номер игрока
                        clientService.getBoard().putVerticalWall(player, x, y);
                        Platform.runLater(() -> {
                            System.out.println("Rendering board after VERTICAL_WALL...");
                            renderBoard();
                        });
                    } else if (message.startsWith("HORIZONTAL_WALL")) {
                        String[] parts = message.split(" ");
                        int x = Integer.parseInt(parts[1]);
                        int y = Integer.parseInt(parts[2]);
                        int player = Integer.parseInt(parts[3]); // Получаем номер игрока
                        clientService.getBoard().putHorizontalWall(player, x, y);
                        Platform.runLater(() -> {
                            System.out.println("Rendering board after HORIZONTAL_WALL...");
                            renderBoard();
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    private int getPlayerNumber(ClientService clientService) {
        int lobbyId = clientService.getClient().getLobbyId();
        Lobby lobby = Server.findLobbyById(lobbyId);
        for (ClientHandler clientHandler : lobby.getClients()) {
            if (clientHandler.getClientService().equals(clientService)) {
                return lobby.getClients().indexOf(clientHandler);
            }
        }
        return 0;
    }

}
