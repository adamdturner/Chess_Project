package results;

/**
 * CreateGameResult holds the necessary data that is returned from a game creation
 */
public class CreateGameResult {
    private int gameID;

    public CreateGameResult(int gameID) {
        this.gameID = gameID;
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }
}

