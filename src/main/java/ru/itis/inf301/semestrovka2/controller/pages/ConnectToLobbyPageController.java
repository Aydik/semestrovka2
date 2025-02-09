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
        FXMLLoaderUtil.loadFXMLToPane("/view/templates/main-menu.fxml", rootPane, null);
    }

    @FXML
    public void connect() {
        System.out.println(textField.getText());
        // условие, что не больше 1000000
        int lobbyId = Integer.parseInt(textField.getText());
        if (lobbyId <= 1000000) {
            Client client = new Client();
            ClientService clientService = new ClientService(Integer.toString(lobbyId));
            rootPane.getChildren().clear();
            FXMLLoaderUtil.loadFXMLToPane("/view/templates/game.fxml", rootPane, clientService);
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
