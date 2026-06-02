package hust.soict.dsai.swing;

import java.awt.*;
import java.awt.event.*;

public class AWTAccumulator extends Frame {

    private TextField tfInput;
    private TextField tfOutput;

    private int sum = 0;

    public AWTAccumulator() {

        setLayout(new GridLayout(2,2));

        add(new Label("Enter an Integer"));

        tfInput = new TextField(10);
        add(tfInput);

        add(new Label("Accumulated Sum"));

        tfOutput = new TextField(10);
        tfOutput.setEditable(false);
        add(tfOutput);

        tfInput.addActionListener(
                new TFInputListener());

        setTitle("AWT Accumulator");
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
            tfOutput.setText(sum + "");
        }
    }

    public static void main(
            String[] args) {

        new AWTAccumulator();
    }
}