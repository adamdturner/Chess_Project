package webSocketMessages.userCommands;

public class ResignGameCommand extends UserGameCommand {
    private int gameID;
    public ResignGameCommand(String authToken, int gameId) {
        super(authToken);
        this.gameID = gameId;
        this.commandType = CommandType.RESIGN;
    }

    public int getGameId() {
        return gameID;
    }

    public void setGameId(int gameId) {
        this.gameID = gameId;
    }
}
