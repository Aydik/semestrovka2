package ru.itis.inf301.semestrovka2.controller.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import java.io.IOException;
import java.util.Optional;
import ru.itis.inf301.semestrovka2.client.ClientService;
import ru.itis.inf301.semestrovka2.controller.pages.GamePageController;
import ru.itis.inf301.semestrovka2.controller.pages.RootPane;

public class FXMLLoaderUtil {
    public static void loadFXMLToPane(String fxmlPath, Pane rootPane, Optional<ClientService> clientService) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(FXMLLoaderUtil.class.getResource(fxmlPath));
            if (fxmlLoader.getLocation() == null) {
                System.err.println("FXML file not found: " + fxmlPath);
                return;
            }

            // Используем контроллер-фабрику для передачи clientService
            fxmlLoader.setControllerFactory(param -> {
                if (param.equals(GamePageController.class) && clientService.isPresent()) {
                    GamePageController controller = new GamePageController();
                    controller.setClientService(clientService.get());  // Передаем clientService
                    return controller;
                }
                try {
                    return param.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            });

            // Загружаем FXML
            Parent scene = fxmlLoader.load();

            // Получаем контроллер из FXML
            Object controller = fxmlLoader.getController();

            // Устанавливаем rootPane в контроллер, если это нужно
            if (controller instanceof RootPane rootPaneAwareController) {
                rootPaneAwareController.setRootPane(rootPane);
            }

            // Очищаем и добавляем новый элемент в rootPane
            rootPane.getChildren().clear();
            rootPane.getChildren().add(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
