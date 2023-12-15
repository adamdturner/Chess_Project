package webSocketMessages.userCommands;

import models.Game;

public class JoinObserverCommand extends UserGameCommand{
    public int gameID;
    public JoinObserverCommand(String authToken, int gameId) {
        super(authToken);
        this.gameID = gameId;
        this.commandType = CommandType.JOIN_OBSERVER;
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }
}
