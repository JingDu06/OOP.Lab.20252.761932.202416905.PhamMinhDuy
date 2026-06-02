package hust.soict.dsai.aims.media;

public class Disc extends Media implements Comparable<Media> {
    private int length;
    private String director;
    public Disc(String title, String category, String director, float cost) {
        super(title, category, cost);
        this.director = director;
    }
    public Disc(String title, String category, float cost) {
        super(title, category, cost);
    }
    public Disc(String title) {
        super(title);
    }
    public Disc(int id, String title, String category, float cost, int length, String director) {
        super(id, title, category, cost);
        this.length = length;
        this.director = director;
    }
    public Disc(String title, String category, String director, int length, float cost) {
        super(title, category, cost);
        this.director = director;
        this.length = length;
    }
    public int getLength() {
        return length;
    }
    public void setLength(int length) {
        this.length = length;
    }
    public String getDirector() {
        return director;
    }
    public void setDirector(String director) {
        this.director = director;
    }
    @Override
    public int compareTo(Media other) {
        return this.getTitle().compareTo(other.getTitle());
    }
}