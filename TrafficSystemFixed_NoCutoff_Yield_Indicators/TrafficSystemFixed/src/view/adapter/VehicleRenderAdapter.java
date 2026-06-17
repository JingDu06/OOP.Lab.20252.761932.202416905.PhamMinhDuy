package view.adapter;

import model.vehicle.Ambulance;
import model.vehicle.Bicycle;
import model.vehicle.Bus;
import model.vehicle.FireTruck;
import model.vehicle.Motorbike;
import model.vehicle.Vehicle;
import view.presentation.VehicleRenderState;
import view.presentation.VehicleSpriteType;

/**
 * Boundary between simulation state and JavaFX drawing state.
 * Coordinate/route logic stays in model + controller; this adapter only copies
 * the current values into an immutable render snapshot.
 */
public final class VehicleRenderAdapter {

    private VehicleRenderAdapter() {
    }

    public static VehicleRenderState snapshotOf(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("vehicle must not be null");
        }

        VehicleSpriteType spriteType = spriteTypeOf(vehicle);
        boolean sirenActive = false;

        if (vehicle instanceof Ambulance) {
            sirenActive = ((Ambulance) vehicle).isSirenOn();
        } else if (vehicle instanceof FireTruck) {
            sirenActive = ((FireTruck) vehicle).isSirenOn();
        }

        return new VehicleRenderState(
                vehicle.getId(),
                spriteType,
                vehicle.getPosition().getX(),
                vehicle.getPosition().getY(),
                normalizeAngle(vehicle.getVisualHeadingDegrees()),
                vehicle.getSpeed(),
                sirenActive,
                vehicle.isLeftIndicatorOn(),
                vehicle.isRightIndicatorOn());
    }

    private static VehicleSpriteType spriteTypeOf(Vehicle vehicle) {
        if (vehicle instanceof Ambulance) return VehicleSpriteType.AMBULANCE;
        if (vehicle instanceof FireTruck) return VehicleSpriteType.FIRE_TRUCK;
        if (vehicle instanceof Bus) return VehicleSpriteType.BUS;
        if (vehicle instanceof Motorbike) return VehicleSpriteType.MOTORBIKE;
        if (vehicle instanceof Bicycle) return VehicleSpriteType.BICYCLE;
        return VehicleSpriteType.CAR;
    }

    private static double normalizeAngle(double angle) {
        double normalized = angle % 360.0;
        return normalized < 0 ? normalized + 360.0 : normalized;
    }
}
