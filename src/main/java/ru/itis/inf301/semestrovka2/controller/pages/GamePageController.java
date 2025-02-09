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
import lombok.Getter;
import lombok.Setter;
import ru.itis.inf301.semestrovka2.client.Client;
import ru.itis.inf301.semestrovka2.client.ClientService;
import ru.itis.inf301.semestrovka2.controller.util.FXMLLoaderUtil;
import ru.itis.inf301.semestrovka2.model.Board;

@Getter
@Setter
public class GamePageController implements RootPane {
    private ClientService clientService;

    @FXML
    public Pane rootPane;

    @FXML
    public Text textHod;

    @FXML
    public GridPane gridPane;

    @FXML
    public void initialize() {
        renderBoard();
    }


    public void renderBoard() {
        gridPane.getChildren().clear();
        Board board = clientService.getClient().getBoard();
        textHod.setText("Ходит user" + board.getStep());
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
                    rect.setOnMouseClicked(event -> {
                        putHorizontalWall(finalRow, finalCol);
                    });
                } else if (col % 2 == 1) {
                    int finalRow = row / 2;
                    int finalCol = col / 2;
                    if (board.getVertical()[finalRow][finalCol] == 1) {
                        rect = new Rectangle(10, 60, Color.valueOf("#79553D"));
                    } else {
                        rect = new Rectangle(10, 60, Color.valueOf("#FAE7B5"));
                    }
                    rect.setOnMouseClicked(event -> {
                        putVerticalWall(finalRow, finalCol);
                    });
                } else {
                    int finalRow = row / 2;
                    int finalCol = col / 2;
                    rect = new Rectangle(60, 60, Color.valueOf("#FFCA86"));
                    if (board.getUser0_x() == finalRow && board.getUser0_y() == finalCol) {
                        circle = new Circle(30, 30, 25, Color.valueOf("#007FFF"));
                    } else if (board.getUser1_x() == finalRow && board.getUser1_y() == finalCol) {
                        circle = new Circle(30, 30, 25, Color.valueOf("#C41E3A"));
                    }
                    rect.setOnMouseClicked(event -> {
                        move(finalRow, finalCol);
                    });
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
        Client client = clientService.getClient();
        client.move(finalRow, finalCol);
    }

    public void putVerticalWall(int finalRow, int finalCol) {
        Client client = clientService.getClient();
        client.putVerticalWall(finalRow, finalCol);
    }

    public void putHorizontalWall(int finalRow, int finalCol) {
        Client client = clientService.getClient();
        client.putHorizontalWall(finalRow, finalCol);
    }

    public void startGame() {
        Client client = clientService.getClient();
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }

                renderBoard();
            }

        }).start();
    }

    public void redirectToWinPage(int result) {
        System.out.println(result);
        rootPane.getChildren().clear();
        FXMLLoaderUtil.loadFXMLToPane("/view/templates/main-menu.fxml", rootPane, clientService);
    }


    @Override
    public void setRootPane(Pane pane) {
        this.rootPane = pane;
    }
}
