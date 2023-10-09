package chess;

import java.util.Objects;

public class MyPosition implements ChessPosition {

    // This represents a location on the chessboard.
    // This should be represented as a row number from 1-8, and a column number from 1-8.
    // (1,1) corresponds to the bottom left corner (which in chess notation is denoted a1).
    // (8,8) corresponds to the top right corner (h8 in chess notation).

    private int row;
    private int column;

    public MyPosition(int row,int column) {
        // Validate that row and column values are within the chessboard bounds (1 to 8)
        if (row < 1 || row > 8 || column < 1 || column > 8) {
            throw new IllegalArgumentException("Invalid row or column value for a chess position");
        }

        this.row = row;
        this.column = column;
    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public int getColumn() {
        return column;
    }

    // use intelliJ to override the .equals() and .hashCode() methods using the generate command

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        MyPosition that = (MyPosition) object;
        return row == that.row && column == that.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }
}
