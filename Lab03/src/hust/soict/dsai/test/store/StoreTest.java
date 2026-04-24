package hust.soict.dsai.test.store;

import hust.soict.dsai.aims.store.Store;
import hust.soict.dsai.aims.media.DigitalVideoDisc;

public class StoreTest {
    public static void main(String[] args) {

        Store store = new Store();

        // Tạo DVD
        DigitalVideoDisc dvd1 = new DigitalVideoDisc("The Lion King",
                "Animation", "Roger Allers", 87, 19.95f);

        DigitalVideoDisc dvd2 = new DigitalVideoDisc("Star Wars",
                "Science Fiction", "George Lucas", 87, 24.95f);

        DigitalVideoDisc dvd3 = new DigitalVideoDisc(
                "Aladin", "Animation", 18.99f);

        // ===== TEST ADD =====
        System.out.println("----- Add DVDs -----");
        store.addDVD(dvd1);
        store.addDVD(dvd2);
        store.addDVD(dvd3);

        // ===== TEST PRINT =====
        System.out.println("\n----- Store content -----");
        store.print();

        // ===== TEST REMOVE =====
        System.out.println("\n----- Remove dvd2 -----");
        store.removeDVD(dvd2);

        // ===== TEST PRINT AGAIN =====
        System.out.println("\n----- After removal -----");
        store.print();
    }
}