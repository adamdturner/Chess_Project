package requests;

/**
 * ListGamesRequest holds the necessary data used in the process of getting a list of games
 */
public class ListGamesRequest {

    private String authToken;

    public ListGamesRequest(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
