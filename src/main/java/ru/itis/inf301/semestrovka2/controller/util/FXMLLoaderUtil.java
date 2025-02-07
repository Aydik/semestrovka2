package ru.itis.inf301.semestrovka2.controller.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

import java.io.IOException;

import ru.itis.inf301.semestrovka2.controller.pages.RootPane;

/**
 * Вспомогательный класс для управления навигацией и передачи контроллерам корневого узла приложения*/
public class FXMLLoaderUtil {

    public static void loadFXMLToPane(String fxmlPath, Pane rootPane) {

        try {

            FXMLLoader fxmlLoader = new FXMLLoader(FXMLLoaderUtil.class.getResource(fxmlPath));
            Parent scene = fxmlLoader.load();


            Object controller = fxmlLoader.getController();
            if (controller instanceof RootPane rootPaneAwareController) {
                rootPaneAwareController.setRootPane(rootPane);
            }
            rootPane.getChildren().add(scene);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML: " + fxmlPath, e);
        }

    }

}
