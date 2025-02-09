package ru.itis.inf301.semestrovka2.controller.pages;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Pane;
import ru.itis.inf301.semestrovka2.client.Client;
import ru.itis.inf301.semestrovka2.client.ClientService;
import ru.itis.inf301.semestrovka2.controller.util.FXMLLoaderUtil;
import ru.itis.inf301.semestrovka2.server.Server;

import java.util.Optional;
import java.util.function.UnaryOperator;

public class ConnectToLobbyPageController implements RootPane {
    private ClientService clientService;
    private Client client;
    @FXML
    public Pane rootPane;
    @FXML
    public TextField textField;

    @FXML
    public void initialize() {
        setupNumericField();
    }

    @FXML
    public void back() {
        if (clientService != null) {
            clientService.disconnect();
        }
        rootPane.getChildren().clear();
        FXMLLoaderUtil.loadFXMLToPane("/view/templates/main-menu.fxml", rootPane, Optional.empty());
    }

    @FXML
    public void connect() {
        System.out.println(textField.getText());
        int lobbyId = Integer.parseInt(textField.getText());

        if (lobbyId <= 1000000) {
            // Проверяем, подключен ли уже клиент
            if (clientService == null || !clientService.isConnectedToLobby()) {
                // Инициализируем клиента и пытаемся подключиться
                client = new Client();
                clientService = new ClientService(client);
                // Создаем подключение в фоновом потоке
                new Thread(() -> {
                    clientService.connect(Integer.toString(lobbyId), false);
                    if (client.getClientSocket() == null) {
                        Platform.runLater(() -> {
                            System.err.println("Failed to connect to the server.");
                        });
                        return;
                    }
                    Platform.runLater(() -> {
                        System.out.println(clientService.isConnectedToLobby());
                        rootPane.getChildren().clear(); // Очистка текущего экрана
                        FXMLLoaderUtil.loadFXMLToPane("/view/templates/game.fxml", rootPane, Optional.of(clientService));
                    });
                }).start();
            } else {
                System.err.println("Already connected to a lobby.");
            }
        } else {
            System.out.println("слишком большое число лобби");
        }
    }

    @Override
    public void setRootPane(Pane pane) {
        this.rootPane = pane;
    }

    public void setupNumericField() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) { // Разрешены только цифры
                return change;
            }
            return null; // Отменяет ввод
        };
        textField.setTextFormatter(new TextFormatter<>(filter));
    }
}