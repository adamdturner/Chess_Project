package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MyKnight implements ChessPiece {

    // This class represents a single chess piece, with its corresponding type and team color.

    private final ChessGame.TeamColor teamColor;
    private final PieceType pieceType;

    public MyKnight(ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
        this.pieceType = PieceType.KNIGHT;
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

        // Define the possible move offsets for a knight (8 possible moves)
        int[] rowOffsets = {-2, -2, -1, -1, 1, 1, 2, 2};
        int[] colOffsets = {-1, 1, -2, 2, -2, 2, -1, 1};

        // Check each possible move
        for (int i = 0; i < 8; i++) {
            int newRow = myRow + rowOffsets[i];
            int newCol = myCol + colOffsets[i];

            // Check if the new position is within the bounds of the chessboard
            if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                ChessPosition newPosition = new MyPosition(newRow, newCol);
                ChessPiece targetPiece = board.getPiece(newPosition);

                // If the target position is empty or contains an opponent's piece, it's a valid move
                if (targetPiece == null || targetPiece.getTeamColor() != teamColor) {
                    validMoves.add(new MyMove(myPosition, newPosition, null));
                }
            }
        }

        return validMoves;
    }
}
