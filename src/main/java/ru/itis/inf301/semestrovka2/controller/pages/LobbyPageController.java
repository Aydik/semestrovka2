package ru.itis.inf301.semestrovka2.controller.pages;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import ru.itis.inf301.semestrovka2.client.Client;
import ru.itis.inf301.semestrovka2.client.ClientService;
import ru.itis.inf301.semestrovka2.controller.util.FXMLLoaderUtil;

import static java.lang.Thread.sleep;


public class LobbyPageController implements RootPane {
    private ClientService clientService;

    @FXML
    public Pane rootPane;

    @FXML
    public Text lobbyId;

    @FXML
    public void initialize() {
    }

    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
        lobbyId.setText(Integer.toString(clientService.getLobby_id()));
        waitConnection();
    }

    @FXML
    public void back() {
        clientService.disconnect();
        rootPane.getChildren().clear();
        FXMLLoaderUtil.loadFXMLToPane("/view/templates/main-menu.fxml", rootPane);
    }


    public void waitConnection() {
        Client client = clientService.getClient();
        new Thread(() -> {
            String message;

            while (true) {
                message = client.getMessage();

                if (message == null) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        System.out.println(e);
                    }
                    continue;
                }
                message = message.replace("MESSAGE ", "");
                if (message.startsWith("You have joined the lobby")) {
                    String[] messageArr = message.split(" ");
                    client.setClient_index(Integer.parseInt(messageArr[messageArr.length - 1]));
                }
                if (message.trim().equals("Game started!")) {
                    System.out.println("Received started!");
                    System.out.println(clientService.getLobby_id());
                    Platform.runLater(() -> {
                        rootPane.getChildren().clear();
                        FXMLLoaderUtil.loadFXMLToPane("/view/templates/game.fxml", rootPane, clientService);
                    });

                }
            }

        }).start();
    }


    @Override
    public void setRootPane(Pane pane) {
        this.rootPane = pane;
    }
}
