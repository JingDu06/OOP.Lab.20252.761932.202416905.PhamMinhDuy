package hust.soict.dsai.aims.screen;

import javax.swing.*;

public class AddCompactDiscToStoreScreen extends AddItemToStoreScreen {
    private JTextField tfArtist;

    public AddCompactDiscToStoreScreen() {
        super("CD");

        centerPanel.add(new JLabel("  Artist: "));
        tfArtist = new JTextField();
        centerPanel.add(tfArtist);

        setVisible(true);
    }
}