package project.avery.utilities.config;

public class InvalidConverterException extends Exception {
    public InvalidConverterException() {
    }

    public InvalidConverterException(final String msg) {
        super(msg);
    }

    public InvalidConverterException(final Throwable cause) {
        super(cause);
    }

    public InvalidConverterException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
