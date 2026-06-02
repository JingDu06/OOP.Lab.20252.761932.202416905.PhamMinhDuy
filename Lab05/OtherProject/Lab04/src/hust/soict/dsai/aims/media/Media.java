package hust.soict.dsai.aims.media;

public abstract class Media {
    private int id;
    private String title;
    private String category;
    private float cost;

    public Media() {}

    public Media(int id, String title, String category, float cost) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.cost = cost;
    }

    public String getTitle() {
        return title;
    }

    public float getCost() {
        return cost;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Media)) return false;

        Media other = (Media) obj;
        return this.title.equals(other.title);
    }

    @Override
    public String toString() {
        return title + " - " + category + " - " + cost;
    }
}