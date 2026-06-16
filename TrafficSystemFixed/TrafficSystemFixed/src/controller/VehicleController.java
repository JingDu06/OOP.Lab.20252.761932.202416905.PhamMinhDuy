package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import model.trafficlight.TrafficLight;
import model.vehicle.Ambulance;
import model.vehicle.FireTruck;
import model.vehicle.Vehicle;
import model.vehicle.VehicleState;
import util.CollisionDetector;
import util.Direction;
import util.SoundManager;

public class VehicleController {

    private static final double SAFE_DISTANCE = 48;
    private static final double EMERGENCY_YIELD_DISTANCE = 130;
    private static final double CENTER_X = 450;
    private static final double CENTER_Y = 350;
    private static final double STOP_LINE_OFFSET = 85;

    private final List<Vehicle> vehicles;
    private final CollisionDetector collisionDetector;
    private final SoundManager soundManager;
    private List<TrafficLight> trafficLights;

    public VehicleController() {
        vehicles = new ArrayList<>();
        collisionDetector = new CollisionDetector();
        soundManager = new SoundManager();
        trafficLights = Collections.emptyList();
    }

    public void addVehicle(Vehicle vehicle) {


        if (vehicle != null && !vehicles.contains(vehicle)) {
            vehicles.add(vehicle);
        }
    }

    public void removeVehicle(Vehicle vehicle) {

        vehicles.remove(vehicle);
    }

    public List<Vehicle> getVehicles() {


        return Collections.unmodifiableList(vehicles);
    }

    public void setTrafficLights(List<TrafficLight> trafficLights) {
        this.trafficLights = trafficLights == null ? Collections.emptyList() : trafficLights;
    }

    public void updateVehicles() {
        applyTrafficRules();

        for (Vehicle vehicle : new ArrayList<>(vehicles)) {
            vehicle.update();
        }

        preventCollisions();
        removeVehiclesOutsideMap();
    }

    private void applyTrafficRules() {
        for (Vehicle vehicle : vehicles) {
            if (isEmergency(vehicle)) {
                soundManager.playSiren();
                continue;
            }

            if (mustStopAtRedLight(vehicle) || hasFrontVehicleTooClose(vehicle)) {
                vehicle.brake();
                vehicle.setState(VehicleState.WAITING);
            }

            if (shouldYieldToEmergency(vehicle)) {
                vehicle.brake();
                vehicle.nudgeAside(2.0);
                vehicle.setState(VehicleState.WAITING);
            }
        }
    }

    private boolean mustStopAtRedLight(Vehicle vehicle) {
        if (trafficLights.isEmpty() || trafficLights.stream().noneMatch(TrafficLight::isRed)) {
            return false;
        }

        switch (vehicle.getDirection()) {
            case EAST:
                return vehicle.getPosition().getX() < CENTER_X - STOP_LINE_OFFSET
                        && vehicle.getPosition().getX() > CENTER_X - STOP_LINE_OFFSET - 45;
            case WEST:
                return vehicle.getPosition().getX() > CENTER_X + STOP_LINE_OFFSET
                        && vehicle.getPosition().getX() < CENTER_X + STOP_LINE_OFFSET + 45;
            case NORTH:
                return vehicle.getPosition().getY() > CENTER_Y + STOP_LINE_OFFSET
                        && vehicle.getPosition().getY() < CENTER_Y + STOP_LINE_OFFSET + 45;
            case SOUTH:
                return vehicle.getPosition().getY() < CENTER_Y - STOP_LINE_OFFSET
                        && vehicle.getPosition().getY() > CENTER_Y - STOP_LINE_OFFSET - 45;
            default:
                return false;
        }
    }

    private boolean hasFrontVehicleTooClose(Vehicle vehicle) {
        return vehicles.stream()
                .filter(other -> other != vehicle)
                .filter(other -> other.getDirection() == vehicle.getDirection())
                .map(other -> distanceInFront(vehicle, other))
                .filter(distance -> distance >= 0)
                .min(Comparator.naturalOrder())
                .orElse(Double.MAX_VALUE) < SAFE_DISTANCE;
    }

    private boolean shouldYieldToEmergency(Vehicle vehicle) {
        return vehicles.stream()
                .filter(this::isEmergency)
                .anyMatch(emergency -> distanceBetween(vehicle, emergency) < EMERGENCY_YIELD_DISTANCE);
    }

    private boolean isEmergency(Vehicle vehicle) {
        return vehicle instanceof Ambulance || vehicle instanceof FireTruck;
    }

    private double distanceInFront(Vehicle currentVehicle, Vehicle candidate) {
        switch (currentVehicle.getDirection()) {
            case NORTH:
                return currentVehicle.getPosition().getY() - candidate.getPosition().getY();
            case SOUTH:
                return candidate.getPosition().getY() - currentVehicle.getPosition().getY();
            case EAST:
                return candidate.getPosition().getX() - currentVehicle.getPosition().getX();
            case WEST:
                return currentVehicle.getPosition().getX() - candidate.getPosition().getX();
            default:
                return -1;
        }
    }

    private double distanceBetween(Vehicle first, Vehicle second) {
        double dx = first.getPosition().getX() - second.getPosition().getX();
        double dy = first.getPosition().getY() - second.getPosition().getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    private void preventCollisions() {
        for (int i = 0; i < vehicles.size(); i++) {
            Vehicle vehicleA = vehicles.get(i);

            for (int j = i + 1; j < vehicles.size(); j++) {
                Vehicle vehicleB = vehicles.get(j);

                if (collisionDetector.isColliding(vehicleA, vehicleB)) {
                    vehicleA.brake();
                    vehicleB.brake();
                    vehicleA.nudgeAside(1.5);
                    vehicleB.nudgeAside(-1.5);
                    soundManager.playHorn();
                }
            }
        }
    }

    private void removeVehiclesOutsideMap() {

        Iterator<Vehicle> iterator = vehicles.iterator();

        while (iterator.hasNext()) {
            Vehicle vehicle = iterator.next();
            double x = vehicle.getPosition().getX();
            double y = vehicle.getPosition().getY();

 
            if (x < -140 || x > 1040 || y < -140 || y > 840) {
                iterator.remove();
            }
        }
    }
}
