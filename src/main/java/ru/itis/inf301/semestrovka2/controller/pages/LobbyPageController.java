package ru.itis.inf301.semestrovka2.controller.pages;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import ru.itis.inf301.semestrovka2.client.ClientService;
import ru.itis.inf301.semestrovka2.controller.util.FXMLLoaderUtil;

import java.util.Random;

public class LobbyPageController implements RootPane {
    ClientService clientService;

    @FXML
    public Pane rootPane;
    @FXML
    public Text lobbyId;

    @FXML
    public void initialize() {
        Random random = new Random();
        String lobby_id = Integer.toString(random.nextInt(1000000));
        lobbyId.setText(lobby_id);
        clientService = new ClientService(lobby_id);
    }

    @FXML
    public void back() {
        clientService.disconnect();
        rootPane.getChildren().clear();
        FXMLLoaderUtil.loadFXMLToPane("/view/templates/main-menu.fxml", rootPane);
    }


    @Override
    public void setRootPane(Pane pane) {
        this.rootPane = pane;
    }
}
