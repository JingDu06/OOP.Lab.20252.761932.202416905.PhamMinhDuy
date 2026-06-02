package hust.soict.dsai.aims.media;

import java.util.Comparator;

public abstract class Media implements Comparable<Media> {
    private int id;
    private String title;
    private String category;
    private float cost;
    
    // Thêm 2 hằng số Comparator theo yêu cầu Phần 12
    public static final Comparator<Media> COMPARE_BY_TITLE_COST = new MediaComparatorByTitleCost();
    public static final Comparator<Media> COMPARE_BY_COST_TITLE = new MediaComparatorByCostTitle();

    public Media(int id, String title, String category, float cost) {
        super();
        this.id = id;
        this.title = title;
        this.category = category;
        this.cost = cost;
    }
    
    public Media(String title, String category, float cost) {
        super();
        this.title = title;
        this.category = category;
        this.cost = cost;
    }
    
    public Media(String title) {
        super();
        this.title = title;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || !(obj instanceof Media)) return false;
        
        Media other = (Media) obj;

        if (this.getTitle() == null) {
            return other.getTitle() == null;
        }
        
        return this.getTitle().equalsIgnoreCase(other.getTitle());
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    // Cập nhật toString() cho Phần 11
    @Override
    public String toString() {
        return String.format("Media [ID=%d, Title='%s', Category='%s', Cost=%.2f]", 
                             this.getId(), this.getTitle(), this.getCategory(), this.getCost());
    }
}