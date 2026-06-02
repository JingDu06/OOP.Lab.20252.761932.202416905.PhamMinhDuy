package hust.soict.dsai.aims.aims;

import hust.soict.dsai.aims.cart.Cart;
import hust.soict.dsai.aims.media.*;
import hust.soict.dsai.aims.store.Store;
import hust.soict.dsai.aims.exception.PlayerException;
import javax.swing.JOptionPane; 
import java.util.Scanner;

public class Aims {
    private static Store store = new Store();
    private static Cart cart = new Cart();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        initData();
        boolean exit = false;
        while (!exit) {
            showMenu();
            int choice = getUserChoice(0, 3);
            switch (choice) {
                case 1:
                    viewStoreMenu();
                    break;
                case 2:
                    updateStoreMenu();
                    break;
                case 3:
                    seeCurrentCartMenu();
                    break;
                case 0:
                    System.out.println("Goodbye!");
                    exit = true;
                    break;
            }
        }
        scanner.close();
    }

    // --- MENUS ---
    public static void showMenu() {
        System.out.println("\nAIMS: ");
        System.out.println("--------------------------------");
        System.out.println("1. View store");
        System.out.println("2. Update store");
        System.out.println("3. See current cart");
        System.out.println("0. Exit");
        System.out.println("--------------------------------");
        System.out.print("Please choose a number: 0-1-2-3: ");
    }

    public static void storeMenu() {
        System.out.println("\nOptions: ");
        System.out.println("--------------------------------");
        System.out.println("1. See a media's details");
        System.out.println("2. Add a media to cart");
        System.out.println("3. Play a media");
        System.out.println("4. See current cart");
        System.out.println("0. Back");
        System.out.println("--------------------------------");
        System.out.print("Please choose a number: 0-1-2-3-4: ");
    }

    public static void mediaDetailsMenu(Media media) {
        System.out.println("\nOptions: ");
        System.out.println("--------------------------------");
        System.out.println("1. Add to cart");
        if (media instanceof Playable) {
            System.out.println("2. Play");
        }
        System.out.println("0. Back");
        System.out.println("--------------------------------");
        System.out.print("Please choose a number: 0-1-2: ");
    }

    public static void cartMenu() {
        System.out.println("\nOptions: ");
        System.out.println("--------------------------------");
        System.out.println("1. Filter medias in cart");
        System.out.println("2. Sort medias in cart");
        System.out.println("3. Remove media from cart");
        System.out.println("4. Play a media");
        System.out.println("5. Place order");
        System.out.println("0. Back");
        System.out.println("--------------------------------");
        System.out.print("Please choose a number: 0-1-2-3-4-5: ");
    }

    // -- menu ---
    public static void viewStoreMenu() {
        boolean back = false;
        while (!back) {
            store.printStore();
            storeMenu();
            int choice = getUserChoice(0, 4);
            switch (choice) {
                case 1:
                    System.out.print("Enter media title: ");
                    String title = scanner.nextLine();
                    Media media = store.searchByTitle(title);
                    if (media != null) {
                        System.out.println(media.toString());
                        handleMediaDetails(media);
                    } else {
                        System.out.println("Media not found!");
                    }
                    break;
                case 2:
                    System.out.print("Enter media title to add: ");
                    String addTitle = scanner.nextLine();
                    Media addMedia = store.searchByTitle(addTitle);
                    if (addMedia != null) {
                        cart.addMedia(addMedia);
                    } else {
                        System.out.println("Media not found in store!");
                    }
                    break;
                case 3:
                    System.out.print("Enter media title to play: ");
                    String playTitle = scanner.nextLine();
                    Media playMedia = store.searchByTitle(playTitle);
                    if (playMedia != null) {
                        if (playMedia instanceof Playable) {
                            try {
                                ((Playable) playMedia).play();
                            } catch (PlayerException e) {
                                System.err.println(e.toString());
                                e.printStackTrace();
                                // Bật Dialog GUI thông báo lỗi
                                JOptionPane.showMessageDialog(null, e.getMessage(), "Illegal DVD Length", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            System.out.println("This media cannot be played (Not a CD/DVD).");
                        }
                    } else {
                        System.out.println("Media not found!");
                    }
                    break;
                case 4:
                    seeCurrentCartMenu();
                    break;
                case 0:
                    back = true;
                    break;
            }
        }
    }

    public static void handleMediaDetails(Media media) {
        boolean back = false;
        while (!back) {
            mediaDetailsMenu(media);
            int maxOption = (media instanceof Playable) ? 2 : 1;
            int choice = getUserChoice(0, maxOption);
            
            if (choice == 1) {
                cart.addMedia(media);
            } else if (choice == 2 && media instanceof Playable) {
                try {
                    ((Playable) media).play();
                } catch (PlayerException e) {
                    System.err.println(e.toString());
                    e.printStackTrace();
                    // Bật Dialog GUI thông báo lỗi
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Illegal DVD Length", JOptionPane.ERROR_MESSAGE);
                }
            } else if (choice == 0) {
                back = true;
            }
        }
    }

    public static void updateStoreMenu() {
        System.out.println("\n--- Update Store ---");
        System.out.println("1. Add new media");
        System.out.println("2. Remove a media");
        System.out.println("0. Back");
        System.out.print("Choose option: ");
        int choice = getUserChoice(0, 2);
        
        if (choice == 1) {
            System.out.println("Feature: Add media manually (To be implemented). For now, use code to add.");
        } else if (choice == 2) {
            System.out.print("Enter title to remove: ");
            String title = scanner.nextLine();
            store.removeMediabyTitle(title);
        }
    }

    public static void seeCurrentCartMenu() {
        boolean back = false;
        while (!back) {
            cart.printAll();
            cartMenu();
            int choice = getUserChoice(0, 5);
            switch (choice) {
                case 1:
                    System.out.println("1. Filter by ID\n2. Filter by Title");
                    int filterChoice = getUserChoice(1, 2);
                    if (filterChoice == 1) {
                        System.out.print("Enter ID: ");
                        cart.findMedia(getUserChoice(0, 9999));
                    } else {
                        System.out.print("Enter Title: ");
                        cart.findMedia(scanner.nextLine());
                    }
                    break;
                case 2:
                    System.out.println("1. Sort by Title\n2. Sort by Cost");
                    int sortChoice = getUserChoice(1, 2);
                    if (sortChoice == 1) cart.sortMediaByTitle();
                    else cart.sortMediaByCost();
                    break;
                case 3:
                    System.out.print("Enter title to remove from cart: ");
                    String rmTitle = scanner.nextLine();
                    Media rmMedia = cart.searchByTitle(rmTitle);
                    if (rmMedia != null) cart.removeMedia(rmMedia);
                    else System.out.println("Media not found in cart!");
                    break;
                case 4:
                    System.out.print("Enter media title to play: ");
                    String playTitle = scanner.nextLine();
                    Media playMedia = cart.searchByTitle(playTitle);
                    if (playMedia != null && playMedia instanceof Playable) {
                        try {
                            ((Playable) playMedia).play();
                        } catch (PlayerException e) {
                            System.err.println(e.toString());
                            e.printStackTrace();
                            // Bật Dialog GUI thông báo lỗi
                            JOptionPane.showMessageDialog(null, e.getMessage(), "Illegal DVD Length", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        System.out.println("Media not found or cannot be played!");
                    }
                    break;
                case 5:
                    System.out.println("An order has been created. Your cart is now empty!");
                    cart.empty();
                    back = true; 
                    break;
                case 0:
                    back = true;
                    break;
            }
        }
    }
    
    private static int getUserChoice(int min, int max) {
        int choice = -1;
        while (choice < min || choice > max) {
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice < min || choice > max) {
                    System.out.printf("Invalid choice. Please enter a number between %d and %d: ", min, max);
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
        return choice;
    }

    private static void initData() {
        DigitalVideoDisc dvd1 = new DigitalVideoDisc("Toaru Kagaku no Railgun", "Animation", "Misaka Mikoto", 87, 19.95f);
        DigitalVideoDisc dvd2 = new DigitalVideoDisc("Citrus", "Yuri", "Aihara Yuzu", 87, 24.95f);
        CompactDisc cd1 = new CompactDisc("infinite synthesis", "EDM", "fripSide", 20.0f);
        Book book1 = new Book(1, "Your Name", "Romance", 15.5f);
        
        // Đĩa cứng mã hóa lỗi với Length = 0
        DigitalVideoDisc errorDvd = new DigitalVideoDisc("Broken DVD", "Test", "Unknown", 0, 5.0f);
        
        store.addMedia(dvd1);
        store.addMedia(dvd2);
        store.addMedia(cd1);
        store.addMedia(book1);
        store.addMedia(errorDvd);
    }
}