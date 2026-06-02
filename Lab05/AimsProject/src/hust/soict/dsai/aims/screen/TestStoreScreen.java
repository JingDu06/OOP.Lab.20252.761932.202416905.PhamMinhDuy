package hust.soict.dsai.aims.screen;

import hust.soict.dsai.aims.store.Store;
import hust.soict.dsai.aims.media.Book;
import hust.soict.dsai.aims.media.CompactDisc;
import hust.soict.dsai.aims.media.DigitalVideoDisc;

public class TestStoreScreen {
    public static void main(String[] args) {

        Store store = new Store();
        
        // DVD: DigitalVideoDisc(String title, String category, String director, int length, float cost)
        DigitalVideoDisc dvd1 = new DigitalVideoDisc("The Lion King", "Animation", "Roger Allers", 87, 19.95f);
        DigitalVideoDisc dvd2 = new DigitalVideoDisc("Star Wars", "Science Fiction", "George Lucas", 124, 24.95f);
        DigitalVideoDisc dvd3 = new DigitalVideoDisc("Aladdin", "Animation", 18.99f); // Dùng constructor 3 tham số
        DigitalVideoDisc dvd4 = new DigitalVideoDisc("Avatar", "Science Fiction", "James Cameron", 162, 22.50f);

        // Book: Book(int id, String title, String category, float cost)
        Book book1 = new Book(4, "The Valley of Fear", "Detective", 20.00f);
        Book book2 = new Book(6,"Harry Potter", "Fantasy", 25.50f);

        // CD: CompactDisc(String title, String category, float cost)
        CompactDisc cd = new CompactDisc("Adele 21", "Music", 15.50f);
        store.addMedia(dvd1);
        store.addMedia(dvd2);
        store.addMedia(dvd3);
        store.addMedia(book1);
        store.addMedia(cd);
        store.addMedia(dvd4);
        store.addMedia(book2);

        new StoreScreen(store);
    }
}
