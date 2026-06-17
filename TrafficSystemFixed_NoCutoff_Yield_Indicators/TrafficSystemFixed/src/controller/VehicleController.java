package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import model.trafficlight.TrafficLight;
import model.vehicle.Ambulance;
import model.vehicle.Bicycle;
import model.vehicle.Bus;
import model.vehicle.FireTruck;
import model.vehicle.Motorbike;
import model.vehicle.Vehicle;
import model.vehicle.VehicleState;
import util.CollisionDetector;
import util.Direction;
import util.SoundManager;

public class VehicleController {

    private enum TurnChoice {
        STRAIGHT,
        LEFT,
        RIGHT
    }

    private static class PendingTurn {
        private final Direction targetDirection;
        private final TurnChoice turnChoice;
        private final double startX;
        private final double startY;
        private final double control1X;
        private final double control1Y;
        private final double control2X;
        private final double control2Y;
        private final double targetX;
        private final double targetY;
        private final int totalSteps;
        private int currentStep;
        private int slowTick;
        private double maxSpeedDuringTurn;

        private PendingTurn(Direction targetDirection,
                            TurnChoice turnChoice,
                            double startX,
                            double startY,
                            double control1X,
                            double control1Y,
                            double control2X,
                            double control2Y,
                            double targetX,
                            double targetY,
                            int totalSteps) {
            this.targetDirection = targetDirection;
            this.turnChoice = turnChoice;
            this.startX = startX;
            this.startY = startY;
            this.control1X = control1X;
            this.control1Y = control1Y;
            this.control2X = control2X;
            this.control2Y = control2Y;
            this.targetX = targetX;
            this.targetY = targetY;
            this.totalSteps = totalSteps;
            this.currentStep = 0;
            this.slowTick = 0;
            this.maxSpeedDuringTurn = 0;
        }
    }

    private static class PendingLaneChange {
        private final Direction direction;
        private final TurnChoice turnChoice;
        private final double startLateral;
        private final double targetLateral;
        private final double targetOffset;
        private final double longitudinalLength;
        private final boolean emergencyYield;
        private double travelled;

        private PendingLaneChange(Direction direction,
                                  TurnChoice turnChoice,
                                  double startLateral,
                                  double targetLateral,
                                  double targetOffset,
                                  double longitudinalLength,
                                  boolean emergencyYield) {
            this.direction = direction;
            this.turnChoice = turnChoice;
            this.startLateral = startLateral;
            this.targetLateral = targetLateral;
            this.targetOffset = targetOffset;
            this.longitudinalLength = longitudinalLength;
            this.emergencyYield = emergencyYield;
            this.travelled = 0;
        }
    }

    private static final double SAFE_DISTANCE = 54;
    private static final double PROXIMITY_SLOW_DISTANCE = 96;
    private static final double PROXIMITY_STOP_DISTANCE = 38;
    private static final double JUNCTION_SLOW_DISTANCE = 82;
    private static final double EMERGENCY_YIELD_DISTANCE = 230;
    private static final double CENTER_X = 450;
    private static final double CENTER_Y = 350;
    private static final double STOP_LINE_OFFSET = 216;
    private static final double RIGHT_LANE_OFFSET = 45;
    private static final double TURN_EXIT_OFFSET = 152;
    private static final double LANE_KEEP_MARGIN = 2.5;
    private static final double LEFT_TURN_LANE_OFFSET = 18;
    private static final double OUTER_LANE_OFFSET = 26;
    private static final double MIDDLE_LANE_OFFSET = 47;
    private static final double INNER_LANE_OFFSET = 68;
    private static final double RIGHT_TURN_LANE_OFFSET = 72;
    private static final double BUS_LANE_OFFSET = 82;
    private static final double TURN_POCKET_DISTANCE = 260;
    // Bắt đầu đánh lái từ sớm, trước khi đầu xe chạm vào phần đường thẳng của nhánh mới.
    // Khoảng này chỉ kích hoạt quỹ đạo; xe vẫn phải ở đúng làn và kiểm tra an toàn trước khi cua.
    private static final double LEFT_TURN_START_DISTANCE = 146;
    private static final double RIGHT_TURN_START_DISTANCE = 164;
    private static final double OUTER_LANE_EARLY_TURN_EXTRA = 72;
    private static final double CONFLICT_RESERVATION_DISTANCE = 178;
    private static final double CLEAR_ROAD_FAST_DISTANCE = 210;
    private static final double TURN_FAST_REAR_DISTANCE = 150;
    private static final double TURN_FAST_PATH_DISTANCE = 95;
    private static final double LANE_CHANGE_START_DISTANCE = 350;
    private static final double LANE_CHANGE_MIN_LENGTH = 150;
    private static final double LANE_CHANGE_FRONT_GAP = 82;
    private static final double LANE_CHANGE_REAR_GAP = 62;
    private static final double LANE_CHANGE_FINISH_TOLERANCE = 2.0;
    private static final double LANE_CENTER_CORRECTION_PER_TICK = 0.35;
    private static final double EMERGENCY_YIELD_LANE_CHANGE_LENGTH = 135;
    private static final double EMERGENCY_YIELD_SLOW_SPEED = 1.30;
    private static final double EMERGENCY_SAME_ROAD_WIDTH = 104;
    private static final double EMERGENCY_MIN_FRONT_GAP = 72;
    private static final double EMERGENCY_MIN_REAR_GAP = 58;
    private static final int EMERGENCY_OVERTAKE_WAIT_TICKS = 100; // 10 giây ở nhịp 100 ms/tick
    private static final double EMERGENCY_OVERTAKE_FRONT_GAP = 92;
    private static final double EMERGENCY_OVERTAKE_REAR_GAP = 72;
    private static final double BICYCLE_PULL_OVER_OFFSET = 82;
    private static final double EMERGENCY_PASS_CLEARANCE = 78;
    private static final double EMERGENCY_CLOSE_BEHIND_DISTANCE = 92;
    private static final double EMERGENCY_CRITICAL_BEHIND_DISTANCE = 54;
    private static final double LANE_TARGET_TOLERANCE = 13;

    private final List<Vehicle> vehicles;
    private final CollisionDetector collisionDetector;
    private final SoundManager soundManager;
    private final List<Vehicle> blockedVehicles;
    private final Map<Vehicle, TurnChoice> turnChoices;
    private final Map<Vehicle, PendingTurn> turningVehicles;
    private final Map<Vehicle, PendingLaneChange> laneChangingVehicles;
    private final Map<Vehicle, Double> assignedLaneOffsets;
    private final Map<Vehicle, Integer> emergencyBlockedTicks;
    private final Map<Vehicle, Vehicle> bicycleYieldingToEmergency;
    private final Set<Vehicle> emergencyOvertakingVehicles;
    private final List<Vehicle> vehiclesThatTurned;
    private final Random random;
    private List<TrafficLight> trafficLights;

    public VehicleController() {
        vehicles = new ArrayList<>();
        collisionDetector = new CollisionDetector();
        soundManager = new SoundManager();
        blockedVehicles = new ArrayList<>();
        turnChoices = new HashMap<>();
        turningVehicles = new HashMap<>();
        laneChangingVehicles = new HashMap<>();
        assignedLaneOffsets = new HashMap<>();
        emergencyBlockedTicks = new HashMap<>();
        bicycleYieldingToEmergency = new HashMap<>();
        emergencyOvertakingVehicles = new HashSet<>();
        vehiclesThatTurned = new ArrayList<>();
        random = new Random();
        trafficLights = Collections.emptyList();
    }

    public void addVehicle(Vehicle vehicle) {


        if (vehicle != null && !vehicles.contains(vehicle)) {
            vehicles.add(vehicle);
            // Giữ đúng làn mà xe được spawn vào. Ba làn đều dùng được cho mọi xe,
            // riêng xe đạp luôn bị khóa ở làn trong/phải nhất.
            double initialOffset = vehicle instanceof Bicycle
                    ? INNER_LANE_OFFSET
                    : nearestLaneOffsetForPosition(vehicle);
            assignedLaneOffsets.put(vehicle, initialOffset);
        }
    }

    private void setLaneChangeIndicators(Vehicle vehicle, double startLateral, double targetLateral) {
        if (vehicle == null) {
            return;
        }
        vehicle.clearIndicators();
        if (targetLateral > startLateral + 0.5) {
            vehicle.setRightIndicatorOn(true);
        } else if (targetLateral < startLateral - 0.5) {
            vehicle.setLeftIndicatorOn(true);
        }
    }

    private void clearLaneChangeIndicators(Vehicle vehicle) {
        if (vehicle != null) {
            vehicle.clearIndicators();
        }
    }

    public void removeVehicle(Vehicle vehicle) {

        vehicles.remove(vehicle);
        turnChoices.remove(vehicle);
        turningVehicles.remove(vehicle);
        laneChangingVehicles.remove(vehicle);
        assignedLaneOffsets.remove(vehicle);
        emergencyBlockedTicks.remove(vehicle);
        bicycleYieldingToEmergency.remove(vehicle);
        bicycleYieldingToEmergency.values().removeIf(value -> value == vehicle);
        emergencyOvertakingVehicles.remove(vehicle);
        vehiclesThatTurned.remove(vehicle);
        clearLaneChangeIndicators(vehicle);
    }

    public List<Vehicle> getVehicles() {


        return Collections.unmodifiableList(vehicles);
    }

    public void setTrafficLights(List<TrafficLight> trafficLights) {
        this.trafficLights = trafficLights == null ? Collections.emptyList() : trafficLights;
    }

    public void updateVehicles() {
        blockedVehicles.clear();
        applyTrafficRules();

        for (Vehicle vehicle : new ArrayList<>(vehicles)) {
            if (!blockedVehicles.contains(vehicle)) {
                if (turningVehicles.containsKey(vehicle)) {
                    advanceTurn(vehicle);
                } else if (laneChangingVehicles.containsKey(vehicle)) {
                    advanceLaneChange(vehicle);
                } else {
                    vehicle.update();
                    prepareLaneChangeIfNeeded(vehicle);
                    if (!laneChangingVehicles.containsKey(vehicle)) {
                        applyTurnIfNeeded(vehicle);
                        keepVehicleOnRoad(vehicle);
                    }
                }
            }
        }

        // Không còn teleport xe lùi về sau khi va chạm.
        // Xe sẽ giảm tốc từ xa, còn nếu quá sát thì dừng mềm tại chỗ.
        preventCollisions();
        removeVehiclesOutsideMap();
    }

