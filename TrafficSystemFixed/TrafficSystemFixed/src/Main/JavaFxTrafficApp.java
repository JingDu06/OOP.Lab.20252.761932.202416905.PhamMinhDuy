package Main;

import controller.SimulationController;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.trafficlight.BasicTrafficLight;
import model.vehicle.Vehicle;
import model.vehicle.VehicleFactory;
import strategy.EmergencyDriver;
import strategy.NormalDriver;
import util.Direction;
import util.Vector2D;
import view.screen.JavaFxSimulationScreen;

public class JavaFxTrafficApp extends Application {

    private static final long FRAME_INTERVAL = 100_000_000L;

    private SimulationController controller;

    private JavaFxSimulationScreen simulationScreen;

    private BorderPane root;

    private Label statusLabel;

    private AnimationTimer timer;

    private long lastFrame;

    private int tick;

    @Override
    public void start(Stage stage) {

        statusLabel = new Label("Ready");
        resetController();

        Button startButton = new Button("Start");
        Button pauseButton = new Button("Pause");
        Button resetButton = new Button("Reset");

        startButton.setOnAction(event -> controller.start());
        pauseButton.setOnAction(event -> controller.stop());
        resetButton.setOnAction(event -> resetSimulation());

        HBox toolbar = new HBox(10, startButton, pauseButton, resetButton, statusLabel);
        toolbar.setPadding(new Insets(10));

        root = new BorderPane();
        root.setTop(toolbar);
        root.setCenter(simulationScreen);

        stage.setTitle("Traffic System JavaFX");
        stage.setScene(new Scene(
                root,
                JavaFxSimulationScreen.WIDTH,
                JavaFxSimulationScreen.HEIGHT + 50));
        stage.show();

        simulationScreen.render(tick);
        runAnimationLoop();
    }

    private void runAnimationLoop() {

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {

                if (now - lastFrame < FRAME_INTERVAL) {
                    return;
                }

                lastFrame = now;

                if (controller.isRunning()) {
                    controller.update();
                    tick++;
                }

                simulationScreen.render(tick);
            }
        };

        timer.start();
    }

    private void resetSimulation() {

        controller.stop();
        resetController();
        root.setCenter(simulationScreen);
        simulationScreen.render(tick);
    }

    private void resetController() {

        controller = new SimulationController();
        setupDemoScenario();
        simulationScreen = new JavaFxSimulationScreen(controller, statusLabel);
        tick = 0;
    }

    private void setupDemoScenario() {

        controller.getTrafficController().addTrafficLight(new BasicTrafficLight("TL-01"));

        Vehicle car = VehicleFactory.createVehicle(
                "CAR", "CAR-01", new Vector2D(100, 350), Direction.EAST);
        car.setStrategy(new NormalDriver());
        controller.getVehicleController().addVehicle(car);

        Vehicle motorbike = VehicleFactory.createVehicle(
                "MOTORBIKE", "MOTO-01", new Vector2D(780, 300), Direction.WEST);
        motorbike.setStrategy(new NormalDriver());
        controller.getVehicleController().addVehicle(motorbike);

        Vehicle ambulance = VehicleFactory.createVehicle(
                "AMBULANCE", "AMB-01", new Vector2D(450, 620), Direction.NORTH);
        ambulance.setStrategy(new EmergencyDriver());
        controller.getVehicleController().addVehicle(ambulance);
    }

    @Override
    public void stop() {

        if (timer != null) {
            timer.stop();
        }
    }

    public static void main(String[] args) {

        launch(args);
    }
}
