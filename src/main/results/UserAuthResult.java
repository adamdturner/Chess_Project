package results;

/**
 * UserAuthResult holds the necessary data that is returned when a user registers or logs in
 */
public record UserAuthResult(String username, String authToken) {}
