import javax.swing.JOptionPane;

public class FirstDegreesystem {
     public static void main(String[] args) {

        double a11 = Double.parseDouble(JOptionPane.showInputDialog("Nhập a11: ",JOptionPane.INFORMATION_MESSAGE));
        double a12 = Double.parseDouble(JOptionPane.showInputDialog("Nhập a12: ",JOptionPane.INFORMATION_MESSAGE));
        double b1  = Double.parseDouble(JOptionPane.showInputDialog("Nhập b1: ",JOptionPane.INFORMATION_MESSAGE));

        double a21 = Double.parseDouble(JOptionPane.showInputDialog("Nhập a21: ",JOptionPane.INFORMATION_MESSAGE));
        double a22 = Double.parseDouble(JOptionPane.showInputDialog("Nhập a22: ",JOptionPane.INFORMATION_MESSAGE));
        double b2  = Double.parseDouble(JOptionPane.showInputDialog("Nhập b2: ",JOptionPane.INFORMATION_MESSAGE));

        double D  = a11 * a22 - a21 * a12;
        double D1 = b1 * a22 - b2 * a12;
        double D2 = a11 * b2 - a21 * b1;

        if (D != 0) {
            double x1 = D1 / D;
            double x2 = D2 / D;
            System.out.println("He co nghiem duy nhat: " + x1 +" "+ x2);
        } else {
            if (D1 == 0 && D2 == 0) {
                System.out.println("He co vo so nghiem");
            } else {
                System.out.println("He vo nghiem");
            }
        }
    }
    
}