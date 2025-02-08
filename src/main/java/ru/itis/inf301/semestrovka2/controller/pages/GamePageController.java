package ru.itis.inf301.semestrovka2.controller.pages;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import ru.itis.inf301.semestrovka2.client.ClientService;
import ru.itis.inf301.semestrovka2.controller.util.FXMLLoaderUtil;

public class GamePageController implements RootPane {
    ClientService clientService;


    @FXML
    public Pane rootPane;
    @FXML
    public Text lobbyId;

    @FXML
    public void initialize(ClientService clientService) {
        this.clientService = clientService;
    }


    @Override
    public void setRootPane(Pane pane) {
        this.rootPane = pane;
    }
}
