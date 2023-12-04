package chess;

import java.util.Objects;

public class MyMove implements ChessMove {

    // This class represents a possible move a chess piece could make.
    // It contains the starting and ending positions.
    // It also contains a field for the type of piece a pawn is being promoted to.
    // If the move would not result in a pawn being promoted, the promotion type field should be null.

    private ChessPosition startPosition;
    private ChessPosition endPosition;
    private ChessPiece.PieceType promotionPiece;

    public MyMove(ChessPosition startPosition, ChessPosition endPosition, ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }


    @Override
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    @Override
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    @Override
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

    // use intelliJ to override the .equals() and .hashCode() methods using the generate command

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        MyMove myMove = (MyMove) object;
        return Objects.equals(startPosition, myMove.startPosition) && Objects.equals(endPosition, myMove.endPosition) && promotionPiece == myMove.promotionPiece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPosition, endPosition, promotionPiece);
    }
}
