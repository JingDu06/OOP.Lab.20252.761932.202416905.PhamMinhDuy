import javax.swing.JOptionPane;

public class Pheptinh {
    public static void main(String[] args){
    String strNum1, strNum2;
    strNum1 = JOptionPane.showInputDialog(null,
					  "First Number:", JOptionPane.INFORMATION_MESSAGE);
    double num1 = Double.parseDouble(strNum1);

    strNum2 = JOptionPane.showInputDialog(null,
					  "Second Number:",JOptionPane.INFORMATION_MESSAGE);
    double num2 = Double.parseDouble(strNum2);

    double sum = num1 + num2;

    System.out.println("Tong cua ban la: " + sum + "\n");

    if(num2 == 0) {
	System.out.println("Khong chia duoc\n");
    }
    else {
    double quotient = num1/num2;
    System.out.println("Phep chia cua ban la: " + quotient + "\n" );
    }

    double product = num1 * num2;

    System.out.println("Phep nhan cua ban la: " + product + "\n");

    double difference = num1 - num2;

    System.out.println("Phep tru cua ban la: " + difference + "\n");

    System.exit(0);
    }
}