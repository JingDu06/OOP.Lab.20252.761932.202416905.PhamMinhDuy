import javax.swing.JOptionPane;

public class FirstDegreeEquation {
    public static void main(String[] args){
	String strNum1, strNum2;

	strNum1 = JOptionPane.showInputDialog(null,
					      "a is: ",
					      JOptionPane.INFORMATION_MESSAGE);
	double a = Double.parseDouble(strNum1);

	strNum2 = JOptionPane.showInputDialog(null,
					      "b is: ",
					      JOptionPane.INFORMATION_MESSAGE);
	double b = Double.parseDouble(strNum2);
    
	if(a == 0){
        if(b!=0) {
	    System.out.println("Phuong trinh vo nghiem\n");
        }
        else System.out.println("Phuong trinh co vo so nghiem");
        System.exit(1);
	}

	System.out.println("Nghiem cua phuong trinh la: " + -b/a + "\n");
	System.exit(0);
    }
}