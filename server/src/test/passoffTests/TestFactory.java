package passoffTests;

import chess.*;

/**
 * Used for testing your code
 * Add in code using your classes for each method for each FIXME
 */
public class TestFactory {

    //Chess Functions
    //------------------------------------------------------------------------------------------------------------------
    public static ChessBoard getNewBoard(){
        // Create and return an instance of your ChessBoard implementation (e.g., MyBoard)
		return new MyBoard();
    }

    public static ChessGame getNewGame(){
        // Create and return an instance of your ChessGame implementation (e.g., MyGame)
		return new MyGame();
    }

    public static ChessPiece getNewPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type){
        // Create and return an instance of your ChessPiece implementation (e.g., MyKing, MyQueen, etc.)
        switch (type) {
            case KING:
                return new MyKing(pieceColor);
            case QUEEN:
                return new MyQueen(pieceColor);
            case BISHOP:
                return new MyBishop(pieceColor);
            case ROOK:
                return new MyRook(pieceColor);
            case PAWN:
                return new MyPawn(pieceColor);
            case KNIGHT:
                return new MyKnight(pieceColor);
            default:
                throw new IllegalArgumentException("Invalid piece type: " + type);
        }
    }

    public static ChessPosition getNewPosition(Integer row, Integer col){
        // Create and return an instance of your ChessPosition implementation (e.g., MyPosition)
		return new MyPosition(row,col);
    }

    public static ChessMove getNewMove(ChessPosition startPosition, ChessPosition endPosition, ChessPiece.PieceType promotionPiece){
        // Create and return an instance of your ChessMove implementation (e.g., MyMove)
		return new MyMove(startPosition,endPosition,promotionPiece);
    }
    //------------------------------------------------------------------------------------------------------------------


    //Server API's
    //------------------------------------------------------------------------------------------------------------------
    public static String getServerPort(){
        return "8080";
    }
    //------------------------------------------------------------------------------------------------------------------


    //Websocket Tests
    //------------------------------------------------------------------------------------------------------------------
    public static Long getMessageTime(){
        /*
        Changing this will change how long tests will wait for the server to send messages.
        3000 Milliseconds (3 seconds) will be enough for most computers. Feel free to change as you see fit,
        just know increasing it can make tests take longer to run.
        (On the flip side, if you've got a good computer feel free to decrease it)
         */
        return 3000L;
    }
    //------------------------------------------------------------------------------------------------------------------
}
