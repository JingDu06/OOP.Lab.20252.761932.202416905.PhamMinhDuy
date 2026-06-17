package view.renderer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;

import javafx.scene.image.Image;
import view.presentation.VehicleSpriteType;

/**
 * Loads every vehicle sprite once and reuses it for all frames.
 *
 * Loading is synchronous so Canvas never keeps drawing the vector fallback while
 * a background image is still being decoded. Classpath loading is preferred;
 * filesystem fallbacks make the sprites work when the project is started
 * directly from an IDE as well as with Maven.
 */
public final class VehicleSpriteStore {

    private final Map<VehicleSpriteType, Image> sprites =
            new EnumMap<>(VehicleSpriteType.class);

    public VehicleSpriteStore() {
        for (VehicleSpriteType type : VehicleSpriteType.values()) {
            sprites.put(type, load(type));
        }
    }

    public Image get(VehicleSpriteType type) {
        return sprites.get(type);
    }

    public boolean isLoaded(VehicleSpriteType type) {
        Image image = sprites.get(type);
        return image != null && !image.isError() && image.getWidth() > 0 && image.getHeight() > 0;
    }

    private Image load(VehicleSpriteType type) {
        String resourcePath = type.getResourcePath();

        // 1. Normal Maven/packaged application classpath.
        try (InputStream stream = VehicleSpriteStore.class.getResourceAsStream(resourcePath)) {
            if (stream != null) {
                Image image = new Image(stream);
                if (!image.isError() && image.getWidth() > 0) {
                    return image;
                }
            }
        } catch (IOException ignored) {
            // Continue to filesystem fallbacks below.
        }

        // 2. Running from the project root in an IDE/terminal.
        String relative = resourcePath.startsWith("/")
                ? resourcePath.substring(1)
                : resourcePath;
        Path[] candidates = {
                Path.of("src", "main", "resources", relative),
                Path.of("TrafficSystemFixed", "src", "main", "resources", relative)
        };

        for (Path candidate : candidates) {
            if (!Files.isRegularFile(candidate)) {
                continue;
            }
            try (InputStream stream = new FileInputStream(candidate.toFile())) {
                Image image = new Image(stream);
                if (!image.isError() && image.getWidth() > 0) {
                    return image;
                }
            } catch (IOException ignored) {
                // Try the next location.
            }
        }

        System.err.println("[VehicleSpriteStore] Không tìm thấy sprite: " + resourcePath);
        return null;
    }
}
