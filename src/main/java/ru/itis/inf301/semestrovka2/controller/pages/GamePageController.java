package ru.itis.inf301.semestrovka2.controller.pages;

import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import ru.itis.inf301.semestrovka2.client.ClientService;
import ru.itis.inf301.semestrovka2.controller.util.FXMLLoaderUtil;
import ru.itis.inf301.semestrovka2.model.Board;

public class GamePageController implements RootPane {
    private Board board;
    // ClientService clientService;

    @FXML
    public Pane rootPane;

    @FXML
    public Text textHod;

    @FXML
    public GridPane gridPane;

    @FXML
    public void initialize() {
        board = new Board();
        renderBoard();
    }

    public void renderBoard() {
        gridPane.getChildren().clear();
        textHod.setText("Ходит user" + board.getHod());
        for (int row = 0; row < 17; row++) {
            for (int col = 0; col < 17; col++) {
                Rectangle rect;
                Circle circle = null;
                if (row % 2 == 1 && col % 2 == 1) {
                    rect = new Rectangle(10, 10, Color.valueOf("#FAE7B5"));
                }
                else if (row % 2 == 1){
                    int finalRow = row / 2;
                    int finalCol = col / 2;
                    if (board.getHorizontal()[finalRow][finalCol] == 1){
                        rect = new Rectangle(60, 10, Color.valueOf("#79553D"));
                    } else {
                        rect = new Rectangle(60, 10, Color.valueOf("#FAE7B5"));
                    }
                    rect.setOnMouseClicked(event -> {
                        putHorizontalWall(finalRow, finalCol);
                    });
                }
                else if (col % 2 == 1){
                    int finalRow = row / 2;
                    int finalCol = col / 2;
                    if (board.getVertical()[finalRow][finalCol] == 1){
                        rect = new Rectangle(10, 60, Color.valueOf("#79553D"));
                    } else {
                        rect = new Rectangle(10, 60, Color.valueOf("#FAE7B5"));
                    }
                    rect.setOnMouseClicked(event -> {
                        putVerticalWall(finalRow, finalCol);
                    });
                }
                else {
                    int finalRow = row / 2;
                    int finalCol = col / 2;
                    rect = new Rectangle(60, 60, Color.valueOf("#FFCA86"));
                    if (board.getUser0_x() == finalRow && board.getUser0_y() == finalCol){
                        circle = new Circle(30, 30, 25, Color.valueOf("#007FFF"));
                    } else if (board.getUser1_x() == finalRow && board.getUser1_y() == finalCol){
                        circle = new Circle(30, 30, 25, Color.valueOf("#C41E3A"));
                    }
                    rect.setOnMouseClicked(event -> {
                        move(finalRow, finalCol);
                    });
                }
                gridPane.add(rect, col, row);
                if(circle != null) {
                    gridPane.add(circle, col, row);
                    GridPane.setHalignment(circle, HPos.CENTER);
                    GridPane.setValignment(circle, VPos.CENTER);
                }
            }
        }
    }

    public void move(int finalRow, int finalCol) {
        // board.getHod() заменить на user
        System.out.println("cell " + finalRow + " " + finalCol);
        if(board.move(board.getHod(), finalRow, finalCol)){
            // закинуть на сервак этот ход
            if(board.checkResult() != -1 ){
                redirectToWinPage(board.checkResult());
            } else renderBoard();
        }
    }

    public void putVerticalWall(int finalRow, int finalCol) {
        System.out.println("vertical wall " + finalRow + " " + finalCol);
        if(board.putVerticalWall(board.getHod(), finalRow, finalCol)){
            if(board.checkResult() != -1 ){
                redirectToWinPage(board.checkResult());
            } else renderBoard();
        }
    }

    public void putHorizontalWall(int finalRow, int finalCol) {
        System.out.println("horizontal wall " + finalRow + " " + finalCol);
        if(board.putHorizontalWall(board.getHod(), finalRow, finalCol)){
            if(board.checkResult() != -1 ){
                redirectToWinPage(board.checkResult());
            } else renderBoard();
        }
    }

    public void redirectToWinPage(int result){
        System.out.println(result);
        rootPane.getChildren().clear();
        FXMLLoaderUtil.loadFXMLToPane("/view/templates/main-menu.fxml", rootPane);
    }

    /* создать метод вызывающий ожидание хода соперника
        обновляет board
        if(board.checkResult() != -1 ){
                    redirectToWinPage(board.checkResult());
                } else renderBoard();
    */


    @Override
    public void setRootPane(Pane pane) {
        this.rootPane = pane;
    }
}
