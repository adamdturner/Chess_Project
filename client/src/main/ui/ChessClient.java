package ui;

import chess.*;
import models.AuthToken;
import models.Game;
import webSocketMessages.userCommands.*;

import java.util.*;

public class ChessClient {
    private final Scanner scanner;
    private State currentState;
    private static AuthToken authToken;
    private static Game currentGame;
    private static int gameID;
    public static boolean running;
    private final PreLoginFacade preLoginFacade;
    private final PostLoginFacade postLoginFacade;
    private final Map<Integer, Integer> gameNumberToIDMap = new HashMap<>();
    private final Map<Integer, String> gameIDToNameMap = new HashMap<>();
    private final WebSocketClient webSocketClient;

    public ChessClient(Scanner scanner) {
        currentGame = new Game(0,"","","", null);
        authToken = new AuthToken("","");
        gameID = 0;
        running = true;
        this.scanner = scanner;
        this.currentState = State.LOGGED_OUT; // Starting state is logged out
        this.preLoginFacade = new PreLoginFacade("http://localhost:8080");
        this.postLoginFacade = new PostLoginFacade("http://localhost:8080");
        // Initialize the WebSocketClient and connect to the server
        this.webSocketClient = new WebSocketClient("ws://localhost:8080/connect", this);
    }

    public void run() throws InvalidMoveException {
        // static variable is initially set to true, allowing it to loop continuously until quit command is given
        while (running) {
            switch (currentState) {
                case LOGGED_OUT:
                    handlePreLoginCommands();
                    break;
                case LOGGED_IN:
                    handlePostLoginCommands();
                    break;
                case BLACK, WHITE:
                    handleGameplayCommands();
                    break;
                case OBSERVE:
                    handleObserverCommands();
                    break;
            }
        }
    }

    /** All preLogin commands, help menu and helper functions are below */
    private void handlePreLoginCommands() {
        System.out.print("Enter command: ");
        String inputLine = scanner.nextLine();
        String[] inputParts = inputLine.split("\\s+"); // Splitting input by spaces
        String command = inputParts[0].toLowerCase();

        switch (command) {
            case "help":
                printPreLoginHelp();
                break;
            case "login":
                if (inputParts.length == 3) { // Expecting 2 parameters for login
                    login(inputParts[1], inputParts[2]);
                } else {
                    System.out.println("Invalid login command. Usage: login <USERNAME> <PASSWORD>");
                }
                break;
            case "register":
                if (inputParts.length == 4) { // Expecting 3 parameters for register
                    register(inputParts[1], inputParts[2], inputParts[3]);
                } else {
                    System.out.println("Invalid register command. Usage: register <USERNAME> <PASSWORD> <EMAIL>");
                }
                break;
            case "quit":
                running = false;
                System.out.println("Exiting the chess client. Goodbye!");
                System.exit(0);
                break;
            default:
                System.out.println("Unknown command. Type 'help' for a list of commands.");
                break;
        }
    }

