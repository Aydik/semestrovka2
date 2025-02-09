package ru.itis.inf301.semestrovka2.controller.pages;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import ru.itis.inf301.semestrovka2.client.Client;
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
    public void initialize() throws InterruptedException {
        Random random = new Random();
        String lobby_id = Integer.toString(random.nextInt(1000000));
        wait(lobby_id);
    }

    @FXML
    public void back() {
        clientService.disconnect();
        rootPane.getChildren().clear();
        FXMLLoaderUtil.loadFXMLToPane("/view/templates/main-menu.fxml", rootPane, null);
    }

    public void wait(String lobby_id) throws InterruptedException {
        lobbyId.setText(lobby_id);
        clientService = new ClientService(lobby_id);
        Client client = clientService.getClient();

        new Thread(() -> {
            String message;

            while (true) {
                message = client.getMessage();

                if (message == null) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    continue;
                }
                message = message.replace("MESSAGE ", "");
                System.out.println("proccesing " + message);
                if (message.trim().equals("Game started!")) {
                    System.out.println("Received started!");
                    Platform.runLater(() -> {
                        rootPane.getChildren().clear();
                        FXMLLoaderUtil.loadFXMLToPane("/view/templates/game.fxml", rootPane, clientService);
                    });

                }
                continue;
            }

        }).start();
    }


    @Override
    public void setRootPane(Pane pane) {
        this.rootPane = pane;
    }
}
