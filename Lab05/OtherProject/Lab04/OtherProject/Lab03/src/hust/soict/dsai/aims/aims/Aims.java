package hust.soict.dsai.test.aims;

import hust.soict.dsai.aims.cart.Cart;
import hust.soict.dsai.aims.media.DigitalVideoDisc;

public class Aims {
    public static void main(String[] args) {
        Cart cart = new Cart();

        DigitalVideoDisc dvd1 = new DigitalVideoDisc("The Lion King",
                "Animation", "Roger Allers", 87, 19.95f);

        DigitalVideoDisc dvd2 = new DigitalVideoDisc("Star Wars",
                "Science Fiction", "George Lucas", 87, 24.95f);

        DigitalVideoDisc dvd3 = new DigitalVideoDisc("Animation", "Aladin", 18.99f);

        cart.addDigitalVideoDisc(dvd1, dvd2, dvd3);

        cart.print();

        cart.searchByTitle("Lion");
        cart.searchById(1);

        cart.removeDigitalVideoDisc(dvd2);

        System.out.println("After remove:");
        cart.print();
    }
}