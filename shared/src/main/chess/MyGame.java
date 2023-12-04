package chess;

import java.util.Collection;
import java.util.HashSet;

public class MyGame implements ChessGame {

    private TeamColor teamTurn;
    private ChessBoard chessBoard;

    public MyGame() {
        this.initialize();
    }

    @Override
    public void initialize() {
        teamTurn = TeamColor.WHITE;    // initialize to white because white team starts the game
        chessBoard = new MyBoard();
        chessBoard.resetBoard();
    }

    // return which teams turn it is
    @Override
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    // set which team's turn it is
    @Override
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    // Gets a valid moves for a piece at the given location
    // Takes as input a position on the chessboard and returns all moves the piece there can legally make.
    // If there is no piece at that location, this method returns null.
    @Override
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece myPiece = chessBoard.getPiece(startPosition);
        // make sure there is a piece there before checking the moves, if no piece then return null
        if (myPiece == null) {
            return null;
        }
        // create collection of potential moves. Need to verify that each don't endanger the King
        Collection<ChessMove> potentialMoves = myPiece.pieceMoves(chessBoard,startPosition);

        // Find the position of the king of the current team
        TeamColor currentTeam = myPiece.getTeamColor();

        // Create a new list to put all the valid moves into
        Collection<ChessMove> validMoves = new HashSet<>();

