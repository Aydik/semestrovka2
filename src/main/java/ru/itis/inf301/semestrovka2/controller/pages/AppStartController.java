package ru.itis.inf301.semestrovka2.controller.pages;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import ru.itis.inf301.semestrovka2.controller.util.FXMLLoaderUtil;

public class AppStartController implements RootPane {
    @FXML
    public Pane rootPane;

    @FXML
    public void initialize() {
        FXMLLoaderUtil.loadFXMLToPane("/view/templates/main-menu.fxml", rootPane, java.util.Optional.empty());
    }

    @Override
    public void setRootPane(Pane pane) {
        this.rootPane = pane;
    }
}
