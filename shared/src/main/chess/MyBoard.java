package chess;

public class MyBoard implements ChessBoard {

    // This class stores all the uncaptured pieces in a Game.
    // It needs to support adding and removing pieces for testing,
    // as well as a resetBoard() method that sets the standard Chess starting configuration.

    // Create a 2D array to represent the chessboard
    private final ChessPiece[][] board;

    // Constructor
    public MyBoard() {
        // Initialize the board in the constructor
        board = new ChessPiece[8][8];
        // turns out that we don't want to reset the board when initializing
        // a new board because that will fail the piece move tests.
        // When creating a board it should start out entirely empty
    }


    // returns the current board as it is
    @Override
    public ChessPiece[][] getBoard() {
        return this.board;
    }

    // Adds a chess piece to the chessboard
    //     @param position where to add the piece to
    //     @param piece    the piece to add
    @Override
    public void addPiece(ChessPosition position, ChessPiece piece) {
        // Add the piece to the specified position on the board
        int row = position.getRow();
        int col = position.getColumn();
        board[row-1][col-1] = piece;        // included the -1 in the array because the indices go from 0-7
    }


    // Gets a chess piece on the chessboard
    //     @param position The position to get the piece from
    //     @return Either the piece at the position, or null if no piece is at that
    //             position
    @Override
    public ChessPiece getPiece(ChessPosition position) {
        // Get the piece at the specified position on the board
        int row = position.getRow();
        int col = position.getColumn();
        return board[row-1][col-1];         // included the -1 in the array because the indices go from 0-7
    }



    // Sets the board to the default starting board
    //     (How the game of chess normally starts)
    @Override
    public void resetBoard() {
        // Clear the board
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                board[row][col] = null;
            }
        }

        // Set up the pieces for the starting position

        // White pieces (bottom two rows)
        for (int col = 0; col < 8; col++) {
            board[1][col] = new MyPawn(ChessGame.TeamColor.WHITE);
        }
        board[0][0] = new MyRook(ChessGame.TeamColor.WHITE);
        board[0][1] = new MyKnight(ChessGame.TeamColor.WHITE);
        board[0][2] = new MyBishop(ChessGame.TeamColor.WHITE);
        board[0][3] = new MyQueen(ChessGame.TeamColor.WHITE);
        board[0][4] = new MyKing(ChessGame.TeamColor.WHITE);
        board[0][5] = new MyBishop(ChessGame.TeamColor.WHITE);
        board[0][6] = new MyKnight(ChessGame.TeamColor.WHITE);
        board[0][7] = new MyRook(ChessGame.TeamColor.WHITE);

        // Black pieces (top two rows)
        for (int col = 0; col < 8; col++) {
            board[6][col] = new MyPawn(ChessGame.TeamColor.BLACK);
        }
        board[7][0] = new MyRook(ChessGame.TeamColor.BLACK);
        board[7][1] = new MyKnight(ChessGame.TeamColor.BLACK);
        board[7][2] = new MyBishop(ChessGame.TeamColor.BLACK);
        board[7][3] = new MyQueen(ChessGame.TeamColor.BLACK);
        board[7][4] = new MyKing(ChessGame.TeamColor.BLACK);
        board[7][5] = new MyBishop(ChessGame.TeamColor.BLACK);
        board[7][6] = new MyKnight(ChessGame.TeamColor.BLACK);
        board[7][7] = new MyRook(ChessGame.TeamColor.BLACK);
    }
}
