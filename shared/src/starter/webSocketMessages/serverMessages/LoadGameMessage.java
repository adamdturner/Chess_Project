package webSocketMessages.serverMessages;

import models.Game;

// This is an example. Adapt it based on your actual game state needs.
public class LoadGameMessage extends ServerMessage {
    private final Game game;

    public LoadGameMessage(Game game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public Game getGame() {
        return game;
    }
}
