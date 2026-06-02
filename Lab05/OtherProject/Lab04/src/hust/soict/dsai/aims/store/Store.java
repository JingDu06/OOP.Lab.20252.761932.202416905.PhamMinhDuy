package hust.soict.dsai.aims.store;

import hust.soict.dsai.aims.media.DigitalVideoDisc;

public class Store {
    private static final int MAX_ITEMS_IN_STORE = 100;

    private DigitalVideoDisc[] itemsInStore = new DigitalVideoDisc[MAX_ITEMS_IN_STORE];
    private int qty = 0;

    // Add DVD
    public void addDVD(DigitalVideoDisc dvd) {
        if (qty < MAX_ITEMS_IN_STORE) {
            itemsInStore[qty++] = dvd;
            System.out.println("Added: " + dvd.getTitle());
        } else {
            System.out.println("Store is full");
        }
    }

    // Remove DVD
    public void removeDVD(DigitalVideoDisc dvd) {
        for (int i = 0; i < qty; i++) {
            if (itemsInStore[i] == dvd) {
                for (int j = i; j < qty - 1; j++) {
                    itemsInStore[j] = itemsInStore[j + 1];
                }
                itemsInStore[--qty] = null;
                System.out.println("Removed: " + dvd.getTitle());
                return;
            }
        }
        System.out.println("DVD not found");
    }

    // Print store
    public void print() {
        System.out.println("***************STORE***************");
        for (int i = 0; i < qty; i++) {
            System.out.println((i + 1) + ". " + itemsInStore[i]);
        }
        System.out.println("***********************************");
    }
}