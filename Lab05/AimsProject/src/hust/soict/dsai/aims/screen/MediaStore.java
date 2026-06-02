package hust.soict.dsai.aims.screen;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import hust.soict.dsai.aims.media.Media;
import hust.soict.dsai.aims.media.Playable;

public class MediaStore extends JPanel {

    private Media media;

    public MediaStore(Media media) {
        this.media = media;

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(media.getTitle());
        title.setFont(new Font(title.getFont().getName(), Font.PLAIN, 20));
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel cost = new JLabel(media.getCost() + " $");
        cost.setAlignmentX(CENTER_ALIGNMENT);

        JPanel container = new JPanel();
        container.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton btnAddToCart = new JButton("Add to cart");
        btnAddToCart.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                null,
                media.getTitle() + " has been added to cart!"
            );
        });
        container.add(btnAddToCart);

        if (media instanceof Playable) {
            JButton btnPlay = new JButton("Play");

            btnPlay.addActionListener(e -> {
                try {
                    ((Playable) media).play();

                    JOptionPane.showMessageDialog(
                        null,
                        "Playing " + media.getTitle(),
                        "Play Media",
                        JOptionPane.INFORMATION_MESSAGE
                    );

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                        null,
                        ex.getMessage(),
                        "Illegal DVD Length",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            });

            container.add(btnPlay);
        }

        this.add(Box.createVerticalGlue());
        this.add(title);
        this.add(cost);
        this.add(Box.createVerticalGlue());
        this.add(container);

        this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }
}