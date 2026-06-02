package hust.soict.dsai.aims.media;

import java.util.Comparator;

public class MediaComparatorByCostTitle implements Comparator<Media> {
    @Override
    public int compare(Media m1, Media m2) {
        // Sắp xếp theo Cost giảm dần (Decreasing)
        int costDiff = Double.compare(m2.getCost(), m1.getCost());
        if (costDiff != 0) {
            return costDiff;
        }
        return m1.getTitle().compareToIgnoreCase(m2.getTitle());
    }
}