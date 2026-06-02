package hust.soict.dsai.aims.screen;

import javax.swing.*;

public class AddDigitalVideoDiscToStoreScreen extends AddItemToStoreScreen {
    private JTextField tfDirector, tfLength;

    public AddDigitalVideoDiscToStoreScreen() {
        super("DVD");
        
        centerPanel.add(new JLabel("  Director: "));
        tfDirector = new JTextField();
        centerPanel.add(tfDirector);

        centerPanel.add(new JLabel("  Length: "));
        tfLength = new JTextField();
        centerPanel.add(tfLength);

        setVisible(true);
    }
}