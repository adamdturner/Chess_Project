package webSocketMessages.userCommands;

import models.Game;

public class JoinObserverCommand extends UserGameCommand{
    public Game game;
    public JoinObserverCommand(String authToken, Game game) {
        super(authToken);
        this.game = game;
        this.commandType = CommandType.JOIN_OBSERVER;
    }
}
