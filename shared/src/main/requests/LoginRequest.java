package requests;

/**
 * LoginRequest holds the necessary data used in the process of logging into a game
 */
public record LoginRequest(String username, String password) {
}

