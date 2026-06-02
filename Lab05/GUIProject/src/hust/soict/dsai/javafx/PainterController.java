package hust.soict.dsai.javafx;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class PainterController {

    @FXML
    private Pane drawingAreaPane;

    @FXML
    void drawingAreaMouseDragged(MouseEvent event) {

        Circle newCircle = new Circle(
                event.getX(),
                event.getY(),
                4,
                Color.BLACK);

        drawingAreaPane.getChildren().add(newCircle);
    }

    @FXML
    void clearButtonPressed() {

        drawingAreaPane
                .getChildren()
                .clear();
    }
}