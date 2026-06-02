package hust.soict.dsai.aims.exception;

public class LimitExceededException extends Exception {

    public LimitExceededException() {
        super();
    }

    // Constructor này sẽ được gọi khi bạn ném ngoại lệ: throw new LimitExceededException("ERROR: The number of media has reached its limit");
    public LimitExceededException(String message) {
        super(message);
    }

    public LimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }

    public LimitExceededException(Throwable cause) {
        super(cause);
    }
}
