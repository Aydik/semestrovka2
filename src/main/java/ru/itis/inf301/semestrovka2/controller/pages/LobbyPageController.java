package ru.itis.inf301.semestrovka2.controller.pages;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import ru.itis.inf301.semestrovka2.client.ClientService;
import ru.itis.inf301.semestrovka2.controller.util.FXMLLoaderUtil;
import ru.itis.inf301.semestrovka2.server.Lobby;
import ru.itis.inf301.semestrovka2.server.Server;

import java.util.Random;

public class LobbyPageController implements RootPane {
    ClientService clientService;

    @FXML
    public Pane rootPane;

    @FXML
    public Text lobbyId;

    @FXML
    public void initialize() throws InterruptedException {
        Random random = new Random();
        String lobby_id = Integer.toString(random.nextInt(1000000));
        wait(lobby_id);
    }

    @FXML
    public void back() {
        clientService.disconnect();
        rootPane.getChildren().clear();
        FXMLLoaderUtil.loadFXMLToPane("/view/templates/main-menu.fxml", rootPane);
    }

    public void wait(String lobby_id) throws InterruptedException {
        lobbyId.setText(lobby_id);
        clientService = new ClientService(lobby_id);
        Lobby lobby = new Lobby(Integer.parseInt(lobby_id));
        Server.getLobbies().add(lobby);
        lobby.addClient(clientService.getClient());
        while (!lobby.isStarted()) {
            Thread.sleep(1000);
        }
        lobby.startLobby();
        // ждать подключения второго игрока

         rootPane.getChildren().clear();
         FXMLLoaderUtil.loadFXMLToPane("/view/templates/game.fxml", rootPane);
    }


    @Override
    public void setRootPane(Pane pane) {
        this.rootPane = pane;
    }
}
