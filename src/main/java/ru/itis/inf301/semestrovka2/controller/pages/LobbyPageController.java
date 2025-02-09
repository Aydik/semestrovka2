package ru.itis.inf301.semestrovka2.controller.pages;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import ru.itis.inf301.semestrovka2.client.Client;
import ru.itis.inf301.semestrovka2.client.ClientService;
import ru.itis.inf301.semestrovka2.controller.util.FXMLLoaderUtil;
import ru.itis.inf301.semestrovka2.model.Board;

import java.util.Optional;
import java.util.Random;

public class LobbyPageController implements RootPane {
    private ClientService clientService;
    private Client client;
    @FXML
    public Pane rootPane;
    @FXML
    public Text lobbyId;

    @FXML
    public void initialize() {
        Random random = new Random();
        String lobby_id = Integer.toString(random.nextInt(1000000));
        wait(lobby_id);
    }

    @FXML
    public void back() {
        if (clientService != null) {
            clientService.disconnect();
        }
        rootPane.getChildren().clear();
        FXMLLoaderUtil.loadFXMLToPane("/view/templates/main-menu.fxml", rootPane, Optional.empty());
    }

    public void wait(String lobby_id) {
        lobbyId.setText(lobby_id);
        client = new Client();
        clientService = new ClientService(client);
        clientService.connect(lobby_id, true);
        if (client.getClientSocket() == null) {
            System.err.println("Failed to connect to the server.");
            return;
        }
        new Thread(() -> {
            try {
                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < 60000) { // 60 секунд
                    String response = clientService.getClient().readMessage();
                    if (response == null) continue;
                    System.out.println("Processing message: '" + response + "'");
                    if (response.trim().equals("Game started!")) {
                        System.out.println("Received GAME_START message");
                        Platform.runLater(() -> {
                            System.out.println("Switching to game screen...");
                            System.out.println(clientService.isConnectedToLobby());
                            rootPane.getChildren().clear(); // Очистка текущего экрана
                            FXMLLoaderUtil.loadFXMLToPane("/view/templates/game.fxml", rootPane, Optional.of(clientService));
                        });
                        return; // Завершаем поток после перехода на экран игры
                    }
                }
                // Если время истекло
                Platform.runLater(() -> {
                    System.out.println("Timeout expired, returning to main menu...");
                    rootPane.getChildren().clear();
                    FXMLLoaderUtil.loadFXMLToPane("/view/templates/main-menu.fxml", rootPane, Optional.empty());
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void setRootPane(Pane pane) {
        this.rootPane = pane;
    }

    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
        System.out.println("ClientService initialized: " + (this.clientService != null));
        // Инициализация прослушивания ходов после установки ClientService

    }
}
