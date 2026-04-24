public class DigitalVideoDisc {
    private String title;
    private String category;
    private String director;
    private int length;
    private float cost;

    // --- ACCESSORS (Getters) ---
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getDirector() { return director; }
    public int getLength() { return length; }
    public float getCost() { return cost; }

    // --- CONSTRUCTORS ---

    // 1. Create a DVD object by title
    public DigitalVideoDisc(String title) {
        this.title = title;
    }

    // 2. Create a DVD object by category, title and cost
    public DigitalVideoDisc(String category, String title, float cost) {
        this(title);
        this.category = category;
        this.cost = cost;
    }

    // 3. Create a DVD object by director, category, title and cost
    public DigitalVideoDisc(String director, String category, String title, float cost) {
        this(category, title, cost);
        this.director = director;
    }

    // 4. Create a DVD object by all attributes: title, category, director, length and cost
    public DigitalVideoDisc(String title, String category, String director, int length, float cost) {
        
        this(director, category, title, cost); 
        this.length = length;
    }
}