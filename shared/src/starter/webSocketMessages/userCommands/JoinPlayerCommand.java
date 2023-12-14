package webSocketMessages.userCommands;

import chess.ChessGame;

public class JoinPlayerCommand extends UserGameCommand {

    public int gameID;
    public final ChessGame.TeamColor playerColor;
    public JoinPlayerCommand(String authToken, int gameId, ChessGame.TeamColor playerColor) {
        super(authToken);
        this.gameID = gameId;
        this.playerColor = playerColor;
        this.commandType = CommandType.JOIN_PLAYER;
    }

    public int getGameID() {
        return gameID;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }
}
