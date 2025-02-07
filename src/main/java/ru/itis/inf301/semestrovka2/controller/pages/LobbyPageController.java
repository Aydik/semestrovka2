package ru.itis.inf301.semestrovka2.controller.pages;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import ru.itis.inf301.semestrovka2.controller.util.FXMLLoaderUtil;

public class LobbyPageController implements RootPane {

    @FXML
    public Pane rootPane;

    @FXML
    public void initialize() {

    }

    @FXML
    public void back() {
        rootPane.getChildren().clear();
        FXMLLoaderUtil.loadFXMLToPane("/view/templates/start-game.fxml", rootPane);
    }


    @Override
    public void setRootPane(Pane pane) {
        this.rootPane = pane;
    }
}
