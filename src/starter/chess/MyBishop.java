package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MyBishop implements ChessPiece {

    // This class represents a single chess piece, with its corresponding type and team color.

    private ChessGame.TeamColor teamColor;

    public MyBishop(ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
    }

    @Override
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    @Override
    public PieceType getPieceType() {
        return PieceType.BISHOP;
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

        // Define the possible move offsets for a bishop (diagonal directions)
        int[] rowOffsets = {-1, -1, 1, 1};
        int[] colOffsets = {-1, 1, -1, 1};

        // Check each diagonal direction
        for (int i = 0; i < 4; i++) {
            for (int step = 1; step <= 7; step++) {
                int newRow = myRow + step * rowOffsets[i];
                int newCol = myCol + step * colOffsets[i];

                // Check if the new position is within the bounds of the chessboard
                if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                    ChessPosition newPosition = new MyPosition(newRow, newCol);
                    ChessPiece targetPiece = board.getPiece(newPosition);

                    // If the target position is empty, it's a valid move
                    if (targetPiece == null) {
                        validMoves.add(new MyMove(myPosition, newPosition, null));
                    } else {
                        // If the target position contains an opponent's piece, it's a valid capture move
                        if (targetPiece.getTeamColor() != teamColor) {
                            validMoves.add(new MyMove(myPosition, newPosition, null));
                        }
                        break; // Stop searching in this direction if a piece is encountered
                    }
                } else {
                    // Stop searching in this direction if it's out of bounds
                    break;
                }
            }
        }

        return validMoves;
    }
}
