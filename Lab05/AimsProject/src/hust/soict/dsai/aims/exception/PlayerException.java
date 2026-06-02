package hust.soict.dsai.aims.exception;

public class PlayerException extends Exception {

    // Constructor mặc định không tham số
    public PlayerException() {
        super();
    }

    // Constructor nhận vào một thông báo lỗi (Dùng ở Hình 47: throw new PlayerException("ERROR:..."))
    public PlayerException(String message) {
        super(message);
    }

    public PlayerException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor chỉ nhận vào nguyên nhân gốc (Throwable)
    public PlayerException(Throwable cause) {
        super(cause);
    }
}