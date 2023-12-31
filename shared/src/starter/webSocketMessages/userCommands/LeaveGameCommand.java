package webSocketMessages.userCommands;

public class LeaveGameCommand extends UserGameCommand {
    private int gameID;
    public LeaveGameCommand(String authToken, int gameId) {
        super(authToken);
        this.gameID = gameId;
        this.commandType = CommandType.LEAVE;
    }

    public int getGameId() {
        return gameID;
    }

    public void setGameId(int gameId) {
        this.gameID = gameId;
    }
}
