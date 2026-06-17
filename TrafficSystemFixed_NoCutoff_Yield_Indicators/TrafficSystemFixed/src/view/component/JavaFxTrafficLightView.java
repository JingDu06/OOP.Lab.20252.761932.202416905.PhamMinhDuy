package view.component;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.trafficlight.TrafficLight;

public class JavaFxTrafficLightView {

    private static final double WIDTH = 42;
    private static final double HEIGHT = 112;

    private final TrafficLight trafficLight;

    public JavaFxTrafficLightView(TrafficLight trafficLight) {

        this.trafficLight = trafficLight;
    }

    public void render(GraphicsContext graphics,
                       double x,
                       double y) {
        render(graphics, x, y, 0);
    }

    public void render(GraphicsContext graphics,
                       double x,
                       double y,
                       double angleDegrees) {

        graphics.save();
        graphics.translate(x + WIDTH / 2, y + HEIGHT / 2);
        graphics.rotate(angleDegrees);
        graphics.translate(-WIDTH / 2, -HEIGHT / 2);

        graphics.setFill(Color.BLACK);
        graphics.fillRoundRect(0, 0, WIDTH, HEIGHT, 12, 12);
        drawBulb(graphics, 21, 24, Color.RED, trafficLight.isRed());
        drawBulb(graphics, 21, 56, Color.YELLOW, trafficLight.isYellow());
        drawBulb(graphics, 21, 88, Color.LIMEGREEN, trafficLight.isGreen());
        graphics.restore();

        graphics.setFill(Color.WHITE);
        graphics.fillText(trafficLight.getId(), x - 4, y - 8);
        if (trafficLight.shouldDisplayTimer()) {
            graphics.fillText(String.valueOf(trafficLight.getTimer()), x + 14, y + HEIGHT + 16);
        }
    }

    private void drawBulb(GraphicsContext graphics,
                          double x,
                          double y,
                          Color color,
                          boolean active) {

        graphics.setFill(active ? color : Color.web("#333333"));
        graphics.fillOval(x - 10, y - 10, 20, 20);
    }
}
