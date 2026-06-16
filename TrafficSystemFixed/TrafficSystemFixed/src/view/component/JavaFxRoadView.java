package view.component;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class JavaFxRoadView {

    private final double width;

    private final double height;

    public JavaFxRoadView(double width,
                          double height) {

        this.width = width;
        this.height = height;
    }

    public void render(GraphicsContext graphics) {

        graphics.setFill(Color.DARKGRAY);
        graphics.fillRect(0, 280, width, 140);
        graphics.fillRect(380, 0, 140, height);

        graphics.setStroke(Color.WHITE);
        graphics.setLineWidth(2);
        graphics.setLineDashes(18);
        graphics.strokeLine(0, 350, width, 350);
        graphics.strokeLine(450, 0, 450, height);
        graphics.setLineDashes(null);
    }
}
