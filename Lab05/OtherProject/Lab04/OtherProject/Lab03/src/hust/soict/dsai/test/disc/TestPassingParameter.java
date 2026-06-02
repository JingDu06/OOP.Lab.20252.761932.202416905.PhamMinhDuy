package hust.soict.dsai.test.disc;

import hust.soict.dsai.aims.media.DigitalVideoDisc;

public class TestPassingParameter {
    
    public static void swap(Object o1, Object o2) {
        Object tmp = o1;
        o1 = o2;
        o2 = tmp;
    }

    public static void main(String[] args) {
        DigitalVideoDisc jungleDVD = new DigitalVideoDisc("Jungle");
        DigitalVideoDisc cinderellaDVD = new DigitalVideoDisc("Cinderella");

        // 1. swap KHÔNG hoạt động
        swap(jungleDVD, cinderellaDVD);
        System.out.println("After swap - jungle: " + jungleDVD.getTitle());

        // 2. changeTitle hoạt động
        changeTitle(jungleDVD, cinderellaDVD.getTitle());
        System.out.println("After changeTitle - jungle: " + jungleDVD.getTitle());

        // reset lại
        jungleDVD.setTitle("Jungle");

        // 3. correctSwap (swap nội dung)
        correctSwap(jungleDVD, cinderellaDVD);
        System.out.println("After correct swap - jungle: " + jungleDVD.getTitle());
        System.out.println("After correct swap - cinderella: " + cinderellaDVD.getTitle());
    }

    public static void changeTitle(DigitalVideoDisc dvd, String title) {
        String oldTitle = dvd.getTitle();
        dvd.setTitle(title);
        dvd = new DigitalVideoDisc(oldTitle); // không ảnh hưởng object gốc
    }

    // ⚠️ Không swap object thật, chỉ swap dữ liệu bên trong
    public static void correctSwap(DigitalVideoDisc dvd1, DigitalVideoDisc dvd2) {
        String tmpTitle = dvd1.getTitle();
        dvd1.setTitle(dvd2.getTitle());
        dvd2.setTitle(tmpTitle);
    }
}