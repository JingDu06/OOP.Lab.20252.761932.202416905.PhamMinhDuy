package hust.soict.dsai.swing;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class NumberGrid
        extends JFrame {

    private JButton[] btnNumbers =
            new JButton[10];

    private JButton btnDelete;
    private JButton btnClear;

    private JTextField tfDisplay;

    public NumberGrid() {

        Container cp =
                getContentPane();

        cp.setLayout(
                new BorderLayout());

        tfDisplay =
                new JTextField();

        cp.add(
                tfDisplay,
                BorderLayout.NORTH);

        JPanel panelButtons =
                new JPanel();

        panelButtons.setLayout(
                new GridLayout(
                        4,3));

        ButtonListener listener =
                new ButtonListener();

        for(int i=1;i<=9;i++) {

            btnNumbers[i] =
                    new JButton(
                            i+"");

            btnNumbers[i]
                    .addActionListener(
                            listener);

            panelButtons.add(
                    btnNumbers[i]);
        }

        btnClear =
                new JButton("C");

        btnDelete =
                new JButton("DEL");

        btnNumbers[0] =
                new JButton("0");

        btnClear.addActionListener(
                listener);

        btnDelete.addActionListener(
                listener);

        btnNumbers[0]
                .addActionListener(
                        listener);

        panelButtons.add(
                btnClear);

        panelButtons.add(
                btnNumbers[0]);

        panelButtons.add(
                btnDelete);

        cp.add(
                panelButtons,
                BorderLayout.CENTER);

        setTitle("Number Grid");

        setSize(300,300);

        setVisible(true);
    }

    private class ButtonListener
            implements ActionListener {

        @Override
        public void actionPerformed(
                ActionEvent e) {

            String button =
                    e.getActionCommand();

            if(Character.isDigit(
                    button.charAt(0))) {

                tfDisplay.setText(
                        tfDisplay.getText()
                        + button);
            }

            else if(
                    button.equals(
                            "DEL")) {

                String text =
                        tfDisplay.getText();

                if(text.length() > 0) {

                    tfDisplay.setText(
                            text.substring(
                                    0,
                                    text.length()-1));
                }
            }

            else if(
                    button.equals("C")) {

                tfDisplay.setText("");
            }
        }
    }

    public static void main(
            String[] args) {

        new NumberGrid();
    }
}