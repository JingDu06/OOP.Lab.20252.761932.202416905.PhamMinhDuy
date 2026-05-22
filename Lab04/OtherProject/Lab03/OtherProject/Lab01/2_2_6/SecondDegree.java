import javax.swing.JOptionPane;

public class SecondDegree {
    public static void main(String[] args) {

        double a = Double.parseDouble(JOptionPane.showInputDialog("Enter a:"));
        double b = Double.parseDouble(JOptionPane.showInputDialog("Enter b:"));
        double c = Double.parseDouble(JOptionPane.showInputDialog("Enter c:"));

        String result = "";

        if (a == 0) {
            if (b == 0) {
                if (c == 0) {
                    result = "Phuong trinh co vo so nghiem";
                } else {
                    result = "Phuong trinh vo nghiem";
                }
            } else {
                double x = -c / b;
                result = "Phuong trinh co nghiem x1 = x2 =: " + x;
            }
        } else {
            double delta = b * b - 4 * a * c;

            if (delta > 0) {
                double x1 = (-b + Math.sqrt(delta)) / (2 * a);
                double x2 = (-b - Math.sqrt(delta)) / (2 * a);
                result = "Phuong trinh co 2 nghiem phan biet:\n"
                       + "x1 =: " + x1 + "\n"
                       + "x2 =: " + x2;
            } else if (delta == 0) {
                double x = -b / (2 * a);
                result = "Phuong trinh co nghiem kep x =: " + x;
            } else {
                result = "Phuong trinh khong co nghiem thuc";
            }
        }

        JOptionPane.showMessageDialog(null, result,
                "Result", JOptionPane.INFORMATION_MESSAGE);

        System.exit(0);
    }
}