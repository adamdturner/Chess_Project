package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MyPawn implements ChessPiece {

    // This class represents a single chess piece, with its corresponding type and team color.

    private ChessGame.TeamColor teamColor;

    public MyPawn(ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
    }

    @Override
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    @Override
    public PieceType getPieceType() {
        return PieceType.PAWN;
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

        // Define the possible move offsets for a pawn (forward and capture diagonally)
        int forwardOffset = (teamColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int captureOffset = (teamColor == ChessGame.TeamColor.WHITE) ? 1 : -1;

        // Check the forward move
        int newRow = myRow + forwardOffset;
        int newCol = myCol;
        if (newRow >= 1 && newRow <= 8) {
            ChessPosition newPosition = new MyPosition(newRow, newCol);
            ChessPiece targetPiece = board.getPiece(newPosition);

            // If the target position is empty, it's a valid move
            if (targetPiece == null) {
                // Check if the pawn has reached the opposite end for promotion
                if (newRow == 1 || newRow == 8) {
                    // Add promotion moves for Queen, Rook, Bishop, and Knight
                    validMoves.add(new MyMove(myPosition, newPosition, PieceType.QUEEN));
                    validMoves.add(new MyMove(myPosition, newPosition, PieceType.ROOK));
                    validMoves.add(new MyMove(myPosition, newPosition, PieceType.BISHOP));
                    validMoves.add(new MyMove(myPosition, newPosition, PieceType.KNIGHT));
                }
                else {
                    validMoves.add(new MyMove(myPosition, newPosition, null));
                }

                // Check the initial two-square move for pawns (only on their starting rank)
                if ((teamColor == ChessGame.TeamColor.WHITE && myRow == 2) ||
                        (teamColor == ChessGame.TeamColor.BLACK && myRow == 7)) {
                    newRow = myRow + 2 * forwardOffset;
                    newPosition = new MyPosition(newRow, newCol);
                    targetPiece = board.getPiece(newPosition);

                    // If the two-square forward position is empty, it's a valid move
                    if (targetPiece == null) {
                        validMoves.add(new MyMove(myPosition, newPosition, null));
                    }
                }
            }
        }

        // Check capture moves diagonally
        int[] captureCols = {myCol - 1, myCol + 1};
        for (int captureCol : captureCols) {
            if (captureCol >= 1 && captureCol <= 8) {
                newRow = myRow + captureOffset;
                newCol = captureCol;
                ChessPosition newPosition = new MyPosition(newRow, newCol);
                ChessPiece targetPiece = board.getPiece(newPosition);

                // If the target position contains an opponent's piece, it's a valid capture move
                if (targetPiece != null && targetPiece.getTeamColor() != teamColor) {
                    // Check if the pawn has reached the opposite end for promotion
                    if (newRow == 1 || newRow == 8) {
                        // Add promotion moves for Queen, Rook, Bishop, and Knight
                        validMoves.add(new MyMove(myPosition, newPosition, PieceType.QUEEN));
                        validMoves.add(new MyMove(myPosition, newPosition, PieceType.ROOK));
                        validMoves.add(new MyMove(myPosition, newPosition, PieceType.BISHOP));
                        validMoves.add(new MyMove(myPosition, newPosition, PieceType.KNIGHT));
                    } else {
                        validMoves.add(new MyMove(myPosition, newPosition, null));
                    }
                }
            }
        }

        return validMoves;
    }
}
