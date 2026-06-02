package hust.soict.dsai.aims.cart;

import java.util.Collections; 
import hust.soict.dsai.aims.media.Media;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Cart {
    private ObservableList<Media> itemsOrdered = FXCollections.observableArrayList();
    public ObservableList<Media> getItemsOrdered() {
        return itemsOrdered;
    }

    public void setItemsOrdered(ObservableList<Media> itemsOrdered) {
        this.itemsOrdered = itemsOrdered;
    }

    public void addMedia(Media d) {
        itemsOrdered.add(d); //
        System.out.println("The media has been added: " + d.getTitle());
    }

    public void addMedia(Media ... dArray) {
        for (Media m : dArray) {
            itemsOrdered.add(m);
        }
        System.out.println("Added multiple items to the cart.");
    }

    // Nạp chồng để thêm 2 Media cùng lúc
    public void addMedia(Media d1, Media d2) {
        itemsOrdered.add(d1);
        itemsOrdered.add(d2);
        System.out.println("Added 2 items: " + d1.getTitle() + ", " + d2.getTitle());
    }

    // Xóa Media khỏi giỏ hàng
    public void removeMedia(Media d) {
        if (itemsOrdered.remove(d)) { // Sử dụng hàm remove có sẵn của ArrayList
            System.out.println("The media has been removed: " + d.getTitle());
        } else {
            System.out.println("Media not found in cart.");
        }
    }

    public double totalCost() {
        double tc = 0;
        for (Media m : itemsOrdered) { // Duyệt qua ArrayList
            tc += m.getCost();
        }
        return tc;
    }

    public void printAll() {
        System.out.println("*******************************CART******************************");
        System.out.println("Ordered items: ");
        for (Media d : itemsOrdered) {
            System.out.printf("ID: %d. %s - %s - %.2f\n", d.getId(), d.getTitle(), d.getCategory(), d.getCost());
        }
        System.out.printf("Total cost: %.2f\n", totalCost());
        System.out.println("*****************************************************************");
    }

    public void findMedia(int id) {
        boolean found = false;
        for (Media m : itemsOrdered) {
            if (m.getId() == id) {
                System.out.printf("Found: ID %d. %s - %s\n", m.getId(), m.getTitle(), m.getCategory());
                found = true;
            }
        }
        if (!found) System.out.println("Not found");
    }
    public void findMedia(String title) {
        boolean found = false;
        for (Media m : itemsOrdered) {
            if (m.getTitle().equalsIgnoreCase(title)) {
                System.out.printf("Found: ID %d. %s - %s\n", m.getId(), m.getTitle(), m.getCategory());
                found = true;
            }
        }
        if (!found) System.out.println("Not found");
    }


    public Media searchByTitle(String title) {
        for (Media m : itemsOrdered) {
            if (m.getTitle().equalsIgnoreCase(title)) {
                return m;
            }
        }
        return null;
    }

    public void sortMediaByTitle() {
        Collections.sort(itemsOrdered, Media.COMPARE_BY_TITLE_COST);
        System.out.println("Cart sorted by Title -> Cost.");
    }

    public void sortMediaByCost() {
        Collections.sort(itemsOrdered, Media.COMPARE_BY_COST_TITLE);
        System.out.println("Cart sorted by Cost -> Title.");
    }

    public void empty() {
        itemsOrdered.clear();
        System.out.println("The cart is now empty.");
    }
}