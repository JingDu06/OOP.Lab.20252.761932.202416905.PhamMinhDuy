package hust.soict.dsai.aims.media;

import java.util.Comparator;

public class MediaComparatorByTitleCost implements Comparator<Media> {
    @Override
    public int compare(Media m1, Media m2) {
        // Sắp xếp theo Title (Alphabetical)
        int titleDiff = m1.getTitle().compareToIgnoreCase(m2.getTitle());
        if (titleDiff != 0) {
            return titleDiff;
        }
        return Double.compare(m2.getCost(), m1.getCost());
    }
}