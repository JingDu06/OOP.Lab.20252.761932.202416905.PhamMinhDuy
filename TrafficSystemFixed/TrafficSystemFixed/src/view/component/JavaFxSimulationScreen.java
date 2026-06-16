package view.screen;

import controller.SimulationController;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import model.trafficlight.TrafficLight;
import model.vehicle.Vehicle;
import view.component.JavaFxRoadView;
import view.component.JavaFxTrafficLightView;
import view.component.JavaFxVehicleView;

public class JavaFxSimulationScreen extends BorderPane {

    public static final double WIDTH = 900;
    public static final double HEIGHT = 700;

    private final SimulationController controller;

    private final Canvas canvas;

    private final Label statusLabel;

    private final JavaFxRoadView roadView;

    public JavaFxSimulationScreen(SimulationController controller,
                                  Label statusLabel) {

        this.controller = controller;
        this.statusLabel = statusLabel;
        this.canvas = new Canvas(WIDTH, HEIGHT);
        this.roadView = new JavaFxRoadView(WIDTH, HEIGHT);

        setCenter(canvas);
    }

    public void render(int tick) {

        GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.setFill(Color.web("#20242a"));
        graphics.fillRect(0, 0, WIDTH, HEIGHT);

        roadView.render(graphics);
        renderTrafficLights(graphics);
        renderVehicles(graphics);
        updateStatus(tick);
    }

    private void renderTrafficLights(GraphicsContext graphics) {

        double x = 530;
        double y = 220;

        for (TrafficLight light : controller.getTrafficController().getTrafficLights()) {
            new JavaFxTrafficLightView(light).render(graphics, x, y);
            x += 60;
        }
    }

    private void renderVehicles(GraphicsContext graphics) {

        for (Vehicle vehicle : controller.getVehicleController().getVehicles()) {
            new JavaFxVehicleView(vehicle).render(graphics);
        }
    }

    private void updateStatus(int tick) {

        statusLabel.setText(String.format(
                "Tick: %d | Vehicles: %d | Lights: %d | %s",
                tick,
                controller.getVehicleController().getVehicles().size(),
                controller.getTrafficController().getTrafficLights().size(),
                controller.isRunning() ? "Running" : "Paused"));
    }
}