    private void printPreLoginHelp() {
        System.out.println("Available commands:");
        System.out.println("  help - Show this help menu");
        System.out.println("  login <USERNAME> <PASSWORD> - Log in to your account to play chess");
        System.out.println("  register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
        System.out.println("  quit - Exit the chess client");
    }

    private void login(String username, String password) {
        AuthToken newToken = preLoginFacade.login(username, password);
        if (newToken != null) {
            authToken = newToken;
            System.out.println("Login successful!");
            currentState = State.LOGGED_IN;
        } else {
            System.out.println("Login failed. Please check your username and password.");
        }
    }

    private void register(String username, String password, String email) {
        AuthToken newToken = preLoginFacade.register(username, password, email);
        if (newToken != null) {
            authToken = newToken;
            System.out.println("Registration successful!");
            currentState = State.LOGGED_IN;
        } else {
            System.out.println("Registration failed. Please try a different username.");
        }
    }

    /** All postLogin commands, help menus and helper functions are below */
    private void handlePostLoginCommands() {
        System.out.print("Enter command: ");
        String inputLine = scanner.nextLine();
        String[] inputParts = inputLine.split("\\s+"); // Splitting input by spaces
        String command = inputParts[0].toLowerCase();

        switch (command) {
            case "help":
                printPostLoginHelp();
                break;
            case "logout":
                logout();
                break;
            case "create":
                if (inputParts.length >= 2) {
                    String gameName = String.join(" ", Arrays.copyOfRange(inputParts, 1, inputParts.length));
                    createGame(gameName);
                } else {
                    System.out.println("Invalid command. Usage: create <GAME NAME>");
                }
                break;
            case "list":
                listGames();
                break;
            case "join":
                if (inputParts.length == 3) {
                    String gameId = inputParts[1];
                    String color = inputParts[2].toUpperCase();

                    if (color.equals("WHITE") || color.equals("BLACK")) {
                        joinGame(gameId, color);
                    } else {
                        System.out.println("Invalid color. Please choose 'WHITE' or 'BLACK'.");
                    }
                } else {
                    System.out.println("Invalid command. Usage: join <GAME ID> [WHITE|BLACK]");
                }
                break;
            case "observe":
                if (inputParts.length == 2) {
                    joinObserver(inputParts[1]);
                } else {
                    System.out.println("Invalid command. Usage: observe <GAME ID>");
                }
                break;
            default:
                System.out.println("Unknown command. Type 'help' for a list of commands.");
                break;
        }
    }

    private void printPostLoginHelp() {
        System.out.println("Available commands:");
        System.out.println("  help - Show this help menu");
        System.out.println("  logout - Log out of your account");
        System.out.println("  create <name> - Create a new chess game with the name you give");
        System.out.println("  list - List all available games to join");
        System.out.println("  join <ID> [WHITE|BLACK] - Join an existing chess game as WHITE or BLACK");
        System.out.println("  observe <ID> - Join as an observer to an existing game");
    }

    private void logout() {
        if (postLoginFacade.logout(authToken)) {
            System.out.println("Logout successful.");
            currentState = State.LOGGED_OUT;
        } else {
            System.out.println("Logout failed.");
        }
    }

    private void createGame(String name) {
        int ID = postLoginFacade.createGame(authToken, name);
        gameID = ID;
        if (ID != 0) {
            System.out.println("Game '" + name + "' created successfully.");
        } else {
            System.out.println("Failed to create game.");
        }
    }

    private void listGames() {
        List<Game> games = postLoginFacade.listGames(authToken);
        gameNumberToIDMap.clear();
        gameIDToNameMap.clear();

        if (games != null && !games.isEmpty()) {
            System.out.println("Available games:");
            int number = 1;
            for (Game game : games) {
                System.out.println(number + ": Name: " + game.gameName() + ", Players: " + game.whiteUsername() + ", " + game.blackUsername());
                gameNumberToIDMap.put(number, game.gameID());
                gameIDToNameMap.put(game.gameID(), game.gameName());
                number++;
            }
        } else {
            System.out.println("No available games or failed to retrieve games list.");
        }
    }

    private void joinGame(String gameNumber, String color) {
        try {
            int number = Integer.parseInt(gameNumber);
            Integer gameID = gameNumberToIDMap.get(number);
            if (gameID == null) {
                System.out.println("Invalid game number.");
                return;
            }
            currentGame = postLoginFacade.joinGame(authToken, gameID, color);
            if (currentGame != null) {

                ChessGame.TeamColor playerColor = color.equals("WHITE") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
                JoinPlayerCommand command = new JoinPlayerCommand(authToken.authToken(), currentGame.gameID(), playerColor);
                command.setToken(authToken);
                this.webSocketClient.sendCommand(command);

                String gameName = gameIDToNameMap.get(gameID);
                System.out.println("Joined game '" + gameName + "' as " + color);

                if (Objects.equals(color, "WHITE")) {
                    currentState = State.WHITE;                                 // sets the clients state to White Player
                } else if (Objects.equals(color, "BLACK")) {
                    currentState = State.BLACK;                                 // sets the clients state to Black Player
                }
                // Now draw the board twice, once with selected color on bottom and once with opposite
                load(currentGame);
            } else {
                currentState = State.LOGGED_IN;
                System.out.println("Failed to join game.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid game number.");
        }
    }

    private void joinObserver(String gameNumber) {
        try {
            int number = Integer.parseInt(gameNumber);
            Integer gameID = gameNumberToIDMap.get(number);
            if (gameID == null) {
                System.out.println("Invalid game number.");
                return;
            }
            currentGame = postLoginFacade.joinObserver(authToken, gameID);
            if (currentGame != null) {
                String gameName = gameIDToNameMap.get(gameID);
                System.out.println("Joined game '" + gameName + "' as an observer.");

                JoinObserverCommand command = new JoinObserverCommand(authToken.authToken(), currentGame);
                command.setToken(authToken);
                this.webSocketClient.sendCommand(command);

                // Now draw the board twice fixme this may be deleted because the LoadGameMessage coming in should cause the board to be redrawn
                load(currentGame);                                          // prints board with WHITE orientation
                currentState = State.OBSERVE;                               // sets the clients state to Observer
            } else {
                currentState = State.LOGGED_IN;
                System.out.println("Failed to join game as an observer.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid game number.");
        }
    }

    public void drawChessBoard(ChessGame chessGame, boolean whiteAtBottom) {
        ChessBoard boardObject = chessGame.getBoard();
        ChessPiece[][] board = boardObject.getBoard();

        // Print the column labels
        printColumnLabels(whiteAtBottom);

        // Print the board rows
        for (int row = 0; row < board.length; row++) {
            // Calculate the actual row index based on the orientation
            int actualRow = whiteAtBottom ? 7 - row : row;

            // Print the row number at the start
            System.out.print("\u001b[0m" + (actualRow + 1) + " ");

            for (int col = 0; col < board[row].length; col++) {
                // Determine the background color for the current square
                String bgColor = ((actualRow + col) % 2 == 0) ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.SET_BG_COLOR_DARK_GREY;

                // Get the piece at the current position
                ChessPiece piece = board[actualRow][col];

                // Use convertPieceToChar to get the piece character with color
                String pieceChar = convertPieceToSymbol(piece);

                // Print the square with the piece character and background color
                System.out.print(bgColor + pieceChar + "\u001b[0m");
            }
            // Print the row number at the end and reset the line's styles
            System.out.println("\u001b[0m" + " " + (actualRow + 1));
        }
        // Reset the terminal to default and print the column labels again
        System.out.print("\u001b[0m");
        printColumnLabels(whiteAtBottom);
        System.out.println("\u001b[0m"); // Reset after printing the board
    }

    private void printColumnLabels(boolean whiteAtBottom) {
        System.out.print("\u001b[0m"); // Reset before printing column labels
        System.out.print("  ");
        for (int col = 0; col < 8; col++) {
            char label = (char) ('a' + (whiteAtBottom ? col : 7 - col));
            System.out.print(" " + label + " ");
        }
        System.out.println("\u001b[0m"); // Reset after printing column labels
    }

    private String convertPieceToSymbol(ChessPiece piece) {
        if (piece == null) {
            return EscapeSequences.EMPTY; // Use your constant for an empty square
        }
        String pieceSymbol = switch (piece.getPieceType()) {
            case KING ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
            case QUEEN ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case BISHOP ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case KNIGHT ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case ROOK ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            case PAWN ->
                    piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
            default -> "?";
        };
        // Add color to the piece character
        String color = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.SET_TEXT_COLOR_WHITE : EscapeSequences.SET_TEXT_COLOR_BLACK;
        return color + pieceSymbol + EscapeSequences.RESET_TEXT_COLOR;
    }

    /** All gameplay commands, help menu and helper functions are below */
    private void handleGameplayCommands() throws InvalidMoveException {
        System.out.print("Enter gameplay command: ");
        String inputLine = scanner.nextLine();
        String[] inputParts = inputLine.split("\\s+"); // Splitting input by spaces
        String command = inputParts[0].toLowerCase();

        switch (command) {
            case "help":
                printGameplayHelp();
                break;
            case "redraw":
                load(currentGame);
                break;
            case "leave":
                leaveGame();
                break;
            case "legal":
                if (inputParts.length == 2) {
                    showLegalMoves(inputParts[1].toLowerCase());
                } else {
                    System.out.println("Invalid command. Usage: legal <cr> (e.g., move e2)");
                }
                break;
            case "resign":
                resign();
                break;
            case "promotion":
                if (inputParts.length == 3) {
                    makeMove(inputParts[1].toLowerCase(), inputParts[2].toLowerCase());
                } else {
                    System.out.println("Invalid command. Usage: promotion <crcr> [q|r|b|n]");
                }
                break;
            case "move":
                if (inputParts.length == 2 && isValidChessMove(inputParts[1])) {
                    makeMove(inputParts[1], null);
                } else {
                    System.out.println("Invalid command. Usage: move <crcr> (e.g., move e2e4)");
                }
                break;
            case "quit":
                running = false;
                System.out.println("Exiting the chess client. Goodbye!");
                System.exit(0);
                break;
            default:
                System.out.println("Unknown command. Type 'help' for a list of commands.");
                break;
        }
    }

    private boolean isValidChessMove(String move) {
        if (move.length() != 4) { return false; }
        return move.charAt(0) >= 'a' && move.charAt(0) <= 'h' &&
                move.charAt(2) >= 'a' && move.charAt(2) <= 'h' &&
                move.charAt(1) >= '1' && move.charAt(1) <= '8' &&
                move.charAt(3) >= '1' && move.charAt(3) <= '8';
    }

    private void printGameplayHelp() {
        System.out.println("Available gameplay commands:");
        System.out.println("  redraw - Redraw the chessboard");
        System.out.println("  leave - Leave the current game");
        System.out.println("  legal <cr> - Highlight the legal moves from given position (e.g., legal e2)");
        System.out.println("  resign - Resign from the current game");
        System.out.println("  promotion <crcr> [q|r|b|n] - Promote a pawn (e.g., promotion e7e8 q)");
        System.out.println("  move <crcr> - Make a regular move (e.g., move e2e4)");
        System.out.println("  quit - Exit the chess client");
        System.out.println("  help - Show this help menu");
    }

    private void redrawBoard(Game game, String color) {
        if (Objects.equals(color, "WHITE")) {
            drawChessBoard(game.game(), true);   // white on bottom
            System.out.println();                                    // Add some spacing
            drawChessBoard(game.game(), false);  // black on bottom
        } else if (Objects.equals(color, "BLACK")) {
            drawChessBoard(game.game(), false);  // black on bottom
            System.out.println();                                    // Add some spacing
            drawChessBoard(game.game(), true);   // white on bottom
        }
    }

    public void load(Game game) {
        if (currentState == State.BLACK) {
            redrawBoard(game,"BLACK");
        } else {
            if (currentState == State.WHITE) {
                redrawBoard(game,"WHITE");
            } else {
                drawChessBoard(game.game(),true);
            }
        }
        checkGameState();
    }

    private void leaveGame() {
        UserGameCommand command = new LeaveGameCommand(authToken.authToken(), currentGame.gameID());
        command.setCommandType(UserGameCommand.CommandType.LEAVE);
        command.setToken(authToken);
        this.webSocketClient.sendCommand(command);

        // set the currentGame to null when returning to logged in menu because client is no longer in a current game
        currentGame = null;
        currentState = State.LOGGED_IN;
        System.out.println("You have left the game.");
    }

    private void showLegalMoves(String startPosition) {
        // Validate the input for position (should be a two-character string like "e2")
        if (startPosition.length() != 2 || !isValidChessPosition(startPosition)) {
            System.out.println("Invalid position format. Usage: legal <position> (e.g., legal e2)");
            return;
        }
        // Convert the algebraic notation to row and column indices
        int col = startPosition.charAt(0) - 'a' + 1;
        int row = Integer.parseInt(startPosition.substring(1));

        // Create a position object
        ChessPosition position = new MyPosition(row, col);

        // Get the valid moves from the current position
        Collection<ChessMove> validMoves = currentGame.game().validMoves(position);

        // Determine the orientation of the board for the current player
        boolean whiteAtBottom = currentState == State.WHITE;

        // Print the board with valid moves highlighted
        printBoardWithLegalMoves(currentGame.game(), validMoves, whiteAtBottom);
    }

    private boolean isValidChessPosition(String position) {
        return position.charAt(0) >= 'a' && position.charAt(0) <= 'h' &&
                position.charAt(1) >= '1' && position.charAt(1) <= '8';
    }

    private void printBoardWithLegalMoves(ChessGame chessGame, Collection<ChessMove> validMoves, boolean whiteAtBottom) {
        ChessBoard boardObject = chessGame.getBoard();
        ChessPiece[][] board = boardObject.getBoard();

        // Print the column labels
        printColumnLabels(whiteAtBottom);

        // Print the board rows with highlighted legal moves
        for (int row = 0; row < board.length; row++) {
            // Calculate the actual row index based on the orientation
            int actualRow = whiteAtBottom ? 7 - row : row;

            // Print the row number at the start
            System.out.print("\u001b[0m" + (actualRow + 1) + " ");

            for (int col = 0; col < board[row].length; col++) {
                // Determine if the current position is a valid move
                int actualCol = col;
                boolean isLegalMove = validMoves.stream().anyMatch(move ->
                        move.getEndPosition().getRow() == actualRow + 1 &&
                                move.getEndPosition().getColumn() == actualCol + 1 // Adjusting for 1-based index
                );

                // Determine the background color for the current square
                String bgColor = isLegalMove ? EscapeSequences.SET_BG_COLOR_GREEN :
                        ((actualRow + col) % 2 == 0) ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.SET_BG_COLOR_DARK_GREY;

                // Get the piece at the current position
                ChessPiece piece = board[actualRow][col];

                // Use convertPieceToSymbol to get the piece character with color
                String pieceChar = convertPieceToSymbol(piece);

                // Print the square with the piece character and background color
                System.out.print(bgColor + pieceChar + EscapeSequences.RESET_BG_COLOR);
            }

            // Print the row number at the end and reset the line's styles
            System.out.println("\u001b[0m" + " " + (actualRow + 1));
        }
        // Reset the terminal to default and print the column labels again
        System.out.print("\u001b[0m");
        printColumnLabels(whiteAtBottom);
        System.out.println("\u001b[0m"); // Reset after printing the board
    }

    private void resign() {
        UserGameCommand command = new LeaveGameCommand(authToken.authToken(), currentGame.gameID());
        command.setCommandType(UserGameCommand.CommandType.RESIGN);
        command.setToken(authToken);
        this.webSocketClient.sendCommand(command);
        currentState = State.LOGGED_IN;
        System.out.println("You have resigned from the game.");
    }

    private void makeMove(String moveString, String promotion) throws InvalidMoveException {
        ChessMove move = parseMoveString(moveString, promotion);
        if (move != null) {
            MoveCommand command = new MoveCommand(authToken.authToken(), move, currentGame.gameID());
            command.setToken(authToken);
            this.webSocketClient.sendCommand(command);

            // make the move on the current game and check the game state to see if there is check, checkmate or stalemate
            currentGame.game().makeMove(move);

            // print the board after the move has been made, according to the currentState of the client (not the game)
            // this also checks the game state
            load(currentGame);
        } else {
            System.out.println("Invalid move format.");
        }
    }

    private ChessMove parseMoveString(String moveString, String promotion) {
        if (!isValidChessMove(moveString)) {
            return null;
        }
        // Convert columns from characters to integers (e.g., 'a' -> 1, 'b' -> 2, ..., 'h' -> 8)
        int col1 = moveString.charAt(0) - 'a' + 1;
        int col2 = moveString.charAt(2) - 'a' + 1;

        // Convert rows from characters to integers
        int row1 = Character.getNumericValue(moveString.charAt(1));
        int row2 = Character.getNumericValue(moveString.charAt(3));

        ChessPosition startPosition = new MyPosition(row1, col1);
        ChessPosition endPosition = new MyPosition(row2, col2);

        if (promotion == null) {
            return new MyMove(startPosition, endPosition, null);
        } else {
            ChessPiece.PieceType promotionPiece = switch (promotion) {
                case "q" -> ChessPiece.PieceType.QUEEN;
                case "b" -> ChessPiece.PieceType.BISHOP;
                case "r" -> ChessPiece.PieceType.ROOK;
                case "n" -> ChessPiece.PieceType.KNIGHT;
                default -> null;
            };
            return new MyMove(startPosition, endPosition, promotionPiece);
        }
    }

    private void checkGameState() {
        ChessGame.TeamColor teamTurn = currentGame.game().getTeamTurn();
        ChessGame.TeamColor opposingTeam = teamTurn == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;

        boolean isCheck = currentGame.game().isInCheck(teamTurn);
        boolean isCheckmate = currentGame.game().isInCheckmate(teamTurn);
        boolean isStalemate = currentGame.game().isInStalemate(teamTurn);

        String teamTurnName = teamTurn == ChessGame.TeamColor.WHITE ? "White" : "Black";
        String opposingTeamName = opposingTeam == ChessGame.TeamColor.WHITE ? "White" : "Black";

        if (isCheckmate) {
            System.out.println("Team " + teamTurnName + " is in checkmate. Team " + opposingTeamName + " has won the game.");
            Game.GameState state = teamTurn == ChessGame.TeamColor.WHITE ? Game.GameState.BLACK : Game.GameState.WHITE;
            currentGame.setState(state);
        } else if (isCheck) {
            System.out.println("Team " + teamTurnName + " is in check. They must get out of check.");
        } else if (isStalemate) {
            System.out.println("Team " + teamTurnName + " is in stalemate. No legal moves to make, and the King is not in check. Game is a draw.");
            currentGame.setState(Game.GameState.DRAW);
        } else {
            System.out.println("It is " + teamTurnName + "'s turn to move. No one is in check or stalemate");
        }
    }

    /** All observer commands, help menu and helper functions are below */
    private void handleObserverCommands() {
        System.out.print("Enter observer command: ");
        String inputLine = scanner.nextLine();
        String[] inputParts = inputLine.split("\\s+");
        String command = inputParts[0].toLowerCase();

        switch (command) {
            case "help":
                printObserverHelp();
                break;
            case "legal":
                if (inputParts.length == 2) {
                    showLegalMoves(inputParts[1].toLowerCase());
                } else {
                    System.out.println("Invalid command. Usage: legal <cr> (e.g., move e2)");
                }
                break;
            case "redraw":
                load(currentGame);
                break;
            case "leave":
                leaveObservation();
                break;
            case "quit":
                running = false;
                System.out.println("Exiting the chess client. Goodbye!");
                System.exit(0);
                break;
            default:
                System.out.println("Unknown command. Type 'help' for a list of commands.");
                break;
        }
    }
    private void printObserverHelp() {
        System.out.println("Available observer commands:");
        System.out.println("  legal <cr> - Show legal moves for a given position (e.g., move e2)");
        System.out.println("  redraw - Redraw the chessboard");
        System.out.println("  leave - Leave observing the current game");
        System.out.println("  quit - Exit the chess client");
        System.out.println("  help - Show this help menu");
    }

    private void leaveObservation() {
        UserGameCommand command = new LeaveGameCommand(authToken.authToken(), currentGame.gameID());
        command.setCommandType(UserGameCommand.CommandType.LEAVE);

        command.setToken(authToken);
        this.webSocketClient.sendCommand(command);

        // set the currentGame to null when returning to logged in menu because client is no longer in a current game
        currentGame = null;
        currentState = State.LOGGED_IN;
        System.out.println("You have left the game.");
    }

    // Method to print incoming server messages
    public void onServerMessage(String message) {
        System.out.println(message);
    }
}