package chess;

import java.util.Collection;

public class MyRook implements ChessPiece {

    // This class represents a single chess piece, with its corresponding type and team color.

    private ChessGame.TeamColor teamColor;

    public MyRook(ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
    }

    @Override
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    @Override
    public PieceType getPieceType() {
        return PieceType.ROOK;
    }


    // This method is similar to ChessGame::validMoves,
    // except it does not check the turn or Check (king safety) constraints.
    // This method does account for enemy and friendly pieces blocking movement paths.
    // Each of the 6 different implementations of this the ChessPiece class will
    // need a unique pieceMoves method to calculate valid moves for that type of piece.
    // See later in the specs for rules on how each piece moves,
    // as well as a couple special moves that can be implemented for extra credit.
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return null;
    }
}
