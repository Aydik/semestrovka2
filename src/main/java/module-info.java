module ru.itis.inf301.semestrovka2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens ru.itis.inf301.semestrovka2.controller.pages to javafx.fxml;
    opens ru.itis.inf301.semestrovka2 to javafx.fxml;
    exports ru.itis.inf301.semestrovka2;
}