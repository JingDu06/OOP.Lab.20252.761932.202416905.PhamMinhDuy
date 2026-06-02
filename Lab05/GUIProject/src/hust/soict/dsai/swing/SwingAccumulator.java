package hust.soict.dsai.swing;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class SwingAccumulator
        extends JFrame {

    private JTextField tfInput;
    private JTextField tfOutput;

    private int sum = 0;

    public SwingAccumulator() {

        Container cp =
                getContentPane();

        cp.setLayout(
                new GridLayout(2,2));

        cp.add(
                new JLabel(
                        "Enter an Integer"));

        tfInput =
                new JTextField(10);

        cp.add(tfInput);

        cp.add(
                new JLabel(
                        "Accumulated Sum"));

        tfOutput =
                new JTextField(10);

        tfOutput.setEditable(false);

        cp.add(tfOutput);

        tfInput.addActionListener(
                new TFInputListener());

        setTitle(
                "Swing Accumulator");

        setSize(350,120);

        setVisible(true);
    }

    private class TFInputListener
            implements ActionListener {

        @Override
        public void actionPerformed(
                ActionEvent evt) {

            int number =
                    Integer.parseInt(
                            tfInput.getText());

            sum += number;

            tfInput.setText("");

            tfOutput.setText(
                    sum + "");
        }
    }

    public static void main(
            String[] args) {

        new SwingAccumulator();
    }
}