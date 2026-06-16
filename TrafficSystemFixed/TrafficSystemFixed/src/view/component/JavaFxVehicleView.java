package view.component;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.vehicle.Vehicle;
import util.Vector2D;

public class JavaFxVehicleView {

    private final Vehicle vehicle;

    public JavaFxVehicleView(Vehicle vehicle) {

        this.vehicle = vehicle;
    }

    public void render(GraphicsContext graphics) {

        Vector2D position = vehicle.getPosition();
        graphics.setFill(getVehicleColor());
        graphics.fillRoundRect(position.getX() - 14, position.getY() - 8, 28, 16, 8, 8);
        graphics.setFill(Color.WHITE);
        graphics.fillText(vehicle.getId(), position.getX() - 20, position.getY() - 14);
    }

    private Color getVehicleColor() {

        String id = vehicle.getId();

        if (id.startsWith("AMB") || id.startsWith("FIRE")) {
            return Color.DEEPSKYBLUE;
        }

        if (id.startsWith("MOTO")) {
            return Color.ORANGE;
        }

        if (id.startsWith("BIKE")) {
            return Color.LIGHTPINK;
        }

        return Color.LIMEGREEN;
    }
}
