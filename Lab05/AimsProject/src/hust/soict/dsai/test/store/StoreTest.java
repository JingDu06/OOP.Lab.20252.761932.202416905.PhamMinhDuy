package hust.soict.dsai.test.store;

import hust.soict.dsai.aims.media.DigitalVideoDisc;
import hust.soict.dsai.aims.store.Store;

public class StoreTest {
    public static void main(String[] args) {
        DigitalVideoDisc Media1 = new DigitalVideoDisc("5cm/s",
            "Anime", "Makoto Shinkai", 19.95f);

        DigitalVideoDisc Media2 = new DigitalVideoDisc("TenSura",
                "Science Fiction", "George Lucas", 24.95f);

        DigitalVideoDisc Media3 = new DigitalVideoDisc("Asagao to Kase-san",
                "Yuri", "Murasaki",  18.99f);
        
        DigitalVideoDisc Media4 = new DigitalVideoDisc("MarriageToxin",
                "Romance-Comedy", "Murasaki",  18.99f);

        Store store = new Store();

        store.addMedia(Media1);
        store.addMedia(Media2);
        store.addMedia(Media3);
        store.addMedia(Media4);

        store.removeMediabyTitle("TenSura");

        store.printStore();
    }
}