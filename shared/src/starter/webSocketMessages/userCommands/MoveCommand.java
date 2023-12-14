package webSocketMessages.userCommands;

import chess.ChessMove;

public class MoveCommand extends UserGameCommand {
    public final ChessMove move;
    public int gameID;
    public MoveCommand(String authToken, ChessMove move, int gameID) {
        super(authToken);
        this.move = move;
        this.gameID = gameID;
        this.commandType = CommandType.MAKE_MOVE;
    }
}
