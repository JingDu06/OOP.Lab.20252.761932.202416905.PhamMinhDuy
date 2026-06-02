package hust.soict.dsai.aims.store;

import hust.soict.dsai.aims.media.Media;
import java.util.ArrayList; //
public class Store {
    private static final int MAX_NUMBER_ITEMS = 40;
    private ArrayList<Media> itemsInStore = new ArrayList<Media>();
    public ArrayList<Media> getItemsInStore() {
        return itemsInStore;
    }

    public void setItemsInStore(ArrayList<Media> itemsInStore) {
        this.itemsInStore = itemsInStore;
    }
    public int numberItems = 0;
    public Store(){

    }

    public void addMedia(Media d){
        if(numberItems < MAX_NUMBER_ITEMS){
            itemsInStore.add(d);
            numberItems++;
            System.out.println("The media has been added: " + d.getTitle());
        }
        else{
            System.out.println("The store is almost full");
        }
    }
    public Media searchByTitle(String title) {
        for (Media m : itemsInStore) {
            if (m.getTitle().equalsIgnoreCase(title)) {
                return m;
            }
        }
        return null;
    }
    public void removeMediabyTitle(String title){
        boolean found = false;
        for(Media m : itemsInStore){
            if(m.getTitle().equals(title)){
                itemsInStore.remove(m);
                numberItems--;
                System.out.println("The media has been removed: " + m.getTitle());
                found = true;
                break;
            }
        }
        if(!found){
            System.out.println("Media not found in store.");
        }
    }
    public void printStore(){
        System.out.println("*******************************STORE******************************");
        System.out.println("Ordered items: ");
        for(Media d : itemsInStore){
            System.out.println(d.toString());
        }
        System.out.println("******************************************************************");
    }
}