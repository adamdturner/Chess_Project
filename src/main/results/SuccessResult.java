package results;

public class SuccessResult extends Result {

    private String authToken;
    private String username;

    public SuccessResult() {
        authToken = "";
        username = "";
    }

    public SuccessResult(String authToken, String username) {
        this.authToken = authToken;
        this.username = username;
    }

    // Getters and setters for each attribute...

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

