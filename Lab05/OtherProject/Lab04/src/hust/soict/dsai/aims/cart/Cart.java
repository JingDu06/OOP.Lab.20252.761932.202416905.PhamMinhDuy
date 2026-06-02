package hust.soict.dsai.aims.cart;

import java.util.ArrayList;

import hust.soict.dsai.aims.media.Media;

public class Cart {

    private ArrayList<Media> itemsOrdered = new ArrayList<Media>();

    public void addMedia(Media media) {
        if (!itemsOrdered.contains(media)) {
            itemsOrdered.add(media);
            System.out.println("Added");
        } else {
            System.out.println("Already exists");
        }
    }

    public void removeMedia(Media media) {
        if (itemsOrdered.remove(media)) {
            System.out.println("Removed");
        } else {
            System.out.println("Not found");
        }
    }

    public float totalCost() {
        float total = 0;

        for (Media media : itemsOrdered) {
            total += media.getCost();
        }

        return total;
    }

    public void print() {
        System.out.println("***********************CART***********************");
        System.out.println("Ordered Items:");

        for (int i = 0; i < itemsOrdered.size(); i++) {
            System.out.println((i + 1) + ". " + itemsOrdered.get(i));
        }

        System.out.println("Total cost: " + totalCost());
        System.out.println("***************************************************");
    }

    public void searchById(int id) {
        for (Media media : itemsOrdered) {
            if (media.getId() == id) {
                System.out.println(media);
                return;
            }
        }

        System.out.println("Not found");
    }

    public void searchByTitle(String title) {
        for (Media media : itemsOrdered) {
            if (media.isMatch(title)) {
                System.out.println(media);
                return;
            }
        }

        System.out.println("Not found");
    }
}