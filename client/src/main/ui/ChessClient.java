package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import models.AuthToken;
import models.Game;

import java.util.*;

public class ChessClient {
    private final Scanner scanner;
    private State currentState;
    static private AuthToken authToken;
    private Game currentGame;
    static public boolean running;
    static private int gameID;
    private final PreLoginFacade preLoginFacade;
    private final PostLoginFacade postLoginFacade;
    private final Map<Integer, Integer> gameNumberToIDMap = new HashMap<>();
    private final Map<Integer, String> gameIDToNameMap = new HashMap<>();

    public ChessClient(Scanner scanner) {
        this.scanner = scanner;
        this.currentState = State.LOGGED_OUT; // Starting state is logged out
        this.preLoginFacade = new PreLoginFacade("http://localhost:8080");
        this.postLoginFacade = new PostLoginFacade("http://localhost:8080");
        this.currentGame = new Game(0,"","","", null);
        authToken = null;
        gameID = 0;
        running = true;
    }

    public void run() {
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


    /** All preLogin commands, help menus and helper functions are below */
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
                String gameName = gameIDToNameMap.get(gameID);
                System.out.println("Joined game '" + gameName + "' as " + color);

                // Now draw the board twice, once with selected color on bottom and once with opposite
                if (Objects.equals(color, "WHITE")) {
                    drawChessBoard(currentGame.game(), true);   // white on bottom
                    System.out.println();                                    // Add some spacing
                    drawChessBoard(currentGame.game(), false);  // black on bottom
                } else if (Objects.equals(color, "BLACK")) {
                    drawChessBoard(currentGame.game(), false);  // black on bottom
                    System.out.println();                                    // Add some spacing
                    drawChessBoard(currentGame.game(), true);   // white on bottom
                }

            } else {
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

                // Now draw the board twice
                drawChessBoard(currentGame.game(), true);  // White at bottom
                System.out.println(); // Add some spacing
                drawChessBoard(currentGame.game(), false); // Black at bottom

            } else {
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
                String pieceChar = convertPieceToChar(piece);

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

    private String convertPieceToChar(ChessPiece piece) {
        if (piece == null) {
            return EscapeSequences.EMPTY; // Use your constant for an empty square
        }

        String pieceChar;
        switch (piece.getPieceType()) {
            case KING:
                pieceChar = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
                break;
            case QUEEN:
                pieceChar = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
                break;
            case BISHOP:
                pieceChar = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
                break;
            case KNIGHT:
                pieceChar = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
                break;
            case ROOK:
                pieceChar = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
                break;
            case PAWN:
                pieceChar = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
                break;
            default:
                pieceChar = "?";
                break;
        }

        // Add color to the piece character
        String color = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.SET_TEXT_COLOR_WHITE : EscapeSequences.SET_TEXT_COLOR_BLACK;
        return color + pieceChar + EscapeSequences.RESET_TEXT_COLOR;
    }


    /** All gameplay commands, help menus and helper functions are below */
    private void handleGameplayCommands() {
        System.out.print("Enter gameplay command: ");
        String inputLine = scanner.nextLine();
        String[] inputParts = inputLine.split("\\s+"); // Splitting input by spaces
        String command = inputParts[0].toLowerCase();

        switch (command) {
            case "help":
                printGameplayHelp();
                break;
            case "redraw":
                redrawBoard();
                break;
            case "leave":
                leaveGame();
                break;
            case "promotion":
                if (inputParts.length == 3) {
                    handlePromotion(inputParts[1], inputParts[2]);
                } else {
                    System.out.println("Invalid command. Usage: promotion <crcr> [q|r|b|n]");
                }
                break;
            case "move":
                if (inputParts.length == 2) {
                    makeMove(inputParts[1]);
                } else {
                    System.out.println("Invalid command. Usage: move <crcr>");
                }
                break;
            case "quit":
                running = false;
                System.out.println("Exiting the chess client. Goodbye!");
                System.exit(0);
                break;
        }
    }

    private void printGameplayHelp() {
        System.out.println("Available gameplay commands:");
        System.out.println("  redraw - Redraw the chessboard");
        System.out.println("  leave - Leave the current game");
        System.out.println("  promotion <crcr> [q|r|b|n] - Promote a pawn (e.g., promotion e7e8 q)");
        System.out.println("  move <crcr> - Make a regular move (e.g., move e2e4)");
        System.out.println("  quit - Exit the chess client");
        System.out.println("  help - Show this help menu");
    }

    private void redrawBoard() {

    }

    private void leaveGame() {

    }

    private void handlePromotion(String inputPart, String inputPart1) {

    }

    private void makeMove(String inputPart) {

    }


    /** All observer commands, help menus and helper functions are below */
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
                showLegalMoves();
                break;
            case "redraw":
                redrawBoard();
                break;
            case "leave":
                leaveObservation();
                break;
            case "quit":
                running = false;
                System.out.println("Exiting the chess client. Goodbye!");
                System.exit(0);
                break;
        }
    }
    private void printObserverHelp() {
        System.out.println("Available observer commands:");
        System.out.println("  legal - Show legal moves for the current game");
        System.out.println("  redraw - Redraw the chessboard");
        System.out.println("  leave - Leave observing the current game");
        System.out.println("  quit - Exit the chess client");
        System.out.println("  help - Show this help menu");
    }

    private void showLegalMoves() {
    }

    private void leaveObservation() {

    }

}