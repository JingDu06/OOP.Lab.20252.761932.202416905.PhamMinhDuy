package hust.soict.dsai.aims.media;

import java.util.ArrayList;

public class Book extends Media {

    private ArrayList<String> authors = new ArrayList<>();

    public Book(int id, String title,
                String category, float cost) {

        super(id, title, category, cost);
    }

    public void addAuthor(String author) {
        if (!authors.contains(author)) {
            authors.add(author);
        }
    }

    public void removeAuthor(String author) {
        authors.remove(author);
    }
}