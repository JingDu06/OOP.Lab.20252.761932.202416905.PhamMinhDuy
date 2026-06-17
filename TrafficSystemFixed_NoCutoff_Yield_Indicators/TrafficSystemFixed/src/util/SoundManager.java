package util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;

import model.vehicle.Ambulance;
import model.vehicle.FireTruck;
import model.vehicle.Vehicle;

/**
 * Sound effects for the simulation.
 *
 * This class deliberately uses the JDK javax.sound.sampled backend instead of
 * JavaFX Media. PCM WAV playback is therefore independent of the JavaFX media
 * native libraries and works when the project is launched from Maven or an IDE.
 *
 * It never changes vehicle coordinates, speed, lane or traffic state.
 */
public final class SoundManager {

    private enum SoundId {
        HORN("car-horn.wav", 0.95, 430),
        AMBULANCE("ambulance-siren.wav", 0.92, 120),
        FIRE_TRUCK("firetruck-siren.wav", 0.95, 120),
        TURN_SIGNAL("turn-signal.wav", 0.75, 330),
        TRAFFIC_LIGHT("traffic-light-change.wav", 0.72, 380);

        private final String fileName;
        private final double volume;
        private final long cooldownMillis;

        SoundId(String fileName, double volume, long cooldownMillis) {
            this.fileName = fileName;
            this.volume = volume;
            this.cooldownMillis = cooldownMillis;
        }
    }

    private static final Map<SoundId, Clip> CLIPS = new EnumMap<>(SoundId.class);
    private static final Map<SoundId, Long> LAST_PLAYED = new EnumMap<>(SoundId.class);
    private static volatile boolean globalEnabled = true;
    private static volatile String lastError = "";

    private boolean enabled = true;

    public SoundManager() {
        // Lazy loading keeps logic-only tests independent from an audio device.
    }

    /** Preload all WAV files and verify that an audio output line can be opened. */
    public static synchronized boolean initialize() {
        boolean ok = true;
        for (SoundId id : SoundId.values()) {
            if (getClip(id) == null) {
                ok = false;
            }
        }
        return ok;
    }

    public void playHorn() {
        play(SoundId.HORN);
    }

    /** Compatibility method for older callers. */
    public void playSiren() {
        play(SoundId.AMBULANCE);
    }

    public void playAmbulanceSiren() {
        play(SoundId.AMBULANCE);
    }

    public void playFireTruckSiren() {
        play(SoundId.FIRE_TRUCK);
    }

    public void playEmergencySiren(Vehicle vehicle) {
        if (vehicle instanceof Ambulance && ((Ambulance) vehicle).isSirenOn()) {
            setLooping(SoundId.AMBULANCE, true);
        } else if (vehicle instanceof FireTruck && ((FireTruck) vehicle).isSirenOn()) {
            setLooping(SoundId.FIRE_TRUCK, true);
        }
    }

    /**
     * Synchronizes the two emergency sirens with the current simulation state.
     * A recording starts once, loops while at least one matching emergency
     * vehicle has its siren enabled, and stops immediately when none remain.
     */
    public void updateEmergencySirens(boolean ambulanceActive, boolean fireTruckActive) {
        if (!enabled || !globalEnabled) {
            stopSound(SoundId.AMBULANCE);
            stopSound(SoundId.FIRE_TRUCK);
            return;
        }
        setLooping(SoundId.AMBULANCE, ambulanceActive);
        setLooping(SoundId.FIRE_TRUCK, fireTruckActive);
    }

    public void playTrafficLightChange() {
        play(SoundId.TRAFFIC_LIGHT);
    }

    public void playTurnSignal() {
        play(SoundId.TURN_SIGNAL);
    }

    /**
     * Emergency sirens remain audible even while an emergency vehicle is
     * temporarily stopped by a safe-distance rule or a red light.
     */
    public void playVehicleSound(Vehicle vehicle) {
        if (vehicle == null) {
            return;
        }
        playEmergencySiren(vehicle);
    }

    public void play(String soundName) {
        if (soundName == null) {
            return;
        }
        switch (soundName.trim().toLowerCase()) {
            case "horn":
                playHorn();
                break;
            case "siren":
            case "ambulance-siren":
                playAmbulanceSiren();
                break;
            case "firetruck-siren":
            case "fire-truck-siren":
                playFireTruckSiren();
                break;
            case "turn-signal":
                playTurnSignal();
                break;
            case "traffic-light-change":
                playTrafficLightChange();
                break;
            default:
                break;
        }
    }

    /** Plays horn, ambulance siren and fire-truck siren for manual verification. */
    public static boolean testAllSounds() {
        if (!globalEnabled) {
            globalEnabled = true;
        }
        if (!initialize()) {
            return false;
        }

        Thread testThread = new Thread(() -> {
            stopAll();
            forcePlay(SoundId.HORN);
            sleepQuietly(650);
            stopSound(SoundId.HORN);

            forcePlay(SoundId.AMBULANCE);
            sleepQuietly(2400);
            stopSound(SoundId.AMBULANCE);

            forcePlay(SoundId.FIRE_TRUCK);
            sleepQuietly(2400);
            stopSound(SoundId.FIRE_TRUCK);
        }, "traffic-audio-test");
        testThread.setDaemon(true);
        testThread.start();
        return true;
    }

