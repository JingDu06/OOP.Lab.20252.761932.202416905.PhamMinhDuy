package hust.soict.dsai.aims.screen;

import hust.soict.dsai.aims.cart.Cart;
import hust.soict.dsai.aims.media.Book;
import hust.soict.dsai.aims.media.CompactDisc;
import hust.soict.dsai.aims.media.DigitalVideoDisc;

public class TestCartScreen {
    public static void main(String[] args) {
        Cart cart = new Cart();

        
        DigitalVideoDisc dvd = new DigitalVideoDisc( "DVD1's Title", "Category 1", "Director 1", 120, 18.06f);
        CompactDisc cd = new CompactDisc( "CD2's Title", "Category 2", 19.64f);
        Book book = new Book(3, "Book3's Title", "Category 3", 8.46f);

        cart.addMedia(dvd);
        cart.addMedia(cd);
        cart.addMedia(book);
        new CartScreen(cart);
    }
}
