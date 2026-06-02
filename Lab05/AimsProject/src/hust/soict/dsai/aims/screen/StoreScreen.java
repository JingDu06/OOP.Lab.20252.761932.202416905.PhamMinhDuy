package hust.soict.dsai.aims.screen;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

import hust.soict.dsai.aims.store.Store;
import hust.soict.dsai.aims.media.*;
public class StoreScreen extends JFrame{
    private Store store;

    JPanel createNorth() {
        JPanel north = new JPanel();
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        north.add(createMenuBar());
        north.add(createHeader());
        return north;
    }

    JMenuBar createMenuBar() {
        JMenu menu = new JMenu("Options");

        JMenu smUpdateStore = new JMenu("Update Store");
        
        JMenuItem btnAddBook = new JMenuItem("Add Book");
        btnAddBook.addActionListener(e -> new AddBookToStoreScreen());
        smUpdateStore.add(btnAddBook);

        // Ghép nối lớp màn hình AddCD mới tạo
        JMenuItem btnAddCD = new JMenuItem("Add CD");
        btnAddCD.addActionListener(e -> new AddCompactDiscToStoreScreen());
        smUpdateStore.add(btnAddCD);

        // Ghép nối lớp màn hình AddDVD mới tạo
        JMenuItem btnAddDVD = new JMenuItem("Add DVD");
        btnAddDVD.addActionListener(e -> new AddDigitalVideoDiscToStoreScreen());
        smUpdateStore.add(btnAddDVD);

        menu.add(smUpdateStore);
        menu.add(new JMenuItem("View store"));
        menu.add(new JMenuItem("View cart"));

        JMenuBar menuBar = new JMenuBar();
        menuBar.setLayout(new FlowLayout(FlowLayout.LEFT));
        menuBar.add(menu);

        return menuBar;
    }
        

    JPanel createHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout (header, BoxLayout.X_AXIS));

        JLabel title = new JLabel("AIMS");
        title.setFont(new Font(title.getFont().getName(), Font.PLAIN, 50));
        title.setForeground (Color.CYAN);

        JButton cart = new JButton("View cart");
        cart.setPreferredSize (new Dimension (100, 50));
        cart.setMaximumSize(new Dimension(100, 50));

        header.add(Box.createRigidArea (new Dimension (10, 10)));
        header.add(title);
        header.add(Box.createHorizontalGlue());
        header.add(cart);
        header.add(Box.createRigidArea (new Dimension (10, 10)));

        return header;
    }

    JPanel createCenter() {
        JPanel center = new JPanel();
        center.setLayout(new GridLayout (3, 3, 2, 2));

        ArrayList<Media> mediaInStore = store.getItemsInStore();
        
        // ĐÃ SỬA: Lấy kích thước thực tế của danh sách, tối đa là 9 để vừa khung 3x3
        int limit = Math.min(mediaInStore.size(), 9); 
        
        for (int i = 0; i < limit; i++) {
            MediaStore cell = new MediaStore (mediaInStore.get(i));
            center.add(cell);
        }

        return center;
    }

    public StoreScreen (Store store) {
        this.store = store;
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());

        cp.add(createNorth(), BorderLayout.NORTH);
        cp.add(createCenter(), BorderLayout.CENTER);

        setVisible(true);
        setTitle("Store");
        setSize(1024, 768);
    }
    public void validateAndRefreshStore() {
        BorderLayout layout = (BorderLayout) getContentPane().getLayout();
        Component centerComponent = layout.getLayoutComponent(BorderLayout.CENTER);
        if (centerComponent != null) {
            getContentPane().remove(centerComponent);
        }

        getContentPane().add(createCenter(), BorderLayout.CENTER);

        revalidate();
        repaint();
    }
}