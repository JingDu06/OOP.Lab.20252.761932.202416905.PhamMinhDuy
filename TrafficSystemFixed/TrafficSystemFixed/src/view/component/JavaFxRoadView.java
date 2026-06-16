package view.component;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.map.TrafficMapType;

public class JavaFxRoadView {

    private final double width;

    private final double height;
    private TrafficMapType mapType;

    public JavaFxRoadView(double width, double height) {
        this.width = width;
        this.height = height;
        this.mapType = TrafficMapType.CROSS_JUNCTION;
    }

    public void setMapType(TrafficMapType mapType) {
        this.mapType = mapType == null ? TrafficMapType.CROSS_JUNCTION : mapType;
    }

    public void render(GraphicsContext graphics) {
        graphics.setFill(Color.web("#28323a"));
        graphics.fillRect(0, 0, width, height);

        if (mapType == TrafficMapType.ROAD_NETWORK) {
            renderNetwork(graphics);
            return;
        }

        renderHorizontalRoad(graphics, 280, 140);
        renderVerticalRoad(graphics, 380, 140);

        if (mapType == TrafficMapType.T_JUNCTION) {
            graphics.setFill(Color.web("#28323a"));
            graphics.fillRect(380, 0, 140, 275);
        }

        if (mapType == TrafficMapType.FIVE_WAY_JUNCTION) {
            graphics.setStroke(Color.DARKGRAY);
            graphics.setLineWidth(112);
            graphics.strokeLine(450, 350, 760, 80);
            graphics.setStroke(Color.WHITE);
            graphics.setLineWidth(2);
            graphics.setLineDashes(18);
            graphics.strokeLine(450, 350, 760, 80);
            graphics.setLineDashes(null);
        }

        graphics.setFill(Color.WHITE);
        graphics.fillText(mapType.getLabel() + " - xe được phóng to tại ngã rẽ", 20, 28);
    }

    private void renderNetwork(GraphicsContext graphics) {
        renderHorizontalRoad(graphics, 120, 70);
        renderHorizontalRoad(graphics, 320, 70);
        renderHorizontalRoad(graphics, 520, 70);
        renderVerticalRoad(graphics, 170, 70);
        renderVerticalRoad(graphics, 420, 70);
        renderVerticalRoad(graphics, 670, 70);

        graphics.setFill(Color.WHITE);
        graphics.fillText("Mạng lưới đường rộng - xe thu nhỏ để quan sát toàn vùng", 20, 28);
    }

    private void renderHorizontalRoad(GraphicsContext graphics, double y, double roadHeight) {
        graphics.setFill(Color.DARKGRAY);
        graphics.fillRect(0, y, width, roadHeight);
        graphics.setStroke(Color.WHITE);
        graphics.setLineWidth(2);
        graphics.setLineDashes(18);
        graphics.strokeLine(0, y + roadHeight / 2, width, y + roadHeight / 2);
        graphics.setLineDashes(null);
    }

    private void renderVerticalRoad(GraphicsContext graphics, double x, double roadWidth) {
        graphics.setFill(Color.DARKGRAY);
        graphics.fillRect(x, 0, roadWidth, height);
        graphics.setStroke(Color.WHITE);
        graphics.setLineWidth(2);
        graphics.setLineDashes(18);
        graphics.strokeLine(x + roadWidth / 2, 0, x + roadWidth / 2, height);
        graphics.setLineDashes(null);
    }
}