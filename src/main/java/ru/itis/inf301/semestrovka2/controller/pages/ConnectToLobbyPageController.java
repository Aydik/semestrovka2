package ru.itis.inf301.semestrovka2.controller.pages;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Pane;
import ru.itis.inf301.semestrovka2.client.Client;
import ru.itis.inf301.semestrovka2.client.ClientService;
import ru.itis.inf301.semestrovka2.controller.util.FXMLLoaderUtil;

import java.util.function.UnaryOperator;

public class ConnectToLobbyPageController implements RootPane {

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

        rootPane.getChildren().clear();
        FXMLLoaderUtil.loadFXMLToPane("/view/templates/main-menu.fxml", rootPane);
    }

    @FXML
    public void connect() {

        String lobbyId = textField.getText();
        if (Integer.parseInt(lobbyId) <= 1000000) {
            ClientService clientService = new ClientService(lobbyId);
            rootPane.getChildren().clear();
            FXMLLoaderUtil.loadFXMLToPane("/view/templates/lobby.fxml", rootPane, clientService);
        }
        // else вывести ошибку

        // создаем перейти в лобби с параметром textField.getText()


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
