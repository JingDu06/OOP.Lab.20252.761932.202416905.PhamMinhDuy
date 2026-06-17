package view.renderer;

public enum DisplayMode {
    BASIC("Basic"),
    SPRITE("Sprite xe");

    private final String label;

    DisplayMode(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}