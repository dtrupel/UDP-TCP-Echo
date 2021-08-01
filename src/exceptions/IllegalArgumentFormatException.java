package exceptions;

public class IllegalArgumentFormatException extends IllegalArgumentException {

    private final String message;

    public IllegalArgumentFormatException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
