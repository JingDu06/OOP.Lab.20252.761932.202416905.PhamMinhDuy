package hust.soict.dsai.aims.media;

import java.util.ArrayList;

import java.util.List;

public class Book extends Media{
    private List<String> authors = new ArrayList<>();

    public Book(int id, String title, String category, float cost) {
        super(id, title, category, cost);
    }

    public void addAuthor(String authName){
        if(authors.contains(authName)){
            System.out.println("Tac gia da ton tai");
        }
        else{
            authors.add(authName);
            System.out.println("Da them tac gia: " + authName);
        }
    }

    public void removeAuthor(String authName){
        if(!authors.contains(authName)){
            System.out.println("Khong tim thay tac gia " + authName);
        }
        else{
            authors.remove(authName);
            System.out.println("Da xoa tac gia: " + authName);
        }
    }
    @Override
    public int compareTo(Media other) {
        // Ví dụ: So sánh theo tiêu đề (Title)
        return this.getTitle().compareTo(other.getTitle());
    }

}