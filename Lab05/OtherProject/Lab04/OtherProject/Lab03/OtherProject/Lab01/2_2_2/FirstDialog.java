import javax.swing.JOptionPane;

public class FirstDialog {
    public static void main(String[] args) {
        // Hiển thị một hộp thoại thông báo với nội dung bên dưới
        JOptionPane.showMessageDialog(null, "Hello world! How are you?");
        
        // Thoát chương trình (Lệnh này quan trọng khi dùng Swing/GUI)
        System.exit(0);
    }
}