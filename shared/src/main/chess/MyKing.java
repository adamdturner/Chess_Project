package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MyKing implements ChessPiece {

    // This class represents a single chess piece, with its corresponding type and team color.

    private final ChessGame.TeamColor teamColor;
    private final PieceType pieceType;

    public MyKing(ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
        this.pieceType = PieceType.KING;
    }

    @Override
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    @Override
    public PieceType getPieceType() {
        return pieceType;
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
        Set<ChessMove> validMoves = new HashSet<>();
        int myRow = myPosition.getRow();
        int myCol = myPosition.getColumn();

        // Define the possible move offsets for a king
        int[] rowOffsets = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] colOffsets = {-1, 0, 1, -1, 1, -1, 0, 1};

        // Check each possible move direction
        for (int i = 0; i < 8; i++) {
            int newRow = myRow + rowOffsets[i];
            int newCol = myCol + colOffsets[i];

            // Check if the new position is within the bounds of the chessboard
            if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                ChessPosition newPosition = new MyPosition(newRow, newCol);
                ChessPiece targetPiece = board.getPiece(newPosition);

                // Check if the target position is either empty or contains an opponent's piece
                if (targetPiece == null || targetPiece.getTeamColor() != teamColor) {
                    validMoves.add(new MyMove(myPosition, newPosition, null));
                }
            }
        }

        return validMoves;
    }
}