    private void play(SoundId soundId) {
        if (!enabled || !globalEnabled || soundId == null) {
            return;
        }

        long now = System.currentTimeMillis();
        long last = LAST_PLAYED.getOrDefault(soundId, 0L);
        if (now - last < soundId.cooldownMillis) {
            return;
        }

        Clip clip = getClip(soundId);
        if (clip == null) {
            return;
        }

        synchronized (clip) {
            try {
                // A siren is restarted immediately after its WAV finishes, but
                // it is never stacked on top of itself every simulation frame.
                if (clip.isRunning()) {
                    return;
                }
                clip.stop();
                clip.setFramePosition(0);
                clip.start();
                LAST_PLAYED.put(soundId, now);
            } catch (RuntimeException exception) {
                lastError = "Không thể phát " + soundId.fileName + ": " + exception.getMessage();
            }
        }
    }

    private static void forcePlay(SoundId soundId) {
        Clip clip = getClip(soundId);
        if (clip == null) {
            return;
        }
        synchronized (clip) {
            try {
                clip.stop();
                clip.setFramePosition(0);
                clip.start();
                LAST_PLAYED.put(soundId, System.currentTimeMillis());
            } catch (RuntimeException exception) {
                lastError = "Không thể phát " + soundId.fileName + ": " + exception.getMessage();
            }
        }
    }

    private static void setLooping(SoundId soundId, boolean active) {
        if (!globalEnabled || soundId == null) {
            if (soundId != null) {
                stopSound(soundId);
            }
            return;
        }

        Clip clip = getClip(soundId);
        if (clip == null) {
            return;
        }

        synchronized (clip) {
            try {
                if (!active) {
                    if (clip.isRunning()) {
                        clip.stop();
                    }
                    clip.setFramePosition(0);
                    return;
                }

                if (!clip.isRunning()) {
                    clip.stop();
                    clip.setFramePosition(0);
                    clip.loop(Clip.LOOP_CONTINUOUSLY);
                    LAST_PLAYED.put(soundId, System.currentTimeMillis());
                }
            } catch (RuntimeException exception) {
                lastError = "Không thể phát lặp " + soundId.fileName + ": " + exception.getMessage();
            }
        }
    }

    private static void stopSound(SoundId soundId) {
        Clip clip = CLIPS.get(soundId);
        if (clip == null) {
            return;
        }
        synchronized (clip) {
            try {
                clip.stop();
                clip.setFramePosition(0);
            } catch (RuntimeException ignored) {
                // Audio errors must never interrupt the traffic simulation.
            }
        }
    }

    private static synchronized Clip getClip(SoundId soundId) {
        Clip cached = CLIPS.get(soundId);
        if (cached != null && cached.isOpen()) {
            return cached;
        }

        try (AudioInputStream stream = openAudioStream(soundId.fileName)) {
            if (stream == null) {
                lastError = "Không tìm thấy /assets/sounds/" + soundId.fileName;
                return null;
            }

            Clip clip = AudioSystem.getClip();
            clip.open(stream);
            applyVolume(clip, soundId.volume);
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    // Keep the clip open for fast replay; only reset its cursor.
                    Clip source = (Clip) event.getLine();
                    if (source.isOpen() && source.getFramePosition() >= source.getFrameLength()) {
                        source.setFramePosition(0);
                    }
                }
            });
            CLIPS.put(soundId, clip);
            lastError = "";
            return clip;
        } catch (Exception exception) {
            lastError = "Lỗi mở âm thanh " + soundId.fileName + ": "
                    + exception.getClass().getSimpleName() + " - " + exception.getMessage();
            return null;
        }
    }

    private static AudioInputStream openAudioStream(String fileName) throws Exception {
        String classpath = "/assets/sounds/" + fileName;
        URL resource = SoundManager.class.getResource(classpath);
        if (resource != null) {
            InputStream raw = resource.openStream();
            return AudioSystem.getAudioInputStream(new BufferedInputStream(raw));
        }

        // IDE/source-tree fallback when resources have not yet been copied.
        File sourceFile = new File("src/main/resources/assets/sounds/" + fileName);
        if (sourceFile.isFile()) {
            return AudioSystem.getAudioInputStream(sourceFile);
        }

        // Fallback for launching from the parent directory.
        File nestedFile = new File("TrafficSystemFixed/src/main/resources/assets/sounds/" + fileName);
        if (nestedFile.isFile()) {
            return AudioSystem.getAudioInputStream(nestedFile);
        }
        return null;
    }

    private static void applyVolume(Clip clip, double volume) {
        if (!clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            return;
        }
        FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        double safeVolume = Math.max(0.0001, Math.min(1.0, volume));
        float decibels = (float) (20.0 * Math.log10(safeVolume));
        decibels = Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), decibels));
        gain.setValue(decibels);
    }

    private static void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    public void mute() {
        enabled = false;
    }

    public void unmute() {
        enabled = true;
    }

    public boolean isEnabled() {
        return enabled && globalEnabled;
    }

    public static void setGlobalEnabled(boolean enabled) {
        globalEnabled = enabled;
        if (!enabled) {
            stopAll();
        }
    }

    public static boolean isGlobalEnabled() {
        return globalEnabled;
    }

    public static String getLastError() {
        return lastError == null ? "" : lastError;
    }

    public static synchronized void stopAll() {
        for (Clip clip : CLIPS.values()) {
            if (clip != null) {
                try {
                    clip.stop();
                    clip.setFramePosition(0);
                } catch (RuntimeException ignored) {
                    // Audio must never stop the traffic simulation.
                }
            }
        }
    }

    public static synchronized void shutdown() {
        for (Clip clip : CLIPS.values()) {
            if (clip != null) {
                try {
                    clip.stop();
                    clip.close();
                } catch (RuntimeException ignored) {
                    // Ignore audio-backend shutdown errors.
                }
            }
        }
        CLIPS.clear();
    }
}
