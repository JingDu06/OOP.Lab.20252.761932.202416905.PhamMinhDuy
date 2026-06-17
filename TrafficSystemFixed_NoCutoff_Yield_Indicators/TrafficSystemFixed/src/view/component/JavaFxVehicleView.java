package view.component;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import view.presentation.VehicleRenderState;
import view.presentation.VehicleSpriteType;
import view.renderer.DisplayMode;
import view.renderer.VehicleSpriteStore;

/**
 * Pure drawing component. It never changes model coordinates or movement state.
 */
public final class JavaFxVehicleView {

    private final VehicleRenderState state;
    private final VehicleSpriteStore spriteStore;
    private final DisplayMode displayMode;
    private final double scale;
    private final int tick;

    public JavaFxVehicleView(VehicleRenderState state,
                             VehicleSpriteStore spriteStore,
                             DisplayMode displayMode,
                             double scale,
                             int tick) {
        this.state = state;
        this.spriteStore = spriteStore;
        this.displayMode = displayMode == null ? DisplayMode.SPRITE : displayMode;
        this.scale = Math.max(0.1, scale);
        this.tick = tick;
    }

    public void render(GraphicsContext graphics) {
        VehicleSpriteType type = state.getSpriteType();

        // Sprite mode is intentionally a little larger and clearer than the old
        // rectangle mode. This changes drawing only, never simulation coordinates.
        double visualScale = displayMode == DisplayMode.SPRITE
                ? Math.max(0.82, scale * 1.08)
                : scale;
        double width = type.getWorldWidth() * visualScale;
        double height = type.getWorldHeight() * visualScale;

        graphics.save();
        graphics.translate(state.getX(), state.getY());
        graphics.rotate(state.getHeadingDegrees());
        graphics.setImageSmoothing(true);

        if (displayMode == DisplayMode.SPRITE) {
            drawSpriteMode(graphics, type, width, height);
        } else {
            drawBasicMode(graphics, type, width, height);
        }

        if (type == VehicleSpriteType.AMBULANCE && state.isSirenActive()) {
            drawAmbulanceFlashingLights(graphics, width, height);
        }
        drawTurnIndicators(graphics, width, height);

        graphics.restore();
    }

    private void drawSpriteMode(GraphicsContext graphics,
                                VehicleSpriteType type,
                                double width,
                                double height) {
        // Small ground shadow makes the PNG visually distinct from Basic mode.
        graphics.setGlobalAlpha(0.30);
        graphics.setFill(Color.BLACK);
        graphics.fillOval(-width * 0.43, -height * 0.31,
                width * 0.90, height * 0.76);
        graphics.setGlobalAlpha(1.0);

        Image sprite = spriteStore == null ? null : spriteStore.get(type);
        if (sprite != null && !sprite.isError()
                && sprite.getWidth() > 0 && sprite.getHeight() > 0) {
            graphics.drawImage(sprite, -width / 2.0, -height / 2.0, width, height);
            return;
        }

        // A very obvious fallback: if this appears, the PNG path is wrong.
        drawMissingSprite(graphics, width, height);
    }

    private void drawBasicMode(GraphicsContext graphics,
                               VehicleSpriteType type,
                               double width,
                               double height) {
        graphics.setFill(fallbackColor(type));
        graphics.fillRoundRect(-width / 2.0, -height / 2.0,
                width, height, height * 0.45, height * 0.45);
        graphics.setFill(Color.web("#f8f8f8"));
        graphics.fillRect(width / 2.0 - Math.max(2.0, width * 0.12),
                -height * 0.28,
                Math.max(1.5, width * 0.07),
                height * 0.56);
    }

    private void drawMissingSprite(GraphicsContext graphics,
                                   double width,
                                   double height) {
        graphics.setFill(Color.MAGENTA);
        graphics.fillRect(-width / 2.0, -height / 2.0, width, height);
        graphics.setStroke(Color.BLACK);
        graphics.setLineWidth(2.0);
        graphics.strokeLine(-width / 2.0, -height / 2.0,
                width / 2.0, height / 2.0);
        graphics.strokeLine(-width / 2.0, height / 2.0,
                width / 2.0, -height / 2.0);
    }

    private void drawAmbulanceFlashingLights(GraphicsContext graphics,
                                              double width,
                                              double height) {
        boolean redPhase = (tick / 2) % 2 == 0;
        Color left = redPhase ? Color.RED : Color.DODGERBLUE;
        Color right = redPhase ? Color.DODGERBLUE : Color.RED;

        double lightX = width * 0.02;
        double lightY = Math.max(2.0, height * 0.29);
        double radius = Math.max(2.0, height * 0.15);

        graphics.save();
        graphics.setGlobalBlendMode(BlendMode.SCREEN);
        graphics.setGlobalAlpha(0.32);
        graphics.setFill(left);
        graphics.fillOval(lightX - radius * 2.5, -lightY - radius * 2.5,
                radius * 5.0, radius * 5.0);
        graphics.setFill(right);
        graphics.fillOval(lightX - radius * 2.5, lightY - radius * 2.5,
                radius * 5.0, radius * 5.0);

        graphics.setGlobalAlpha(1.0);
        graphics.setFill(left);
        graphics.fillOval(lightX - radius, -lightY - radius,
                radius * 2, radius * 2);
        graphics.setFill(right);
        graphics.fillOval(lightX - radius, lightY - radius,
                radius * 2, radius * 2);
        graphics.restore();
    }


    private void drawTurnIndicators(GraphicsContext graphics,
                                    double width,
                                    double height) {
        boolean blinkOn = (tick / 4) % 2 == 0;
        if (!blinkOn) {
            return;
        }
        if (!state.isLeftIndicatorActive() && !state.isRightIndicatorActive()) {
            return;
        }

        double rx = width * 0.30;
        double ry = height * 0.34;
        double radius = Math.max(1.8, height * 0.13);

        graphics.save();
        graphics.setGlobalAlpha(0.95);
        graphics.setFill(Color.web("#ffb000"));

        if (state.isLeftIndicatorActive()) {
            graphics.fillOval(rx - radius, -ry - radius, radius * 2, radius * 2);
            graphics.fillOval(-rx - radius, -ry - radius, radius * 2, radius * 2);
        }
        if (state.isRightIndicatorActive()) {
            graphics.fillOval(rx - radius, ry - radius, radius * 2, radius * 2);
            graphics.fillOval(-rx - radius, ry - radius, radius * 2, radius * 2);
        }
        graphics.restore();
    }

    private Color fallbackColor(VehicleSpriteType type) {
        switch (type) {
            case BUS:
                return Color.LIMEGREEN;
            case AMBULANCE:
                return Color.WHITE;
            case FIRE_TRUCK:
                return Color.CRIMSON;
            case MOTORBIKE:
                return Color.DODGERBLUE;
            case BICYCLE:
                return Color.LIGHTPINK;
            case CAR:
            default:
                return Color.ORANGE;
        }
    }
}
