package view.presentation;

/**
 * Visual-only vehicle categories. This enum belongs to the presentation layer;
 * the traffic model never loads images or depends on JavaFX.
 */
public enum VehicleSpriteType {
    CAR("/assets/vehicles/car.png", 44, 22),
    MOTORBIKE("/assets/vehicles/motorbike.png", 42, 18),
    BICYCLE("/assets/vehicles/bicycle.png", 32, 14),
    BUS("/assets/vehicles/bus.png", 62, 23),
    AMBULANCE("/assets/vehicles/ambulance.png", 50, 23),
    FIRE_TRUCK("/assets/vehicles/firetruck.png", 62, 25);

    private final String resourcePath;
    private final double worldWidth;
    private final double worldHeight;

    VehicleSpriteType(String resourcePath, double worldWidth, double worldHeight) {
        this.resourcePath = resourcePath;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public double getWorldWidth() {
        return worldWidth;
    }

    public double getWorldHeight() {
        return worldHeight;
    }
}
