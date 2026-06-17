package view.presentation;

/**
 * Immutable snapshot consumed by the renderer.
 *
 * It deliberately contains only values needed for drawing. The renderer cannot
 * modify vehicle coordinates, speed, direction, collision state or route logic.
 */
public final class VehicleRenderState {

    private final String id;
    private final VehicleSpriteType spriteType;
    private final double x;
    private final double y;
    private final double headingDegrees;
    private final double speed;
    private final boolean sirenActive;
    private final boolean leftIndicatorActive;
    private final boolean rightIndicatorActive;

    public VehicleRenderState(String id,
                              VehicleSpriteType spriteType,
                              double x,
                              double y,
                              double headingDegrees,
                              double speed,
                              boolean sirenActive,
                              boolean leftIndicatorActive,
                              boolean rightIndicatorActive) {
        this.id = id;
        this.spriteType = spriteType;
        this.x = x;
        this.y = y;
        this.headingDegrees = headingDegrees;
        this.speed = speed;
        this.sirenActive = sirenActive;
        this.leftIndicatorActive = leftIndicatorActive;
        this.rightIndicatorActive = rightIndicatorActive;
    }

    public String getId() {
        return id;
    }

    public VehicleSpriteType getSpriteType() {
        return spriteType;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getHeadingDegrees() {
        return headingDegrees;
    }

    public double getSpeed() {
        return speed;
    }

    public boolean isSirenActive() {
        return sirenActive;
    }

    public boolean isLeftIndicatorActive() {
        return leftIndicatorActive;
    }

    public boolean isRightIndicatorActive() {
        return rightIndicatorActive;
    }
}

