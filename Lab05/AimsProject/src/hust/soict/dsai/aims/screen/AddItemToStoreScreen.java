package hust.soict.dsai.aims.screen;

import javax.swing.*;
import java.awt.*;

public abstract class AddItemToStoreScreen extends JFrame {
    protected JTextField tfTitle, tfCategory, tfCost;
    protected JPanel centerPanel;

    public AddItemToStoreScreen(String itemType) {
        setTitle("Add " + itemType + " to Store");
        setSize(400, 400);
        
        setLayout(new BorderLayout());
        centerPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        centerPanel.add(new JLabel("  Title: "));
        tfTitle = new JTextField();
        centerPanel.add(tfTitle);

        centerPanel.add(new JLabel("  Category: "));
        tfCategory = new JTextField();
        centerPanel.add(tfCategory);

        centerPanel.add(new JLabel("  Cost: "));
        tfCost = new JTextField();
        centerPanel.add(tfCost);

        add(centerPanel, BorderLayout.CENTER);

        JButton btnAdd = new JButton("Add " + itemType);
        btnAdd.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, itemType + " '" + tfTitle.getText() + "' has been added to the store!");
            dispose(); 
        });
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(btnAdd);
        add(bottomPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); 
    }
}