        for (ChessMove move : potentialMoves) {
            // Temporarily make the move
            ChessPiece originalPiece = chessBoard.getPiece(move.getEndPosition());
            chessBoard.addPiece(move.getEndPosition(), myPiece);
            chessBoard.addPiece(move.getStartPosition(), null);

            // Check if the king is still in check after the move
            if (!isInCheck(currentTeam)) {
                validMoves.add(move); // Move is valid if it doesn't leave the king in check
            }
            // Undo the move
            chessBoard.addPiece(move.getStartPosition(), myPiece);
            chessBoard.addPiece(move.getEndPosition(), originalPiece);
        }
        return validMoves;
    }

    // Receives a given move and executes it, provided it is a legal move.
    // If the move is illegal, it throws an InvalidMoveException.
    // A move is illegal if the chess piece cannot move there,
    // if the move leaves the team’s king in danger, or if it’s not the corresponding teams turn.
    // this makeMove function needs to care about which team's turn it is in order for the move to be valid or not
    @Override
    public void makeMove(ChessMove move) throws InvalidMoveException {
        // get the piece that will make the move
        ChessPiece myPiece = chessBoard.getPiece(move.getStartPosition());
        ChessPiece promoPiece = new MyPawn(myPiece.getTeamColor());

        // get the promotion piece for this move
        ChessPiece.PieceType promotion = move.getPromotionPiece();
        if (promotion == ChessPiece.PieceType.QUEEN) {
            promoPiece = new MyQueen(myPiece.getTeamColor());
        }
        if (promotion == ChessPiece.PieceType.ROOK) {
            promoPiece = new MyRook(myPiece.getTeamColor());
        }
        if (promotion == ChessPiece.PieceType.BISHOP) {
            promoPiece = new MyBishop(myPiece.getTeamColor());
        }
        if (promotion == ChessPiece.PieceType.KNIGHT) {
            promoPiece = new MyKnight(myPiece.getTeamColor());
        }

        if (myPiece.getTeamColor() != teamTurn) { throw new InvalidMoveException("Wrong turn"); }

        // Calculate valid moves for the piece
        Collection<ChessMove> validMoves = myPiece.pieceMoves(chessBoard, move.getStartPosition());

        // Check if the given move is in the list of valid moves
        if (!validMoves.contains(move)) {
            throw new InvalidMoveException("Invalid move");
        }

        // Make the move on the chessboard
        chessBoard.addPiece(move.getEndPosition(), myPiece);
        chessBoard.addPiece(move.getStartPosition(), null);

        // Check if the move puts the moving team's king in check
        if (isInCheck(teamTurn)) {
            // Undo the move
            chessBoard.addPiece(move.getStartPosition(), myPiece);
            chessBoard.addPiece(move.getEndPosition(), null);
            throw new InvalidMoveException("Move puts king in check");
        }

        // Check for pawn promotion
        if (myPiece.getPieceType() == ChessPiece.PieceType.PAWN && isPromotionRow(move.getEndPosition())) {
            // Replace the promoted pawn with the selected promotion piece
            chessBoard.addPiece(move.getEndPosition(), promoPiece);
        }

        // Switch the team's turn
        teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    // helper function for makeMove
    // Check if the given position is in the promotion row for the current team
    private boolean isPromotionRow(ChessPosition position) {
        // Assuming white pawns promote on row 8 and black pawns promote on row 1
        return (teamTurn == TeamColor.WHITE && position.getRow() == 8)
                || (teamTurn == TeamColor.BLACK && position.getRow() == 1);
    }


    // Helper function to find the position of the king of the specified team
    private ChessPosition findKingPosition(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currentPosition = new MyPosition(row, col);
                ChessPiece piece = chessBoard.getPiece(currentPosition);

                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING &&
                        piece.getTeamColor() == teamColor) {
                    return currentPosition; // Found the king
                }
            }
        }
        return null; // King not found
    }

    // Determines if the given team is in check
    // Returns true if the specified team’s King could be captured by an opposing piece.
    @Override
    public boolean isInCheck(TeamColor teamColor) {
        // Find the position of the king of the specified team
        ChessPosition kingPosition = findKingPosition(teamColor);

        // Iterate through the pieces of the opposing team
        TeamColor opposingTeam = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currentPosition = new MyPosition(row, col);
                ChessPiece piece = chessBoard.getPiece(currentPosition);

                if (piece != null && piece.getTeamColor() == opposingTeam) {
                    // Calculate valid moves for the opposing piece
                    Collection<ChessMove> validMoves = piece.pieceMoves(chessBoard, currentPosition);

                    // Check if any valid move of the opposing piece captures the king
                    for (ChessMove move : validMoves) {
                        if (move.getEndPosition().equals(kingPosition)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false; // King is not in check
    }

    // Determines if the given team is in checkmate
    // Returns true if the given team has no way to protect their king from being captured.
    @Override
    public boolean isInCheckmate(TeamColor teamColor) {
        // first verify that the team is in check before seeing if it's checkmate
        if (!isInCheck(teamColor)) { return false; }

        // Find the position of the king of the specified team
        ChessPosition kingPosition = findKingPosition(teamColor);

        if (kingPosition == null) {
            // Handle the case where the king is not found (possibly indicating an error)
            return false;
        }
        // Iterate through all the pieces of the team that is in check
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currentPosition = new MyPosition(row, col);
                ChessPiece piece = chessBoard.getPiece(currentPosition);

                if (piece != null && piece.getTeamColor() == teamColor) {
                    // Calculate valid moves for the team's piece
                    Collection<ChessMove> validMoves = piece.pieceMoves(chessBoard, currentPosition);

                    // Check if any valid move of the team's piece can get the king out of check
                    for (ChessMove move : validMoves) {
                        // Temporarily make the move
                        ChessPiece originalPiece = chessBoard.getPiece(move.getEndPosition());
                        chessBoard.addPiece(move.getEndPosition(), piece);
                        chessBoard.addPiece(move.getStartPosition(), null);

                        // Check if the king is still in check after the move
                        boolean stillInCheck = isInCheck(teamColor);

                        // Undo the move
                        chessBoard.addPiece(move.getStartPosition(), piece);
                        chessBoard.addPiece(move.getEndPosition(), originalPiece);

                        if (!stillInCheck) {
                            return false; // There's at least one move that gets the king out of check
                        }
                    }
                }
            }
        }
        return true; // No legal moves can get the king out of check, it's checkmate
    }


    // Determines if the given team is in stalemate, which here is defined as having no valid moves
    // Returns true if the given team has no legal moves, and it is currently that team’s turn.
    @Override
    public boolean isInStalemate(TeamColor teamColor) {
        if (teamColor != teamTurn) { return false; }
        // iterate through every piece of the given team and check if there's any valid moves
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currentPosition = new MyPosition(row, col);
                ChessPiece piece = chessBoard.getPiece(currentPosition);

                if (piece != null && piece.getTeamColor() == teamColor) {
                    // Calculate valid moves for the team's piece
                    Collection<ChessMove> validMoves = validMoves(currentPosition);

                    // If there are any valid moves for the team, it's not in stalemate
                    if (!validMoves.isEmpty()) {
                        return false; // There's at least one valid move
                    }
                }
            }
        }
        return true;
    }

    // Sets this game's chessboard with a given board
    @Override
    public void setBoard(ChessBoard board) {
        chessBoard = board;
    }

    // Gets the current chessboard
    @Override
    public ChessBoard getBoard() {
        return chessBoard;
    }
}
