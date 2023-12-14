package webSocketMessages.userCommands;

import models.User;

public class LeaveGameCommand extends UserGameCommand {
    private int gameId;
    public LeaveGameCommand(String authToken, int gameId) {
        super(authToken);
        this.gameId = gameId;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }
}
