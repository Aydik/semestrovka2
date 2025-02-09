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

    public GamePageController() { }

    public GamePageController(ClientService clientService) {
        this.clientService = clientService;
    }

    @FXML
    public void initialize() {
        if (clientService != null && clientService.isConnectedToLobby()) {
            board = clientService.getBoard();
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
                    rect = new Rectangle(60, 10, board.getHorizontal()[finalRow][finalCol] == 1
                            ? Color.valueOf("#79553D")
                            : Color.valueOf("#FAE7B5"));
                    rect.setOnMouseClicked(event -> putHorizontalWall(finalRow, finalCol));
                } else if (col % 2 == 1) {
                    int finalRow = row / 2;
                    int finalCol = col / 2;
                    rect = new Rectangle(10, 60, board.getVertical()[finalRow][finalCol] == 1
                            ? Color.valueOf("#79553D")
                            : Color.valueOf("#FAE7B5"));
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
        if (board.move(board.getHod(), finalRow, finalCol)) {
            clientService.getClient().addMessage("MOVE " + finalRow + " " + finalCol + " " + board.getHod());
            renderBoard();
        }
    }

    public void putVerticalWall(int finalRow, int finalCol) {
        if (clientService == null || clientService.getClient() == null) {
            System.err.println("ClientService is not initialized!");
            return;
        }
        if (board.putVerticalWall(board.getHod(), finalRow, finalCol)) {
            clientService.getClient().addMessage("VERTICAL_WALL " + finalRow + " " + finalCol + " " + board.getHod());
            renderBoard();
        }
    }

    public void putHorizontalWall(int finalRow, int finalCol) {
        if (clientService == null || clientService.getClient() == null) {
            System.err.println("ClientService is not initialized!");
            return;
        }
        if (board.putHorizontalWall(board.getHod(), finalRow, finalCol)) {
            clientService.getClient().addMessage("HORIZONTAL_WALL " + finalRow + " " + finalCol + " " + board.getHod());
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
                    message = message.replace("[Server]: ", "");
                    System.out.println("Received message: " + message);
                    if (message.startsWith("MOVE")) {
                        String[] parts = message.split(" ");
                        int x = Integer.parseInt(parts[1]);
                        int y = Integer.parseInt(parts[2]);
                        int player = Integer.parseInt(parts[3]);
                        clientService.getBoard().doMove(player, x, y);
                        Platform.runLater(this::renderBoard);
                    } else if (message.startsWith("VERTICAL_WALL")) {
                        String[] parts = message.split(" ");
                        int x = Integer.parseInt(parts[1]);
                        int y = Integer.parseInt(parts[2]);
                        int player = Integer.parseInt(parts[3]);
                        clientService.getBoard().putVerticalWall(player, x, y);
                        Platform.runLater(this::renderBoard);
                    } else if (message.startsWith("HORIZONTAL_WALL")) {
                        String[] parts = message.split(" ");
                        int x = Integer.parseInt(parts[1]);
                        int y = Integer.parseInt(parts[2]);
                        int player = Integer.parseInt(parts[3]);
                        clientService.getBoard().putHorizontalWall(player, x, y);
                        Platform.runLater(this::renderBoard);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