    private void applyTrafficRules() {
        boolean ambulanceSirenActive = vehicles.stream()
                .filter(Ambulance.class::isInstance)
                .map(Ambulance.class::cast)
                .anyMatch(Ambulance::isSirenOn);
        boolean fireTruckSirenActive = vehicles.stream()
                .filter(FireTruck.class::isInstance)
                .map(FireTruck.class::cast)
                .anyMatch(FireTruck::isSirenOn);
        soundManager.updateEmergencySirens(ambulanceSirenActive, fireTruckSirenActive);

        for (Vehicle vehicle : vehicles) {
            // Xe đang đi theo cung rẽ đã có quỹ đạo riêng; không chặn lại giữa ngã tư.
            if (turningVehicles.containsKey(vehicle)) {
                continue;
            }

            if (isEmergency(vehicle)) {
                applyEmergencyVehicleRules(vehicle);
                continue;
            }

            // Xe đạp đã tấp vào phải đứng chờ đến khi xe ưu tiên đi qua hẳn,
            // sau đó mới chuyển từ từ về làn trong và chạy tiếp.
            if (vehicle instanceof Bicycle && updateBicycleEmergencyYield((Bicycle) vehicle)) {
                continue;
            }

            // Ưu tiên xử lý nhường đường trước đèn đỏ và trước các luật giảm tốc khác.
            // Nhờ vậy xe buýt không bị dừng cứng trước khi kịp dạt sang phải.
            Vehicle approachingEmergency = findApproachingEmergency(vehicle);
            if (approachingEmergency != null) {
                handleEmergencyYield(vehicle, approachingEmergency);
                PendingLaneChange yieldChange = laneChangingVehicles.get(vehicle);
                if ((yieldChange != null && yieldChange.emergencyYield)
                        || bicycleYieldingToEmergency.containsKey(vehicle)) {
                    continue;
                }
            }

            if (mustStopForIntersectionConflict(vehicle)) {
                vehicle.brake();
                if (vehicle.getSpeed() <= 0.05) {
                    blockVehicle(vehicle);
                    continue;
                }
            }

            if (mustStopAtRedLight(vehicle)) {
                vehicle.stop();
                blockVehicle(vehicle);
                continue;
            }

            slowWhenApproachingIntersection(vehicle);

            Vehicle nearestRisk = findNearestRiskVehicle(vehicle);
            if (nearestRisk != null) {
                double riskDistance = distanceBetween(vehicle, nearestRisk);
                if (riskDistance < PROXIMITY_STOP_DISTANCE) {
                    vehicle.stop();
                    blockVehicle(vehicle);
                    continue;
                }
                if (riskDistance < PROXIMITY_SLOW_DISTANCE) {
                    limitSpeedNearVehicle(vehicle, nearestRisk, riskDistance);
                }
            }

            Vehicle frontVehicle = findNearestFrontVehicle(vehicle);
            if (frontVehicle != null && distanceInFront(vehicle, frontVehicle) < SAFE_DISTANCE) {
                handleTooCloseFrontVehicle(vehicle, frontVehicle);
            }

            if (vehicle.getState() == VehicleState.WAITING) {
                vehicle.setState(VehicleState.STOPPED);
            }

            if (!blockedVehicles.contains(vehicle)
                    && !turningVehicles.containsKey(vehicle)
                    && approachingEmergency == null
                    && !mustStopAtRedLight(vehicle)) {
                speedUpOnClearRoad(vehicle);
            }
        }
    }


    private void speedUpOnClearRoad(Vehicle vehicle) {
        if (!hasClearRoadAhead(vehicle, CLEAR_ROAD_FAST_DISTANCE)) {
            return;
        }

        // Trong ngã tư hoặc vừa ra khỏi cua thì cho xe thoát nhanh hơn một chút để giảm ùn tắc.
        int extra = (isInsideConflictBox(vehicle) || vehiclesThatTurned.contains(vehicle) || distanceToIntersection(vehicle) < 70) ? 2 : 1;
        for (int i = 0; i < extra; i++) {
            vehicle.accelerate();
        }
    }

    private boolean hasClearRoadAhead(Vehicle vehicle, double lookAheadDistance) {
        for (Vehicle other : vehicles) {
            if (other == vehicle) {
                continue;
            }
            if (other.getDirection() != vehicle.getDirection()) {
                continue;
            }
            if (lateralDistanceBetween(vehicle, other) > 28) {
                continue;
            }
            double gap = distanceInFront(vehicle, other);
            if (gap >= 0 && gap < lookAheadDistance) {
                return false;
            }
        }
        return true;
    }

    private boolean isTurnPathClearForFastMove(Vehicle vehicle, PendingTurn pendingTurn) {
        // Không phóng nhanh nếu phía sau cùng làn có xe đang áp sát đoạn rẽ.
        if (hasVehicleCloseBehind(vehicle, TURN_FAST_REAR_DISTANCE)) {
            return false;
        }

        // Không phóng nhanh nếu có xe đang nằm gần đoạn vào cua/ra cua hoặc đang cắt qua vùng cua.
        for (Vehicle other : vehicles) {
            if (other == vehicle) {
                continue;
            }
            double dNow = distanceBetween(vehicle, other);
            if (dNow < TURN_FAST_PATH_DISTANCE) {
                return false;
            }
            double dTarget = Math.hypot(other.getPosition().getX() - pendingTurn.targetX,
                    other.getPosition().getY() - pendingTurn.targetY);
            if (dTarget < TURN_FAST_PATH_DISTANCE) {
                return false;
            }
            if ((isInsideConflictBox(other) || turningVehicles.containsKey(other))
                    && pathsMayConflict(vehicle, other)
                    && distanceBetween(vehicle, other) < TURN_FAST_PATH_DISTANCE * 1.45) {
                return false;
            }
        }
        return true;
    }

    private boolean hasVehicleCloseBehind(Vehicle vehicle, double rearDistance) {
        for (Vehicle other : vehicles) {
            if (other == vehicle || other.getDirection() != vehicle.getDirection()) {
                continue;
            }
            if (lateralDistanceBetween(vehicle, other) > 32) {
                continue;
            }
            double behindDistance = distanceInFront(other, vehicle);
            if (behindDistance >= 0 && behindDistance < rearDistance) {
                return true;
            }
        }
        return false;
    }


