package dataAccess;

/**
 * Indicates that someone was unauthorized, ex. didn't have authToken
 */
public class UnauthorizedException extends Exception {
    public UnauthorizedException(String message) {
        super(message);
    }
}
