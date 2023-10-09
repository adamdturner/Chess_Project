package chess;

import java.util.Collection;

public class MyGame implements ChessGame {


    @Override
    public TeamColor getTeamTurn() {
        return null;
    }

    @Override
    public void setTeamTurn(TeamColor team) {

    }

    // Takes as input a position on the chessboard and returns all moves the piece there can legally make.
    // If there is no piece at that location, this method returns null.
    @Override
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        return null;
    }

    // Receives a given move and executes it, provided it is a legal move.
    // If the move is illegal, it throws a InvalidMoveException.
    // A move is illegal if the chess piece cannot move there,
    // if the move leaves the team’s king in danger, or if it’s not the corresponding teams turn.
    @Override
    public void makeMove(ChessMove move) throws InvalidMoveException {

    }

    // Returns true if the specified team’s King could be captured by an opposing piece.
    @Override
    public boolean isInCheck(TeamColor teamColor) {
        return false;
    }

    // Returns true if the given team has no way to protect their king from being captured.
    @Override
    public boolean isInCheckmate(TeamColor teamColor) {
        return false;
    }

    // Returns true if the given team has no legal moves, and it is currently that team’s turn.
    @Override
    public boolean isInStalemate(TeamColor teamColor) {
        return false;
    }

    @Override
    public void setBoard(ChessBoard board) {

    }

    @Override
    public ChessBoard getBoard() {
        return null;
    }
}