    private boolean mustStopForIntersectionConflict(Vehicle vehicle) {
        if (isEmergency(vehicle) || vehiclesThatTurned.contains(vehicle) || turningVehicles.containsKey(vehicle)) {
            return false;
        }
        TurnChoice choice = getTurnChoice(vehicle);
        // Right-turn slip lane is outside the conflict box, so it should not wait for the main junction.
        if (choice == TurnChoice.RIGHT) {
            return false;
        }
        if (distanceToIntersection(vehicle) > 95) {
            return false;
        }
        if (isInsideConflictBox(vehicle)) {
            return false;
        }
        for (Vehicle other : vehicles) {
            if (other == vehicle || isEmergency(other)) {
                continue;
            }
            if (isInsideConflictBox(other) || turningVehicles.containsKey(other)) {
                double d = distanceBetween(vehicle, other);
                if (d < 64 && pathsMayConflict(vehicle, other) && shouldYield(vehicle, other)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isInsideConflictBox(Vehicle vehicle) {
        double x = vehicle.getPosition().getX();
        double y = vehicle.getPosition().getY();
        return x > CENTER_X - 132 && x < CENTER_X + 132
                && y > CENTER_Y - 132 && y < CENTER_Y + 132;
    }

    private boolean isDeepInsideConflictBox(Vehicle vehicle) {
        double x = vehicle.getPosition().getX();
        double y = vehicle.getPosition().getY();
        return x > CENTER_X - 92 && x < CENTER_X + 92
                && y > CENTER_Y - 92 && y < CENTER_Y + 92;
    }

    private boolean pathsMayConflict(Vehicle vehicle, Vehicle other) {
        TurnChoice a = getTurnChoice(vehicle);
        TurnChoice b = getTurnChoice(other);
        Direction aExit = a == TurnChoice.STRAIGHT ? vehicle.getDirection() : directionAfterTurn(vehicle.getDirection(), a);
        Direction bExit = b == TurnChoice.STRAIGHT ? other.getDirection() : directionAfterTurn(other.getDirection(), b);

        if (vehicle.getDirection() == other.getDirection() && aExit == bExit) {
            return lateralDistanceBetween(vehicle, other) < SAFE_DISTANCE;
        }
        if (a == TurnChoice.RIGHT && b == TurnChoice.RIGHT && aExit != bExit) {
            return false;
        }
        return vehicle.getDirection() != other.getDirection() || aExit == bExit;
    }

    private boolean shouldYield(Vehicle vehicle, Vehicle other) {
        if (isEmergency(vehicle)) return false;
        if (isEmergency(other)) return true;
        if (vehicle instanceof Bus && !(other instanceof Bus)) return false;
        if (other instanceof Bus && !(vehicle instanceof Bus)) return true;
        double myDistance = distanceToIntersection(vehicle);
        double otherDistance = distanceToIntersection(other);
        if (turningVehicles.containsKey(other) || isInsideConflictBox(other)) return true;
        return myDistance > otherDistance;
    }

    private boolean mustStopAtRedLight(Vehicle vehicle) {
        // Xe đã đi qua vạch dừng/đã vào ngã tư thì phải đi tiếp, không được dừng giữa đường
        // dù đèn vừa chuyển đỏ. Kiểm tra bằng ĐẦU XE thay vì tâm xe để tránh lỗi dừng quá muộn.
        if (turningVehicles.containsKey(vehicle) || isInsideConflictBox(vehicle) || hasFrontPassedStopLine(vehicle)) {
            return false;
        }

        TrafficLight light = getTrafficLightForDirection(vehicle.getDirection());
        if (light == null || !light.isRed()) {
            return false;
        }

        double front = frontCoordinate(vehicle);
        double stopLine = stopLineFor(vehicle.getDirection());
        double stopWindow = 92;

        switch (vehicle.getDirection()) {
            case EAST:
                return front < stopLine && front > stopLine - stopWindow;
            case WEST:
                return front > stopLine && front < stopLine + stopWindow;
            case NORTH:
                return front > stopLine && front < stopLine + stopWindow;
            case SOUTH:
                return front < stopLine && front > stopLine - stopWindow;
            default:
                return false;
        }
    }

    private boolean hasFrontPassedStopLine(Vehicle vehicle) {
        double front = frontCoordinate(vehicle);
        double stopLine = stopLineFor(vehicle.getDirection());
        switch (vehicle.getDirection()) {
            case EAST:
                return front >= stopLine;
            case WEST:
                return front <= stopLine;
            case NORTH:
                return front <= stopLine;
            case SOUTH:
                return front >= stopLine;
            default:
                return false;
        }
    }

    private double frontCoordinate(Vehicle vehicle) {
        double halfLength = vehicleLength(vehicle) / 2.0;
        switch (vehicle.getDirection()) {
            case EAST:
                return vehicle.getPosition().getX() + halfLength;
            case WEST:
                return vehicle.getPosition().getX() - halfLength;
            case NORTH:
                return vehicle.getPosition().getY() - halfLength;
            case SOUTH:
                return vehicle.getPosition().getY() + halfLength;
            default:
                return 0;
        }
    }

    private double vehicleLength(Vehicle vehicle) {
        if (vehicle instanceof Bus || vehicle instanceof FireTruck) {
            return 42;
        }
        if (vehicle instanceof Ambulance) {
            return 34;
        }
        return 28;
    }

    private double stopLineFor(Direction direction) {
        switch (direction) {
            case EAST:
                return CENTER_X - STOP_LINE_OFFSET;
            case WEST:
                return CENTER_X + STOP_LINE_OFFSET;
            case NORTH:
                return CENTER_Y + STOP_LINE_OFFSET;
            case SOUTH:
                return CENTER_Y - STOP_LINE_OFFSET;
            default:
                return 0;
        }
    }

    private TrafficLight getTrafficLightForDirection(Direction direction) {
        if (trafficLights.isEmpty()) {
            return null;
        }

        int index;
        switch (direction) {
            case EAST:
                index = 0;
                break;
            case SOUTH:
                index = 1;
                break;
            case WEST:
                index = 2;
                break;
            case NORTH:
                index = 3;
                break;
            default:
                index = 0;
                break;
        }
        return trafficLights.get(index % trafficLights.size());
    }

    private Vehicle findNearestFrontVehicle(Vehicle vehicle) {
        return vehicles.stream()
                .filter(other -> other != vehicle)
                .filter(other -> other.getDirection() == vehicle.getDirection())
                .filter(other -> lateralDistanceBetween(vehicle, other) <= LANE_KEEP_MARGIN * 2)
                .filter(other -> distanceInFront(vehicle, other) >= 0)
                .min(Comparator.comparingDouble(other -> distanceInFront(vehicle, other)))
                .orElse(null);
    }

    private void handleTooCloseFrontVehicle(Vehicle vehicle, Vehicle frontVehicle) {
        // Khi có xe cứu thương/cứu hỏa bật còi phía sau, mọi xe kể cả xe buýt
        // không được vượt; chúng phải giảm tốc hoặc dạt phải để nhường đường.
        if (findApproachingEmergency(vehicle) != null) {
            slowForEmergency(vehicle);
            return;
        }

        if (!isEmergency(frontVehicle)
                && isFrontVehicleTooSlow(vehicle, frontVehicle)
                && canOvertakeSafely(vehicle)) {
            soundManager.playHorn();
            soundManager.playTurnSignal();
            vehicle.nudgeAside(overtakeOffset(vehicle));
            keepVehicleOnRoad(vehicle);
            return;
        }

        double gap = distanceInFront(vehicle, frontVehicle);
        if (gap < PROXIMITY_STOP_DISTANCE) {
            vehicle.stop();
            blockVehicle(vehicle);
        } else {
            limitSpeedNearVehicle(vehicle, frontVehicle, gap);
        }
    }

    private void limitSpeedNearVehicle(Vehicle vehicle, Vehicle other, double distance) {
        if (isEmergency(vehicle)) {
            // Xe ưu tiên vẫn phải giữ khoảng cách, tuyệt đối không xuyên/đè lên xe trước.
            double safeTarget = Math.max(0.0, other.getSpeed() + (distance > 62 ? 0.35 : 0.0));
            vehicle.setSpeed(Math.min(vehicle.getSpeed(), safeTarget));
            return;
        }

        // Bus được ưu tiên khi nhập cùng làn, xe khác sẽ nhường bus.
        if (!(vehicle instanceof Bus) && other instanceof Bus && distance < PROXIMITY_SLOW_DISTANCE) {
            vehicle.setSpeed(Math.min(vehicle.getSpeed(), 0.7));
            return;
        }

        double targetSpeed;
        if (distance < 50) {
            targetSpeed = 0.7;
        } else if (distance < 70) {
            targetSpeed = 1.2;
        } else {
            targetSpeed = 1.8;
        }
        vehicle.setSpeed(Math.min(vehicle.getSpeed(), targetSpeed));
    }

    private Vehicle findNearestRiskVehicle(Vehicle vehicle) {
        Vehicle nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Vehicle other : vehicles) {
            if (other == vehicle) {
                continue;
            }

            double distance = distanceBetween(vehicle, other);
            if (distance >= PROXIMITY_SLOW_DISTANCE) {
                continue;
            }

            boolean sameLaneAhead = vehicle.getDirection() == other.getDirection()
                    && lateralDistanceBetween(vehicle, other) < 24
                    && distanceInFront(vehicle, other) >= 0;
            boolean junctionRisk = (isInsideConflictBox(vehicle) || isInsideConflictBox(other)
                    || turningVehicles.containsKey(vehicle) || turningVehicles.containsKey(other))
                    && distance < JUNCTION_SLOW_DISTANCE
                    && shouldYieldForMerge(vehicle, other);
            boolean targetMergeRisk = willMergeIntoSameLane(vehicle, other)
                    && distance < JUNCTION_SLOW_DISTANCE
                    && shouldYieldForMerge(vehicle, other);

            if ((sameLaneAhead || junctionRisk || targetMergeRisk) && distance < nearestDistance) {
                nearest = other;
                nearestDistance = distance;
            }
        }
        return nearest;
    }


    private boolean shouldYieldForMerge(Vehicle vehicle, Vehicle other) {
        if (isEmergency(vehicle)) return false;
        if (isEmergency(other)) return true;
        if (vehicle instanceof Bus && !(other instanceof Bus)) return false;
        if (other instanceof Bus && !(vehicle instanceof Bus)) return true;

        Direction myExit = exitDirectionFor(vehicle);
        Direction otherExit = exitDirectionFor(other);
        if (myExit == otherExit) {
            double myLaneError = distanceToExitLane(vehicle, myExit);
            double otherLaneError = distanceToExitLane(other, otherExit);
            if (Math.abs(myLaneError - otherLaneError) > 5) {
                // Xe nào đã gần đúng làn nhập hơn thì được đi trước, xe kia nhường.
                return myLaneError > otherLaneError;
            }
        }

        double myProgress = progressToExit(vehicle, myExit);
        double otherProgress = progressToExit(other, otherExit);
        if (Math.abs(myProgress - otherProgress) > 8) {
            return myProgress < otherProgress;
        }

        return vehicle.getId().compareTo(other.getId()) > 0;
    }

    private Direction exitDirectionFor(Vehicle vehicle) {
        TurnChoice choice = getTurnChoice(vehicle);
        return choice == TurnChoice.STRAIGHT ? vehicle.getDirection() : directionAfterTurn(vehicle.getDirection(), choice);
    }

    private double distanceToExitLane(Vehicle vehicle, Direction exitDirection) {
        double lane = laneCenterForExit(vehicle, exitDirection);
        switch (exitDirection) {
            case EAST:
            case WEST:
                return Math.abs(vehicle.getPosition().getY() - lane);
            case NORTH:
            case SOUTH:
                return Math.abs(vehicle.getPosition().getX() - lane);
            default:
                return Double.MAX_VALUE;
        }
    }

    private double progressToExit(Vehicle vehicle, Direction exitDirection) {
        switch (exitDirection) {
            case EAST:
                return vehicle.getPosition().getX();
            case WEST:
                return -vehicle.getPosition().getX();
            case SOUTH:
                return vehicle.getPosition().getY();
            case NORTH:
                return -vehicle.getPosition().getY();
            default:
                return 0;
        }
    }

    private boolean willMergeIntoSameLane(Vehicle vehicle, Vehicle other) {
        TurnChoice myChoice = getTurnChoice(vehicle);
        TurnChoice otherChoice = getTurnChoice(other);
        Direction myExit = myChoice == TurnChoice.STRAIGHT ? vehicle.getDirection() : directionAfterTurn(vehicle.getDirection(), myChoice);
        Direction otherExit = otherChoice == TurnChoice.STRAIGHT ? other.getDirection() : directionAfterTurn(other.getDirection(), otherChoice);
        if (myExit != otherExit) {
            return false;
        }
        return Math.abs(laneCenterForExit(vehicle, myExit) - laneCenterForExit(other, otherExit)) < 26;
    }

    private boolean isFrontVehicleTooSlow(Vehicle vehicle, Vehicle frontVehicle) {
        return frontVehicle.getSpeed() + 0.5 < vehicle.getSpeed();
    }

    private void blockVehicle(Vehicle vehicle) {
        vehicle.setState(VehicleState.WAITING);
        if (!blockedVehicles.contains(vehicle)) {
            blockedVehicles.add(vehicle);
        }
    }

    private boolean canOvertakeSafely(Vehicle vehicle) {
        double offset = overtakeOffset(vehicle);
        double projectedX = vehicle.getPosition().getX();
        double projectedY = vehicle.getPosition().getY();

        switch (vehicle.getDirection()) {
            case NORTH:
            case SOUTH:
                projectedX += offset;
                break;
            case EAST:
            case WEST:
                projectedY += offset;
                break;
            default:
                break;
        }

        for (Vehicle other : vehicles) {
            if (other == vehicle) {
                continue;
            }
            double dx = projectedX - other.getPosition().getX();
            double dy = projectedY - other.getPosition().getY();
            if (Math.sqrt(dx * dx + dy * dy) < SAFE_DISTANCE * 0.85) {
                return false;
            }
        }
        return true;
    }

    private double overtakeOffset(Vehicle vehicle) {
        return vehicle.getId().hashCode() % 2 == 0 ? 3.0 : -3.0;
    }

    private void applyEmergencyVehicleRules(Vehicle emergency) {
        // Khi đang thực hiện pha vượt an toàn, tiếp tục chuyển làn và không tự chặn
        // bởi chính xe đang được vượt. Va chạm thực tế vẫn được kiểm tra ở advanceLaneChange.
        if (emergencyOvertakingVehicles.contains(emergency)
                && laneChangingVehicles.containsKey(emergency)) {
            emergency.accelerate();
            return;
        }

        Vehicle front = findNearestFrontVehicleInCorridor(emergency, 16);
        if (front != null) {
            double gap = distanceInFront(emergency, front);
            if (gap >= 0 && gap < PROXIMITY_SLOW_DISTANCE + 24) {
                int waited = emergencyBlockedTicks.merge(emergency, 1, Integer::sum);

                // Sau 10 giây mà xe trước vẫn chưa tạo lối, xe ưu tiên được phép
                // chuyển sang làn bên cạnh để vượt, nhưng chỉ khi cả trước và sau đều an toàn.
                if (waited >= EMERGENCY_OVERTAKE_WAIT_TICKS
                        && startEmergencyOvertake(emergency, front)) {
                    emergencyBlockedTicks.put(emergency, 0);
                    return;
                }

                if (gap < PROXIMITY_STOP_DISTANCE + 5) {
                    emergency.stop();
                    blockVehicle(emergency);
                    return;
                }

                double target = Math.max(0.65, front.getSpeed() + 0.30);
                emergency.setSpeed(Math.min(emergency.getSpeed(), target));
                return;
            }
        }

        emergencyBlockedTicks.put(emergency, 0);

        Vehicle risk = findNearestRiskVehicle(emergency);
        if (risk != null && distanceBetween(emergency, risk) < PROXIMITY_STOP_DISTANCE + 5) {
            emergency.stop();
            blockVehicle(emergency);
            return;
        }

        if (hasClearRoadAhead(emergency, CLEAR_ROAD_FAST_DISTANCE * 0.85)) {
            emergency.accelerate();
        }
    }

    private Vehicle findNearestFrontVehicleInCorridor(Vehicle vehicle, double lateralTolerance) {
        return vehicles.stream()
                .filter(other -> other != vehicle)
                .filter(other -> other.getDirection() == vehicle.getDirection())
                .filter(other -> lateralDistanceBetween(vehicle, other) <= lateralTolerance)
                .filter(other -> distanceInFront(vehicle, other) >= 0)
                .min(Comparator.comparingDouble(other -> distanceInFront(vehicle, other)))
                .orElse(null);
    }

    private Vehicle findApproachingEmergency(Vehicle vehicle) {
        if (isEmergency(vehicle)) {
            return null;
        }

        Vehicle nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (Vehicle emergency : vehicles) {
            if (emergency == vehicle || !isEmergencySirenActive(emergency)) {
                continue;
            }
            if (!isEmergencyApproachingSameRoad(vehicle, emergency)) {
                continue;
            }
            double distance = distanceInFront(emergency, vehicle);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = emergency;
            }
        }
        return nearest;
    }

    private void handleEmergencyYield(Vehicle vehicle, Vehicle emergency) {
        if (vehicle instanceof Bicycle) {
            beginOrContinueBicyclePullOver((Bicycle) vehicle, emergency);
            return;
        }

        if (turningVehicles.containsKey(vehicle) || isInsideConflictBox(vehicle)) {
            slowForEmergency(vehicle);
            return;
        }

        double emergencyBehindDistance = distanceInFront(emergency, vehicle);
        double currentOffset = nearestLaneOffsetForPosition(vehicle);
        double targetOffset = nextLaneOffsetToRight(currentOffset);

        PendingLaneChange existing = laneChangingVehicles.get(vehicle);
        if (existing != null) {
            // Tất cả xe thường đang nhường đường phải tiến rất ít về trước để không cắt đầu xe ưu tiên.
            if (existing.emergencyYield) {
                double cap = vehicle instanceof Bus ? 0.90 : 1.00;
                vehicle.setSpeed(Math.min(vehicle.getSpeed(), cap));
                if (emergencyBehindDistance < EMERGENCY_CRITICAL_BEHIND_DISTANCE) {
                    vehicle.setSpeed(Math.min(vehicle.getSpeed(), 0.65));
                }
            } else {
                slowForEmergency(vehicle);
            }
            return;
        }

        if (Math.abs(targetOffset - currentOffset) < 1.0) {
            // Đã ở làn phải nhất: giảm tốc thật sớm để mở đường, không chen ngang.
            if (emergencyBehindDistance < EMERGENCY_CLOSE_BEHIND_DISTANCE) {
                vehicle.stop();
                blockVehicle(vehicle);
            } else {
                slowForEmergency(vehicle);
            }
            return;
        }

        double currentLateral = lateralCoordinate(vehicle);
        double targetLateral = laneCenterForOffset(targetOffset, vehicle.getDirection());
        if (!isEmergencyYieldGapSafe(vehicle, emergency, targetLateral)) {
            if (emergencyBehindDistance < EMERGENCY_CRITICAL_BEHIND_DISTANCE) {
                vehicle.stop();
                blockVehicle(vehicle);
            } else {
                slowForEmergency(vehicle);
            }
            return;
        }

        double length = EMERGENCY_YIELD_LANE_CHANGE_LENGTH
                + Math.abs(targetLateral - currentLateral) * 1.45;
        if (vehicle instanceof Bus) {
            length += 20;
        }
        if (emergencyBehindDistance < EMERGENCY_CLOSE_BEHIND_DISTANCE) {
            // Dạt ngang nhanh hơn, ít tiến dọc hơn để không cắt đầu xe ưu tiên.
            length = Math.max(vehicle instanceof Bus ? 70 : 58,
                    length * (vehicle instanceof Bus ? 0.74 : 0.68));
        }

        laneChangingVehicles.put(vehicle,
                new PendingLaneChange(vehicle.getDirection(), getTurnChoice(vehicle),
                        currentLateral, targetLateral, targetOffset, length, true));
        setLaneChangeIndicators(vehicle, currentLateral, targetLateral);
        soundManager.playTurnSignal();

        double cap = vehicle instanceof Bus ? 0.90 : 1.00;
        if (emergencyBehindDistance < EMERGENCY_CRITICAL_BEHIND_DISTANCE) {
            cap = Math.min(cap, 0.65);
        }
        vehicle.setSpeed(Math.min(vehicle.getSpeed(), cap));
        if (vehicle.getSpeed() < 0.45) {
            vehicle.setSpeed(0.45);
        }
    }

    private boolean updateBicycleEmergencyYield(Bicycle bicycle) {
        Vehicle trackedEmergency = bicycleYieldingToEmergency.get(bicycle);
        if (trackedEmergency == null) {
            return false;
        }

        Vehicle newerEmergency = findApproachingEmergency(bicycle);
        if (newerEmergency != null) {
            trackedEmergency = newerEmergency;
            bicycleYieldingToEmergency.put(bicycle, newerEmergency);
        }

        if (isEmergencyStillPassingBicycle(bicycle, trackedEmergency)) {
            beginOrContinueBicyclePullOver(bicycle, trackedEmergency);
            return true;
        }

        bicycleYieldingToEmergency.remove(bicycle);
        return startBicycleReturnToInnerLane(bicycle);
    }

    private void beginOrContinueBicyclePullOver(Bicycle bicycle, Vehicle emergency) {
        bicycleYieldingToEmergency.put(bicycle, emergency);

        if (turningVehicles.containsKey(bicycle) || isInsideConflictBox(bicycle)) {
            bicycle.stop();
            blockVehicle(bicycle);
            return;
        }

        PendingLaneChange existing = laneChangingVehicles.get(bicycle);
        if (existing != null) {
            slowForEmergency(bicycle);
            return;
        }

        double targetLateral = laneCenterForOffset(BICYCLE_PULL_OVER_OFFSET, bicycle.getDirection());
        double currentLateral = lateralCoordinate(bicycle);
        if (Math.abs(targetLateral - currentLateral) <= LANE_CHANGE_FINISH_TOLERANCE) {
            assignedLaneOffsets.put(bicycle, BICYCLE_PULL_OVER_OFFSET);
            bicycle.stop();
            blockVehicle(bicycle);
            return;
        }

        if (!isEmergencyYieldGapSafe(bicycle, emergency, targetLateral)) {
            if (distanceInFront(emergency, bicycle) < 62) {
                bicycle.stop();
                blockVehicle(bicycle);
            } else {
                slowForEmergency(bicycle);
            }
            return;
        }

        laneChangingVehicles.put(bicycle,
                new PendingLaneChange(bicycle.getDirection(), getTurnChoice(bicycle),
                        currentLateral, targetLateral, BICYCLE_PULL_OVER_OFFSET,
                        82 + Math.abs(targetLateral - currentLateral) * 1.25, true));
        setLaneChangeIndicators(bicycle, currentLateral, targetLateral);
        bicycle.setSpeed(Math.min(Math.max(0.65, bicycle.getSpeed()), 1.00));
        soundManager.playTurnSignal();
    }

    private boolean isEmergencyStillPassingBicycle(Bicycle bicycle, Vehicle emergency) {
        if (emergency == null || !vehicles.contains(emergency) || !isEmergencySirenActive(emergency)) {
            return false;
        }
        if (emergency.getDirection() != bicycle.getDirection()) {
            return false;
        }
        // Dương: xe ưu tiên còn ở sau. Âm nhỏ: đang song song/vừa đi qua.
        double bicycleAheadOfEmergency = distanceInFront(emergency, bicycle);
        return bicycleAheadOfEmergency > -EMERGENCY_PASS_CLEARANCE;
    }

    private boolean startBicycleReturnToInnerLane(Bicycle bicycle) {
        if (laneChangingVehicles.containsKey(bicycle) || turningVehicles.containsKey(bicycle)) {
            return true;
        }

        double targetLateral = laneCenterForOffset(INNER_LANE_OFFSET, bicycle.getDirection());
        double currentLateral = lateralCoordinate(bicycle);
        if (Math.abs(targetLateral - currentLateral) <= LANE_CHANGE_FINISH_TOLERANCE) {
            assignedLaneOffsets.put(bicycle, INNER_LANE_OFFSET);
            bicycle.setState(VehicleState.MOVING);
            bicycle.accelerate();
            return false;
        }

        if (!isLaneChangeGapSafe(bicycle, targetLateral)) {
            bicycle.stop();
            blockVehicle(bicycle);
            return true;
        }

        laneChangingVehicles.put(bicycle,
                new PendingLaneChange(bicycle.getDirection(), getTurnChoice(bicycle),
                        currentLateral, targetLateral, INNER_LANE_OFFSET,
                        110 + Math.abs(targetLateral - currentLateral) * 1.5, false));
        setLaneChangeIndicators(bicycle, currentLateral, targetLateral);
        bicycle.setSpeed(0.65);
        soundManager.playTurnSignal();
        return true;
    }

    private boolean startEmergencyOvertake(Vehicle emergency, Vehicle frontVehicle) {
        if (turningVehicles.containsKey(emergency)
                || laneChangingVehicles.containsKey(emergency)
                || isDeepInsideConflictBox(emergency)) {
            return false;
        }

        double currentOffset = nearestLaneOffsetForPosition(emergency);
        double[] candidates;
        if (currentOffset >= INNER_LANE_OFFSET - 4) {
            candidates = new double[] {MIDDLE_LANE_OFFSET, OUTER_LANE_OFFSET};
        } else if (currentOffset >= MIDDLE_LANE_OFFSET - 4) {
            candidates = new double[] {OUTER_LANE_OFFSET, INNER_LANE_OFFSET};
        } else {
            candidates = new double[] {MIDDLE_LANE_OFFSET, INNER_LANE_OFFSET};
        }

        for (double targetOffset : candidates) {
            double targetLateral = laneCenterForOffset(targetOffset, emergency.getDirection());
            if (!isEmergencyOvertakeGapSafe(emergency, frontVehicle, targetLateral)) {
                continue;
            }

            double currentLateral = lateralCoordinate(emergency);
            double length = 125 + Math.abs(targetLateral - currentLateral) * 1.5;
            if (emergency instanceof FireTruck) {
                length += 32;
            } else if (emergency instanceof Ambulance) {
                length += 16;
            }

            laneChangingVehicles.put(emergency,
                    new PendingLaneChange(emergency.getDirection(), getTurnChoice(emergency),
                            currentLateral, targetLateral, targetOffset, length, false));
            setLaneChangeIndicators(emergency, currentLateral, targetLateral);
            emergencyOvertakingVehicles.add(emergency);
            emergency.setSpeed(Math.max(1.0, emergency.getSpeed()));
            soundManager.playHorn();
            soundManager.playTurnSignal();
            return true;
        }
        return false;
    }

    private boolean isEmergencyOvertakeGapSafe(Vehicle emergency, Vehicle frontVehicle, double targetLateral) {
        for (Vehicle other : vehicles) {
            if (other == emergency || other == frontVehicle
                    || other.getDirection() != emergency.getDirection()) {
                continue;
            }
            if (Math.abs(lateralCoordinate(other) - targetLateral) > LANE_TARGET_TOLERANCE) {
                continue;
            }

            double frontGap = distanceInFront(emergency, other);
            if (frontGap >= 0 && frontGap < EMERGENCY_OVERTAKE_FRONT_GAP) {
                return false;
            }
            double rearGap = distanceInFront(other, emergency);
            if (rearGap >= 0 && rearGap < EMERGENCY_OVERTAKE_REAR_GAP) {
                return false;
            }
        }
        return true;
    }

    private boolean isEmergencyYieldGapSafe(Vehicle vehicle, Vehicle emergency, double targetLateral) {
        for (Vehicle other : vehicles) {
            if (other == vehicle || other == emergency || other.getDirection() != vehicle.getDirection()) {
                continue;
            }
            if (Math.abs(lateralCoordinate(other) - targetLateral) > 18) {
                continue;
            }

            double frontGap = distanceInFront(vehicle, other);
            if (frontGap >= 0 && frontGap < EMERGENCY_MIN_FRONT_GAP) {
                return false;
            }
            double rearGap = distanceInFront(other, vehicle);
            if (rearGap >= 0 && rearGap < EMERGENCY_MIN_REAR_GAP) {
                return false;
            }
        }
        return true;
    }

    private double nextLaneOffsetToRight(double currentOffset) {
        if (currentOffset < (OUTER_LANE_OFFSET + MIDDLE_LANE_OFFSET) / 2.0) {
            return MIDDLE_LANE_OFFSET;
        }
        if (currentOffset < (MIDDLE_LANE_OFFSET + INNER_LANE_OFFSET) / 2.0) {
            return INNER_LANE_OFFSET;
        }
        return INNER_LANE_OFFSET;
    }

    private void slowForEmergency(Vehicle vehicle) {
        if (vehicle.getSpeed() > EMERGENCY_YIELD_SLOW_SPEED) {
            vehicle.brake();
        }
    }

    private boolean isEmergencyApproachingSameRoad(Vehicle vehicle, Vehicle emergency) {
        if (vehicle.getDirection() != emergency.getDirection()) {
            return false;
        }

        double lateralDistance = lateralDistanceBetween(vehicle, emergency);
        double frontDistanceFromEmergency = distanceInFront(emergency, vehicle);
        return lateralDistance <= EMERGENCY_SAME_ROAD_WIDTH
                && frontDistanceFromEmergency >= 0
                && frontDistanceFromEmergency < EMERGENCY_YIELD_DISTANCE;
    }

    private boolean isEmergencySirenActive(Vehicle vehicle) {
        if (vehicle instanceof Ambulance) {
            return ((Ambulance) vehicle).isSirenOn();
        }
        if (vehicle instanceof FireTruck) {
            return ((FireTruck) vehicle).isSirenOn();
        }
        return false;
    }

    private double lateralDistanceBetween(Vehicle first, Vehicle second) {
        switch (first.getDirection()) {
            case EAST:
            case WEST:
                return Math.abs(first.getPosition().getY() - second.getPosition().getY());
            case NORTH:
            case SOUTH:
                return Math.abs(first.getPosition().getX() - second.getPosition().getX());
            default:
                return Double.MAX_VALUE;
        }
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

    private void prepareLaneChangeIfNeeded(Vehicle vehicle) {
        if (vehiclesThatTurned.contains(vehicle)
                || turningVehicles.containsKey(vehicle)
                || laneChangingVehicles.containsKey(vehicle)) {
            return;
        }

        TurnChoice choice = getTurnChoice(vehicle);
        if (choice == TurnChoice.STRAIGHT) {
            return;
        }

        double distance = distanceToIntersection(vehicle);
        if (distance > LANE_CHANGE_START_DISTANCE || distance < 48) {
            return;
        }

        double targetOffset = requiredApproachLaneOffset(choice);
        double targetLateral = laneCenterForOffset(targetOffset, vehicle.getDirection());
        double currentLateral = lateralCoordinate(vehicle);
        double delta = targetLateral - currentLateral;

        if (Math.abs(delta) <= LANE_CHANGE_FINISH_TOLERANCE) {
            assignedLaneOffsets.put(vehicle, targetOffset);
            vehicle.setVisualHeadingDegrees(headingForDirection(vehicle.getDirection()));
            return;
        }

        if (!isLaneChangeGapSafe(vehicle, targetLateral)) {
            // Chờ khoảng trống ngay trên làn hiện tại, không nhảy ngang sang làn rẽ.
            if (distance < 210 && vehicle.getSpeed() > 1.1) {
                vehicle.brake();
            }
            return;
        }

        double length = Math.max(LANE_CHANGE_MIN_LENGTH, 125 + Math.abs(delta) * 1.15);
        if (vehicle instanceof Bus || vehicle instanceof FireTruck) {
            length += 35;
        } else if (vehicle instanceof Ambulance) {
            length += 18;
        }

        laneChangingVehicles.put(vehicle,
                new PendingLaneChange(vehicle.getDirection(), choice,
                        currentLateral, targetLateral, targetOffset, length, false));
        setLaneChangeIndicators(vehicle, currentLateral, targetLateral);
        soundManager.playTurnSignal();
    }

    private void advanceLaneChange(Vehicle vehicle) {
        PendingLaneChange change = laneChangingVehicles.get(vehicle);
        if (change == null) {
            return;
        }

        // Nếu khoảng trống của làn đích tạm thời bị đóng, xe tiếp tục đi thẳng trên làn hiện tại
        // và giảm tốc nhẹ; tuyệt đối không snap/teleport sang làn bên cạnh.
        Vehicle yieldingEmergency = change.emergencyYield ? findApproachingEmergency(vehicle) : null;
        boolean safe = change.emergencyYield
                ? isEmergencyYieldGapSafe(vehicle, yieldingEmergency, change.targetLateral)
                : isLaneChangeGapSafe(vehicle, change.targetLateral);
        double oldX = vehicle.getPosition().getX();
        double oldY = vehicle.getPosition().getY();

        if (!safe) {
            if (vehicle.getSpeed() > 1.0) {
                vehicle.brake();
            }
            vehicle.update();
            vehicle.setVisualHeadingDegrees(headingForDirection(change.direction));
            return;
        }

        vehicle.update();
        double nextTravelled = change.travelled + Math.max(0.65, vehicle.getSpeed());
        double t = Math.min(1.0, nextTravelled / change.longitudinalLength);
        double smooth = t * t * (3.0 - 2.0 * t); // S-curve: vào làn và trả lái mượt
        double lateral = change.startLateral
                + (change.targetLateral - change.startLateral) * smooth;
        setLateralCoordinate(vehicle, lateral);

        // Kiểm tra vị trí dự kiến trước khi chấp nhận bước chuyển làn.
        // Nếu có nguy cơ chồng xe, giữ nguyên vị trí cũ và giảm tốc; không teleport.
        if (!isCurrentPositionSafe(vehicle)) {
            vehicle.getPosition().setX(oldX);
            vehicle.getPosition().setY(oldY);
            vehicle.brake();
            vehicle.setVisualHeadingDegrees(headingForDirection(change.direction));
            return;
        }
        change.travelled = nextTravelled;

        double dx = vehicle.getPosition().getX() - oldX;
        double dy = vehicle.getPosition().getY() - oldY;
        if (Math.hypot(dx, dy) > 0.0001) {
            vehicle.setVisualHeadingDegrees(Math.toDegrees(Math.atan2(dy, dx)));
        }

        if (t >= 1.0) {
            setLateralCoordinate(vehicle, change.targetLateral);
            assignedLaneOffsets.put(vehicle, change.targetOffset);
            laneChangingVehicles.remove(vehicle);
            emergencyOvertakingVehicles.remove(vehicle);
            clearLaneChangeIndicators(vehicle);
            vehicle.setVisualHeadingDegrees(headingForDirection(change.direction));
            vehicle.setState(VehicleState.MOVING);
        }
    }

    private boolean isLaneChangeGapSafe(Vehicle vehicle, double targetLateral) {
        for (Vehicle other : vehicles) {
            if (other == vehicle || other.getDirection() != vehicle.getDirection()) {
                continue;
            }

            double otherLateral = lateralCoordinate(other);
            if (Math.abs(otherLateral - targetLateral) > LANE_TARGET_TOLERANCE) {
                continue;
            }

            double frontGap = distanceInFront(vehicle, other);
            if (frontGap >= 0 && frontGap < LANE_CHANGE_FRONT_GAP) {
                return false;
            }

            double rearGap = distanceInFront(other, vehicle);
            if (rearGap >= 0 && rearGap < LANE_CHANGE_REAR_GAP) {
                return false;
            }
        }
        return true;
    }

    private boolean isInRequiredTurnLane(Vehicle vehicle, TurnChoice choice) {
        if (choice == TurnChoice.STRAIGHT) {
            return true;
        }
        double expected = laneCenterForOffset(requiredApproachLaneOffset(choice), vehicle.getDirection());
        return Math.abs(lateralCoordinate(vehicle) - expected) <= 4.0;
    }

    private double requiredApproachLaneOffset(TurnChoice choice) {
        if (choice == TurnChoice.LEFT) {
            return LEFT_TURN_LANE_OFFSET;
        }
        if (choice == TurnChoice.RIGHT) {
            return RIGHT_TURN_LANE_OFFSET;
        }
        return MIDDLE_LANE_OFFSET;
    }

    private double lateralCoordinate(Vehicle vehicle) {
        switch (vehicle.getDirection()) {
            case EAST:
            case WEST:
                return vehicle.getPosition().getY();
            case NORTH:
            case SOUTH:
                return vehicle.getPosition().getX();
            default:
                return 0;
        }
    }

    private void setLateralCoordinate(Vehicle vehicle, double value) {
        switch (vehicle.getDirection()) {
            case EAST:
            case WEST:
                vehicle.getPosition().setY(value);
                break;
            case NORTH:
            case SOUTH:
                vehicle.getPosition().setX(value);
                break;
            default:
                break;
        }
    }

    private void applyTurnIfNeeded(Vehicle vehicle) {
        if (vehiclesThatTurned.contains(vehicle)) {
            return;
        }

        TurnChoice turnChoice = getTurnChoice(vehicle);
        if (turnChoice == TurnChoice.STRAIGHT) {
            if (hasPassedIntersection(vehicle)) {
                vehiclesThatTurned.add(vehicle);
            }
            return;
        }

        // Xe phải chuyển làn từ từ và vào đúng làn rẽ trước khi bắt đầu cua.
        // Không được đổi tọa độ ngang tức thời ngay tại miệng giao lộ.
        if (laneChangingVehicles.containsKey(vehicle) || !isInRequiredTurnLane(vehicle, turnChoice)) {
            prepareLaneChangeIfNeeded(vehicle);
            return;
        }

        if (!isAtSteeringPoint(vehicle, turnChoice)) {
            return;
        }

        if (turnChoice == TurnChoice.LEFT && !isEmergency(vehicle) && hasOpposingTrafficNearIntersection(vehicle)) {
            vehicle.brake();
            blockVehicle(vehicle);
            return;
        }

        Direction newDirection = directionAfterTurn(vehicle.getDirection(), turnChoice);
        double targetX = targetLaneX(vehicle, newDirection, turnChoice);
        double targetY = targetLaneY(vehicle, newDirection, turnChoice);
        if (!canTurnSafely(vehicle, targetX, targetY)) {
            vehicle.brake();
            blockVehicle(vehicle);
            return;
        }

        double startX = vehicle.getPosition().getX();
        double startY = vehicle.getPosition().getY();
        // Control point đầu đặt gần xe hơn để góc lái bắt đầu thay đổi ngay sau điểm kích hoạt,
        // thay vì xe tiếp tục chạy thẳng sâu vào nhánh mới rồi mới quay.
        double leadIn = turnChoice == TurnChoice.LEFT ? 68 : 48;
        // Control point cuối đủ dài để xe trả lái và song song với đường mới trước khi ra khỏi cung.
        double leadOut = turnChoice == TurnChoice.LEFT ? 82 : 68;
        if (turnChoice == TurnChoice.RIGHT && willExitToOuterLane(vehicle)) {
            leadIn += 8;
            leadOut += 10;
        }
        if (vehicle instanceof Bus || vehicle instanceof FireTruck) {
            // Xe dài vẫn cua rộng nhưng không trì hoãn thời điểm bắt đầu đánh lái.
            leadIn += 8;
            leadOut += 16;
        } else if (vehicle instanceof Ambulance) {
            leadIn += 4;
            leadOut += 8;
        }
        double control1X = startX + dirX(vehicle.getDirection()) * leadIn;
        double control1Y = startY + dirY(vehicle.getDirection()) * leadIn;
        double control2X = targetX - dirX(newDirection) * leadOut;
        double control2Y = targetY - dirY(newDirection) * leadOut;
        int steps = turnStepsFor(vehicle, turnChoice);

        // Cubic Bezier: đi thẳng tới điểm đánh lái -> quay đầu xe từ từ -> trả lái đúng làn ra.
        // Mỗi làn/loại xe có start và target lane riêng nên không còn dồn vào một đường cong.
        PendingTurn pendingTurn = new PendingTurn(newDirection, turnChoice, startX, startY,
                control1X, control1Y, control2X, control2Y, targetX, targetY, steps);
        // Ghi nhớ tốc độ lúc bắt đầu rẽ. Khi xe ra làn mới, nếu phía trước an toàn,
        // tốc độ trong làn mới không được thấp hơn tốc độ khi đang rẽ.
        pendingTurn.maxSpeedDuringTurn = vehicle.getSpeed();
        turningVehicles.put(vehicle, pendingTurn);
        soundManager.playTurnSignal();
    }

    private void slowWhenApproachingIntersection(Vehicle vehicle) {
        if (distanceToIntersection(vehicle) < 150 && vehicle.getSpeed() > 2.0) {
            vehicle.brake();
        }
    }

    private double distanceToIntersection(Vehicle vehicle) {
        switch (vehicle.getDirection()) {
            case EAST:
                return Math.max(0, CENTER_X - vehicle.getPosition().getX());
            case WEST:
                return Math.max(0, vehicle.getPosition().getX() - CENTER_X);
            case NORTH:
                return Math.max(0, vehicle.getPosition().getY() - CENTER_Y);
            case SOUTH:
                return Math.max(0, CENTER_Y - vehicle.getPosition().getY());
            default:
                return Double.MAX_VALUE;
        }
    }

    private boolean hasOpposingTrafficNearIntersection(Vehicle vehicle) {
        Direction opposingDirection = oppositeDirection(vehicle.getDirection());
        return vehicles.stream()
                .filter(other -> other != vehicle)
                .filter(other -> other.getDirection() == opposingDirection)
                .anyMatch(other -> distanceToIntersection(other) < 170);
    }

    private Direction oppositeDirection(Direction direction) {
        switch (direction) {
            case EAST:
                return Direction.WEST;
            case WEST:
                return Direction.EAST;
            case NORTH:
                return Direction.SOUTH;
            case SOUTH:
                return Direction.NORTH;
            default:
                return direction;
        }
    }

    private boolean isAtSteeringPoint(Vehicle vehicle, TurnChoice choice) {
        // Bắt đầu đánh lái TRƯỚC khi xe tới đường cần rẽ.
        // Xe không đi tới tâm ngã tư rồi mới đổi hướng nữa, mà vào cua sớm và mượt hơn.
        double trigger = choice == TurnChoice.RIGHT ? RIGHT_TURN_START_DISTANCE : LEFT_TURN_START_DISTANCE;
        // Nếu xe sẽ nhập vào làn ngoài ở đường mới thì cho bắt đầu cua sớm hơn.
        // Như vậy xe không đi quá sát ngã tư rồi mới xoay, tránh cảm giác rẽ muộn/giật.
        if (choice == TurnChoice.RIGHT && willExitToOuterLane(vehicle)) {
            trigger += OUTER_LANE_EARLY_TURN_EXTRA;
        }
        switch (vehicle.getDirection()) {
            case EAST:
                return vehicle.getPosition().getX() >= CENTER_X - trigger;
            case WEST:
                return vehicle.getPosition().getX() <= CENTER_X + trigger;
            case NORTH:
                return vehicle.getPosition().getY() <= CENTER_Y + trigger;
            case SOUTH:
                return vehicle.getPosition().getY() >= CENTER_Y - trigger;
            default:
                return false;
        }
    }

    private boolean willExitToOuterLane(Vehicle vehicle) {
        Direction exitDirection = directionAfterTurn(vehicle.getDirection(), TurnChoice.RIGHT);
        double expectedLane = laneCenterForOffset(OUTER_LANE_OFFSET, exitDirection);
        double actualLane = laneCenterForExit(vehicle, exitDirection);
        return Math.abs(expectedLane - actualLane) < 4.0;
    }

    private boolean hasPassedIntersection(Vehicle vehicle) {
        switch (vehicle.getDirection()) {
            case EAST:
                return vehicle.getPosition().getX() > CENTER_X + CONFLICT_RESERVATION_DISTANCE;
            case WEST:
                return vehicle.getPosition().getX() < CENTER_X - CONFLICT_RESERVATION_DISTANCE;
            case NORTH:
                return vehicle.getPosition().getY() < CENTER_Y - CONFLICT_RESERVATION_DISTANCE;
            case SOUTH:
                return vehicle.getPosition().getY() > CENTER_Y + CONFLICT_RESERVATION_DISTANCE;
            default:
                return false;
        }
    }

    private TurnChoice getTurnChoice(Vehicle vehicle) {
        TurnChoice existingChoice = turnChoices.get(vehicle);
        if (existingChoice != null) {
            return existingChoice;
        }

        int routeIndex = random.nextInt(100);
        TurnChoice choice;
        if (vehicle instanceof Bicycle) {
            // Xe đạp chỉ đi làn trong, không ép chuyển sang làn trái để rẽ trái.
            choice = routeIndex < 68 ? TurnChoice.STRAIGHT : TurnChoice.RIGHT;
        } else if (routeIndex < 45) {
            choice = TurnChoice.STRAIGHT;
        } else if (routeIndex < 72) {
            choice = TurnChoice.RIGHT;
        } else {
            choice = TurnChoice.LEFT;
        }
        turnChoices.put(vehicle, choice);
        return choice;
    }

    private Direction directionAfterTurn(Direction direction, TurnChoice turnChoice) {
        switch (direction) {
            case EAST:
                return turnChoice == TurnChoice.RIGHT ? Direction.SOUTH : Direction.NORTH;
            case WEST:
                return turnChoice == TurnChoice.RIGHT ? Direction.NORTH : Direction.SOUTH;
            case NORTH:
                return turnChoice == TurnChoice.RIGHT ? Direction.EAST : Direction.WEST;
            case SOUTH:
                return turnChoice == TurnChoice.RIGHT ? Direction.WEST : Direction.EAST;
            default:
                return direction;
        }
    }

    private void advanceTurn(Vehicle vehicle) {
        PendingTurn pendingTurn = turningVehicles.get(vehicle);
        if (pendingTurn == null) {
            return;
        }

        Vehicle riskVehicle = findNearestRiskVehicle(vehicle);
        boolean fastTurn = isTurnPathClearForFastMove(vehicle, pendingTurn);
        if (riskVehicle != null) {
            double distance = distanceBetween(vehicle, riskVehicle);
            if (distance < PROXIMITY_STOP_DISTANCE) {
                vehicle.stop();
                vehicle.setState(VehicleState.WAITING);
                return;
            }
            pendingTurn.slowTick++;
            if (pendingTurn.slowTick % 2 != 0) {
                vehicle.setSpeed(Math.min(vehicle.getSpeed(), 0.8));
                return;
            }
        } else {
            vehicle.setState(VehicleState.MOVING);
        }

        pendingTurn.maxSpeedDuringTurn = Math.max(pendingTurn.maxSpeedDuringTurn, vehicle.getSpeed());

        // Khi đang rẽ mà phía sau/vùng rẽ không có xe cắt qua thì tăng bước di chuyển,
        // giúp xe thoát khỏi cua nhanh hơn nhưng vẫn không bỏ qua kiểm tra khoảng cách an toàn.
        pendingTurn.currentStep += fastTurn ? 2 : 1;
        double t = Math.min(1.0, (double) pendingTurn.currentStep / pendingTurn.totalSteps);
        double oneMinusT = 1.0 - t;

        // Cubic Bezier curve with long lead-in and lead-out.
        // This makes the front of the vehicle point into the new road first, then the body follows.
        double nextX = oneMinusT * oneMinusT * oneMinusT * pendingTurn.startX
                + 3 * oneMinusT * oneMinusT * t * pendingTurn.control1X
                + 3 * oneMinusT * t * t * pendingTurn.control2X
                + t * t * t * pendingTurn.targetX;
        double nextY = oneMinusT * oneMinusT * oneMinusT * pendingTurn.startY
                + 3 * oneMinusT * oneMinusT * t * pendingTurn.control1Y
                + 3 * oneMinusT * t * t * pendingTurn.control2Y
                + t * t * t * pendingTurn.targetY;

        // Tangent of the cubic Bezier curve, used to rotate the vehicle smoothly while it turns.
        double tangentX = 3 * oneMinusT * oneMinusT * (pendingTurn.control1X - pendingTurn.startX)
                + 6 * oneMinusT * t * (pendingTurn.control2X - pendingTurn.control1X)
                + 3 * t * t * (pendingTurn.targetX - pendingTurn.control2X);
        double tangentY = 3 * oneMinusT * oneMinusT * (pendingTurn.control1Y - pendingTurn.startY)
                + 6 * oneMinusT * t * (pendingTurn.control2Y - pendingTurn.control1Y)
                + 3 * t * t * (pendingTurn.targetY - pendingTurn.control2Y);
        vehicle.setVisualHeadingDegrees(Math.toDegrees(Math.atan2(tangentY, tangentX)));

        vehicle.getPosition().setX(nextX);
        vehicle.getPosition().setY(nextY);

        if (t >= 1.0) {
            vehicle.getPosition().setX(pendingTurn.targetX);
            vehicle.getPosition().setY(pendingTurn.targetY);
            vehicle.setDirection(pendingTurn.targetDirection);
            vehicle.setVisualHeadingDegrees(headingForDirection(pendingTurn.targetDirection));

            // Khi vào làn đường mới: giữ tốc độ >= tốc độ khi rẽ nếu phía trước an toàn.
            // Không tăng tốc cưỡng bức nếu ngay trước mặt có xe/đèn đỏ để tránh đâm nhau.
            double exitSpeed = Math.max(vehicle.getSpeed(), pendingTurn.maxSpeedDuringTurn);
            if (hasClearRoadAhead(vehicle, SAFE_DISTANCE * 1.35) && !mustStopAtRedLight(vehicle)) {
                vehicle.setSpeed(exitSpeed);
                vehicle.accelerate();
            }

            assignedLaneOffsets.put(vehicle, normalLaneOffsetFor(vehicle));
            turningVehicles.remove(vehicle);
            laneChangingVehicles.remove(vehicle);
            clearLaneChangeIndicators(vehicle);
            vehiclesThatTurned.add(vehicle);
        }
    }

    private boolean canTurnSafely(Vehicle vehicle, double targetX, double targetY) {
        for (Vehicle other : vehicles) {
            if (other == vehicle) {
                continue;
            }
            double dx = targetX - other.getPosition().getX();
            double dy = targetY - other.getPosition().getY();
            if (Math.sqrt(dx * dx + dy * dy) < SAFE_DISTANCE * 1.10) {
                if (vehicle instanceof Bus && !(other instanceof Bus) && !isEmergency(other)) {
                    other.brake();
                    blockVehicle(other);
                    return true;
                }
                return false;
            }
        }
        return true;
    }

    private double dirX(Direction direction) {
        switch (direction) {
            case EAST:
                return 1;
            case WEST:
                return -1;
            default:
                return 0;
        }
    }

    private double dirY(Direction direction) {
        switch (direction) {
            case SOUTH:
                return 1;
            case NORTH:
                return -1;
            default:
                return 0;
        }
    }

    private int turnStepsFor(Vehicle vehicle, TurnChoice turnChoice) {
        int baseSteps = turnChoice == TurnChoice.LEFT ? 72 : 58;
        if (vehicle instanceof Bus) {
            baseSteps += 10;
        }
        return baseSteps;
    }

    private double rightTurnExitDistance(Vehicle vehicle, TurnChoice turnChoice) {
        if (turnChoice != TurnChoice.RIGHT) {
            return TURN_EXIT_OFFSET;
        }
        // Kết thúc cung ngay đầu phần đường thẳng của nhánh mới. Từ đây vehicle.update()
        // mới tiếp tục cho xe chạy thẳng, nên xe không còn đi sâu vào đường mới rồi mới xoay.
        double exitDistance = willExitToOuterLane(vehicle) ? 164 : 154;
        if (vehicle instanceof Bus || vehicle instanceof FireTruck) {
            exitDistance += 10;
        } else if (vehicle instanceof Ambulance) {
            exitDistance += 5;
        }
        return exitDistance;
    }

    private double targetLaneX(Vehicle vehicle, Direction direction, TurnChoice turnChoice) {
        double exitDistance = rightTurnExitDistance(vehicle, turnChoice);
        switch (direction) {
            case EAST:
                return CENTER_X + exitDistance;
            case WEST:
                return CENTER_X - exitDistance;
            case NORTH:
            case SOUTH:
                return laneCenterForExit(vehicle, direction);
            default:
                return CENTER_X;
        }
    }

    private double targetLaneY(Vehicle vehicle, Direction direction, TurnChoice turnChoice) {
        double exitDistance = rightTurnExitDistance(vehicle, turnChoice);
        switch (direction) {
            case EAST:
            case WEST:
                return laneCenterForExit(vehicle, direction);
            case NORTH:
                return CENTER_Y - exitDistance;
            case SOUTH:
                return CENTER_Y + exitDistance;
            default:
                return CENTER_Y;
        }
    }

    private void preventCollisions() {
        for (int i = 0; i < vehicles.size(); i++) {
            Vehicle vehicleA = vehicles.get(i);

            for (int j = i + 1; j < vehicles.size(); j++) {
                Vehicle vehicleB = vehicles.get(j);

                if (collisionDetector.isColliding(vehicleA, vehicleB)) {
                    if (isInsideConflictBox(vehicleA) || isInsideConflictBox(vehicleB)
                            || turningVehicles.containsKey(vehicleA) || turningVehicles.containsKey(vehicleB)) {
                        resolveJunctionCollision(vehicleA, vehicleB);
                    } else {
                        vehicleA.brake();
                        vehicleB.brake();
                        separateVehicles(vehicleA, vehicleB);
                    }
                    soundManager.playHorn();
                }
            }
        }
    }


    private void resolveJunctionCollision(Vehicle vehicleA, Vehicle vehicleB) {
        Vehicle yielding = chooseYieldingVehicle(vehicleA, vehicleB);
        Vehicle priority = yielding == vehicleA ? vehicleB : vehicleA;

        yielding.stop();
        yielding.setState(VehicleState.WAITING);
        // Nếu đã chạm nhau thì cả xe ưu tiên cũng phải giảm tốc. Không tăng tốc xuyên qua xe khác.
        priority.brake();
    }

    private Vehicle chooseYieldingVehicle(Vehicle vehicleA, Vehicle vehicleB) {
        if (isEmergency(vehicleA) && !isEmergency(vehicleB)) {
            return vehicleB;
        }
        if (isEmergency(vehicleB) && !isEmergency(vehicleA)) {
            return vehicleA;
        }
        // Bus không có làn riêng nữa, nhưng được ưu tiên hơn ô tô/xe máy khi nhập cùng làn.
        if (vehicleA instanceof Bus && !(vehicleB instanceof Bus) && !isEmergency(vehicleB)) {
            return vehicleB;
        }
        if (vehicleB instanceof Bus && !(vehicleA instanceof Bus) && !isEmergency(vehicleA)) {
            return vehicleA;
        }
        if (willMergeIntoSameLane(vehicleA, vehicleB)) {
            return shouldYieldForMerge(vehicleA, vehicleB) ? vehicleA : vehicleB;
        }
        if (turningVehicles.containsKey(vehicleA) && !turningVehicles.containsKey(vehicleB)) {
            return vehicleB;
        }
        if (turningVehicles.containsKey(vehicleB) && !turningVehicles.containsKey(vehicleA)) {
            return vehicleA;
        }
        double distanceA = Math.hypot(vehicleA.getPosition().getX() - CENTER_X, vehicleA.getPosition().getY() - CENTER_Y);
        double distanceB = Math.hypot(vehicleB.getPosition().getX() - CENTER_X, vehicleB.getPosition().getY() - CENTER_Y);
        return distanceA <= distanceB ? vehicleB : vehicleA;
    }

    private void moveBackward(Vehicle vehicle, double amount) {
        switch (vehicle.getDirection()) {
            case EAST:
                vehicle.getPosition().setX(vehicle.getPosition().getX() - amount);
                break;
            case WEST:
                vehicle.getPosition().setX(vehicle.getPosition().getX() + amount);
                break;
            case NORTH:
                vehicle.getPosition().setY(vehicle.getPosition().getY() + amount);
                break;
            case SOUTH:
                vehicle.getPosition().setY(vehicle.getPosition().getY() - amount);
                break;
            default:
                break;
        }
    }

    private void separateVehicles(Vehicle vehicleA, Vehicle vehicleB) {
        // Không đẩy ngang/teleport khi va chạm. Xe phía sau/phải nhường sẽ dừng mềm, xe ưu tiên đi tiếp.
        Vehicle yielding = chooseYieldingVehicle(vehicleA, vehicleB);
        yielding.stop();
        blockVehicle(yielding);
    }

    private void keepVehicleOnRoad(Vehicle vehicle) {
        if (laneChangingVehicles.containsKey(vehicle) || turningVehicles.containsKey(vehicle)) {
            return;
        }

        double target = rightLaneCenter(vehicle);
        switch (vehicle.getDirection()) {
            case EAST:
            case WEST: {
                double current = vehicle.getPosition().getY();
                double correction = clamp(target - current,
                        -LANE_CENTER_CORRECTION_PER_TICK,
                        LANE_CENTER_CORRECTION_PER_TICK);
                vehicle.getPosition().setY(current + correction);
                break;
            }
            case NORTH:
            case SOUTH: {
                double current = vehicle.getPosition().getX();
                double correction = clamp(target - current,
                        -LANE_CENTER_CORRECTION_PER_TICK,
                        LANE_CENTER_CORRECTION_PER_TICK);
                vehicle.getPosition().setX(current + correction);
                break;
            }
            default:
                break;
        }
    }

    private double rightLaneCenter(Vehicle vehicle) {
        return laneCenterFor(vehicle, vehicle.getDirection());
    }

    private double laneCenterFor(Vehicle vehicle, Direction direction) {
        return laneCenterForOffset(currentLaneOffsetFor(vehicle), direction);
    }

    private double laneCenterForExit(Vehicle vehicle, Direction direction) {
        return laneCenterForOffset(normalLaneOffsetFor(vehicle), direction);
    }

    private double currentLaneOffsetFor(Vehicle vehicle) {
        return assignedLaneOffsets.getOrDefault(vehicle, normalLaneOffsetFor(vehicle));
    }

    private double laneCenterForOffset(double offset, Direction direction) {
        switch (direction) {
            case EAST:
                return CENTER_Y + offset;
            case WEST:
                return CENTER_Y - offset;
            case NORTH:
                return CENTER_X + offset;
            case SOUTH:
                return CENTER_X - offset;
            default:
                return 0;
        }
    }

    private double nearestLaneOffsetForPosition(Vehicle vehicle) {
        double rawOffset;
        switch (vehicle.getDirection()) {
            case EAST:
                rawOffset = vehicle.getPosition().getY() - CENTER_Y;
                break;
            case WEST:
                rawOffset = CENTER_Y - vehicle.getPosition().getY();
                break;
            case NORTH:
                rawOffset = vehicle.getPosition().getX() - CENTER_X;
                break;
            case SOUTH:
                rawOffset = CENTER_X - vehicle.getPosition().getX();
                break;
            default:
                rawOffset = MIDDLE_LANE_OFFSET;
                break;
        }

        double best = OUTER_LANE_OFFSET;
        double bestError = Math.abs(rawOffset - best);
        double middleError = Math.abs(rawOffset - MIDDLE_LANE_OFFSET);
        if (middleError < bestError) {
            best = MIDDLE_LANE_OFFSET;
            bestError = middleError;
        }
        double innerError = Math.abs(rawOffset - INNER_LANE_OFFSET);
        if (innerError < bestError) {
            best = INNER_LANE_OFFSET;
        }
        return best;
    }

    private boolean isCurrentPositionSafe(Vehicle vehicle) {
        for (Vehicle other : vehicles) {
            if (other == vehicle) {
                continue;
            }
            if (wouldOverlap(vehicle, other)) {
                return false;
            }
        }
        return true;
    }

    private boolean wouldOverlap(Vehicle first, Vehicle second) {
        if (first.getDirection() == second.getDirection()) {
            double longitudinal;
            switch (first.getDirection()) {
                case EAST:
                case WEST:
                    longitudinal = Math.abs(first.getPosition().getX() - second.getPosition().getX());
                    break;
                case NORTH:
                case SOUTH:
                    longitudinal = Math.abs(first.getPosition().getY() - second.getPosition().getY());
                    break;
                default:
                    longitudinal = distanceBetween(first, second);
                    break;
            }
            double lateral = lateralDistanceBetween(first, second);
            double minLongitudinal = (vehicleLength(first) + vehicleLength(second)) / 2.0 + 5.0;
            double minLateral = (vehicleWidth(first) + vehicleWidth(second)) / 2.0 + 3.0;
            return longitudinal < minLongitudinal && lateral < minLateral;
        }

        // Trong ngã tư, xe khác hướng dùng vùng an toàn tròn nhỏ để tránh cắt xuyên nhau.
        return distanceBetween(first, second) < 24.0;
    }

    private double vehicleWidth(Vehicle vehicle) {
        if (vehicle instanceof Bicycle) return 8;
        if (vehicle instanceof Motorbike) return 9;
        if (vehicle instanceof Bus || vehicle instanceof FireTruck) return 14;
        return 12;
    }

    private double normalLaneOffsetFor(Vehicle vehicle) {
        // Ba làn không giới hạn loại xe. Chỉ xe đạp bị giới hạn ở làn trong/phải nhất.
        if (vehicle instanceof Bicycle) {
            return INNER_LANE_OFFSET;
        }
        int lane = Math.floorMod(vehicle.getId().hashCode(), 3);
        if (lane == 0) return OUTER_LANE_OFFSET;
        if (lane == 1) return MIDDLE_LANE_OFFSET;
        return INNER_LANE_OFFSET;
    }

    private double headingForDirection(Direction direction) {
        switch (direction) {
            case SOUTH:
                return 90;
            case WEST:
                return 180;
            case NORTH:
                return 270;
            case EAST:
            default:
                return 0;
        }
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private void removeVehiclesOutsideMap() {

        Iterator<Vehicle> iterator = vehicles.iterator();

        while (iterator.hasNext()) {
            Vehicle vehicle = iterator.next();
            double x = vehicle.getPosition().getX();
            double y = vehicle.getPosition().getY();

 
            if (x < -140 || x > 1040 || y < -140 || y > 840) {
                iterator.remove();
                turnChoices.remove(vehicle);
                turningVehicles.remove(vehicle);
                laneChangingVehicles.remove(vehicle);
                assignedLaneOffsets.remove(vehicle);
                emergencyBlockedTicks.remove(vehicle);
                bicycleYieldingToEmergency.remove(vehicle);
                bicycleYieldingToEmergency.values().removeIf(value -> value == vehicle);
                emergencyOvertakingVehicles.remove(vehicle);
                vehiclesThatTurned.remove(vehicle);
                clearLaneChangeIndicators(vehicle);
            }
        }
    }
}