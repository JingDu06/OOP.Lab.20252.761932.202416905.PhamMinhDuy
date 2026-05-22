package hust.soict.dsai.aims.media;

import java.util.Comparator;

public class MediaComparatorByCostTitle implements Comparator<Media> {

    @Override
    public int compare(Media o1, Media o2) {

        int costCompare = Float.compare(o2.getCost(), o1.getCost());

        if (costCompare != 0) {
            return costCompare;
        }

        return o1.getTitle().compareTo(o2.getTitle());
    }
}