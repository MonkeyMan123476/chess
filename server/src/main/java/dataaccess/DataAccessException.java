package dataaccess;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception{

    public enum Code {
        ServerError,
        ClientError,
    }

    final private Code code;

    public DataAccessException(String message, Code code) {
        super(message);
        this.code = code;
    }
    public DataAccessException(String message, Throwable ex, Code code) {
        super(message, ex);
        this.code = code;
    }
}
