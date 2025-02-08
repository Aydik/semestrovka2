package ru.itis.inf301.semestrovka2.controller.pages;

import javafx.fxml.FXML;

import javafx.scene.layout.Pane;
import ru.itis.inf301.semestrovka2.controller.util.FXMLLoaderUtil;

public class MainMenuPageController implements RootPane {
    private Pane rootPane;

    @FXML
    private void createLobby() {
        rootPane.getChildren().clear();
        FXMLLoaderUtil.loadFXMLToPane("/view/templates/lobby.fxml", rootPane);
    }

    @FXML
    private void connectToLobby() {
        rootPane.getChildren().clear();
        FXMLLoaderUtil.loadFXMLToPane("/view/templates/connect-to-lobby.fxml", rootPane);
    }

    @Override
    public void setRootPane(Pane pane) {
        this.rootPane = pane;
    }

}