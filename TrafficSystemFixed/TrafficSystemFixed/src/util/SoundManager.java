package util;

public class SoundManager {

    private boolean enabled;

    public SoundManager() {

        enabled = true;
    }

    public void playHorn() {

        play("horn");
    }

    public void playSiren() {

        play("siren");
    }

    public void playTrafficLightChange() {

        play("traffic-light-change");
    }

    public void play(String soundName) {

        if (enabled && soundName != null && !soundName.trim().isEmpty()) {
            System.out.println("[Sound] " + soundName);
        }
    }

    public void mute() {

        enabled = false;
    }

    public void unmute() {

        enabled = true;
    }

    public boolean isEnabled() {

        return enabled;
    }
}