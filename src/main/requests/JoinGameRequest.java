package requests;

public class JoinGameRequest {

    String playerColor;
    int gameID;
    String authToken;

    public JoinGameRequest(String playerColor, int gameID, String authToken) {
        this.playerColor = playerColor;
        this.gameID = gameID;
        this.authToken = authToken;
    }

    public String getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(String playerColor) {
        this.playerColor = playerColor;
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
