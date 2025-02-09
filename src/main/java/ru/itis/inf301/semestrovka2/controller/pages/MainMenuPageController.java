package ru.itis.inf301.semestrovka2.controller.pages;

import javafx.fxml.FXML;

import javafx.scene.layout.Pane;
import ru.itis.inf301.semestrovka2.client.ClientService;
import ru.itis.inf301.semestrovka2.controller.util.FXMLLoaderUtil;

import java.util.Random;

public class MainMenuPageController implements RootPane {
    private Pane rootPane;

    @FXML
    private void createLobby() {
        ClientService clientService = new ClientService();
        rootPane.getChildren().clear();
        FXMLLoaderUtil.loadFXMLToPane("/view/templates/lobby.fxml", rootPane, clientService);
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