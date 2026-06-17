package view.screen;

import controller.SimulationController;
import controller.LightControlMode;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import model.map.TrafficMapType;
import model.trafficlight.TrafficLight;
import model.vehicle.Vehicle;
import view.component.JavaFxRoadView;
import view.component.JavaFxTrafficLightView;
import view.component.JavaFxVehicleView;
import view.adapter.VehicleRenderAdapter;
import view.presentation.VehicleRenderState;
import view.renderer.DisplayMode;
import view.renderer.VehicleSpriteStore;

public class JavaFxSimulationScreen extends BorderPane {

    public static final double WIDTH = 900;
    public static final double HEIGHT = 700;

    private final SimulationController controller;

    private final Canvas canvas;

    private final Label statusLabel;

    private final JavaFxRoadView roadView;
    private final VehicleSpriteStore vehicleSpriteStore;
    private static final double[][] LIGHT_POSITIONS = {
            // Thứ tự này phải khớp với thứ tự addTrafficLight trong JavaFxTrafficApp:
            // TL-East, TL-South, TL-West, TL-North.
            // Sửa lại: KHÔNG đổi vị trí East/North nữa; chỉ đặt đúng góc lề và xoay đúng chiều.
            {260, 500},   // TL-East  - đèn cho xe đi sang Đông, đặt phía Tây/Nam của ngã tư
            {295, 112},   // TL-South - đèn cho xe đi xuống Nam, đặt phía Bắc/Tây của ngã tư
            {590, 135},   // TL-West  - đèn cho xe đi sang Tây, đặt phía Đông/Bắc của ngã tư
            {625, 505}    // TL-North - đèn cho xe đi lên Bắc, đặt phía Nam/Đông của ngã tư
    };
    private static final double[] LIGHT_ANGLES = {90, 0, 90, 0};
    private TrafficMapType mapType;
    private DisplayMode displayMode;

    public JavaFxSimulationScreen(SimulationController controller, Label statusLabel) {
        this.controller = controller;
        this.statusLabel = statusLabel;
        this.canvas = new Canvas(WIDTH, HEIGHT);
        this.roadView = new JavaFxRoadView(WIDTH, HEIGHT);
        this.vehicleSpriteStore = new VehicleSpriteStore();

        this.mapType = TrafficMapType.CROSS_JUNCTION;
        this.displayMode = DisplayMode.SPRITE;
        setCenter(canvas);
        configureManualLightClicks();
    }

    public void setMapType(TrafficMapType mapType) {
        this.mapType = mapType == null ? TrafficMapType.CROSS_JUNCTION : mapType;
        roadView.setMapType(this.mapType);
    }

    public void setDisplayMode(DisplayMode displayMode) {
        this.displayMode = displayMode == null ? DisplayMode.SPRITE : displayMode;
    }

    public void render(int tick) {
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.setFill(Color.web("#20242a"));
        graphics.fillRect(0, 0, WIDTH, HEIGHT);

        roadView.render(graphics);
        renderTrafficLights(graphics);
        renderVehicles(graphics, tick);
        updateStatus(tick);
    }

    private void renderTrafficLights(GraphicsContext graphics) {

        int index = 0;

        for (TrafficLight light : controller.getTrafficController().getTrafficLights()) {
           double[] position = LIGHT_POSITIONS[index % LIGHT_POSITIONS.length];
            double angle = LIGHT_ANGLES[index % LIGHT_ANGLES.length];
            new JavaFxTrafficLightView(light).render(graphics, position[0], position[1], angle);
            index++;
        }
    }

    private void renderVehicles(GraphicsContext graphics, int tick) {
        for (Vehicle vehicle : controller.getVehicleController().getVehicles()) {
            VehicleRenderState renderState = VehicleRenderAdapter.snapshotOf(vehicle);
            new JavaFxVehicleView(
                    renderState,
                    vehicleSpriteStore,
                    displayMode,
                    mapType.getVehicleScale(),
                    tick).render(graphics);
        }
    }

    private void configureManualLightClicks() {
        canvas.setOnMouseClicked(event -> {
            int index = 0;
            for (TrafficLight light : controller.getTrafficController().getTrafficLights()) {
                double[] position = LIGHT_POSITIONS[index % LIGHT_POSITIONS.length];
                double x = position[0];
                double y = position[1];
                double angle = LIGHT_ANGLES[index % LIGHT_ANGLES.length];
                double clickW = angle == 0 ? 42 : 112;
                double clickH = angle == 0 ? 112 : 42;
                if (event.getX() >= x - 35 && event.getX() <= x - 35 + clickW + 70
                         && event.getY() >= y + 35 && event.getY() <= y + 35 + clickH + 70
                        && controller.getTrafficController().getControlMode() == LightControlMode.MANUAL) {
                    controller.getTrafficController().switchLight(light);
                    render(0);
                    return;
                }
                index++;
            }
        });
    }

    private void updateStatus(int tick) {
        statusLabel.setText(String.format(
                "Tick: %d | Vehicles: %d | Lights: %d | %s | %s | %s",
                tick,
                controller.getVehicleController().getVehicles().size(),
                controller.getTrafficController().getTrafficLights().size(),
                controller.isRunning() ? "Running" : "Paused",
                controller.getTrafficController().getControlMode(),
                mapType) + " | Hiển thị: " + displayMode);
    }
}