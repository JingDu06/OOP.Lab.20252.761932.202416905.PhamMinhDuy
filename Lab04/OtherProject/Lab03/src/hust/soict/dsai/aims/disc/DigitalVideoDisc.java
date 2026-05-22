package hust.soict.dsai.aims.disc;

public class DigitalVideoDisc {
    private static int nbDigitalVideoDiscs = 0;

    private int id;
    private String title;
    private String category;
    private String director;
    private int length;
    private float cost;

    public int getId() { return id; }
    public String getTitle() { return title; }
    public float getCost() { return cost; }

    public DigitalVideoDisc(String title) {
        this.title = title;
        this.id = ++nbDigitalVideoDiscs;
    }

    public DigitalVideoDisc(String category, String title, float cost) {
        this(title);
        this.category = category;
        this.cost = cost;
    }

    public DigitalVideoDisc(String director, String category, String title, float cost) {
        this(category, title, cost);
        this.director = director;
    }

    public DigitalVideoDisc(String title, String category, String director, int length, float cost) {
        this(director, category, title, cost);
        this.length = length;
    }

    public boolean isMatch(String title) {
        return this.title.toLowerCase().contains(title.toLowerCase());
    }

    @Override
    public String toString() {
        return "DVD - " + title + " - " + category + " - " + director + " - " + length + ": " + cost + " $";
    }
}