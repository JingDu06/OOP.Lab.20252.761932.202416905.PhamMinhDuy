package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import model.vehicle.Vehicle;
import util.CollisionDetector;

public class VehicleController {

    private final List<Vehicle> vehicles;

    private final CollisionDetector collisionDetector;

    public VehicleController() {


        collisionDetector = new CollisionDetector();
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

    public void updateVehicles() {


        for (Vehicle vehicle : new ArrayList<>(vehicles)) {
            vehicle.update();
        }

        preventCollisions();
        removeVehiclesOutsideMap();
    }

    private void preventCollisions() {

        for (int i = 0; i < vehicles.size(); i++) {
            Vehicle vehicleA = vehicles.get(i);

            for (int j = i + 1; j < vehicles.size(); j++) {
                Vehicle vehicleB = vehicles.get(j);

                if (collisionDetector.isColliding(vehicleA, vehicleB)) {
                    vehicleA.stop();
                    vehicleB.stop();
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

            if (x < -100 || x > 1100 || y < -100 || y > 1100) {
                iterator.remove();
            }
        }
    }
}