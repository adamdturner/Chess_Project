package handlers;

import chess.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.DAOInterface;
import dataAccess.DataAccessException;
import models.AuthToken;
import models.Game;
import models.User;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.userCommands.*;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArraySet;


@WebSocket
public class WebSocketHandler {

    DAOInterface database;

    // Thread-safe set for storing sessions
    private static final CopyOnWriteArraySet<Session> sessions = new CopyOnWriteArraySet<>();
    public WebSocketHandler(DAOInterface database) {
        this.database = database;
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        sessions.add(session);
        System.out.println("Connection opened");
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        sessions.remove(session);
        System.out.println("Connection closed: " + reason);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws DataAccessException, InvalidMoveException {
        if (session != null) {
            // Deserialize the JSON message to UserGameCommand or its specific subclass
            UserGameCommand command = deserializeFromJson(message, UserGameCommand.class);
            if (command.getAuthString() == null) {
                sendErrorMessage(session,"Error: Null AuthToken in UserGameCommand");
            }
            AuthToken token = database.getAuthToken(command.getAuthString());
            if (token != null) {
                User user = database.getUser(token.username());
                if (user != null) {
                    // Process the command based on its type
                    switch (command.getCommandType()) {
                        case JOIN_PLAYER:
                            JoinPlayerCommand joinPlayerCommand = deserializeFromJson(message, JoinPlayerCommand.class);
                            handleJoinPlayerCommand(session, joinPlayerCommand, user);
                            break;
                        case JOIN_OBSERVER:
                            JoinObserverCommand joinObserverCommand = deserializeFromJson(message, JoinObserverCommand.class);
                            handleJoinObserverCommand(session, joinObserverCommand, user);
                            break;
                        case MAKE_MOVE:
                            MoveCommand moveCommand = deserializeFromJson(message, MoveCommand.class);
                            handleMakeMoveCommand(session, moveCommand, user);
                            break;
                        case LEAVE:
                            LeaveGameCommand leaveGameCommand = deserializeFromJson(message, LeaveGameCommand.class);
                            handleLeaveCommand(session, leaveGameCommand, user);
                            break;
                        case RESIGN:
                            ResignGameCommand resignGameCommand = deserializeFromJson(message, ResignGameCommand.class);
                            handleResignCommand(session, resignGameCommand, user);
                            break;
                        default:
                            // someone sent in a command with just the authToken and no commandType
                            sendErrorMessage(session, "Error: Command sent with no commandType");
                            break;
                    }
                } else {
                    sendErrorMessage(session, "Error: invalid user, database.getUser() was null");
                }
            } else {
                sendErrorMessage(session, "Error: invalid token, database.getAuthToken() was null");
            }
        }
    }


    /** Methods for handling UserGameCommands below */

    private void handleJoinPlayerCommand(Session session, JoinPlayerCommand command, User user) throws DataAccessException {
        int gameId = command.getGameID();
        Game game = database.findGame(gameId);
        if (game != null) {
            String username = "";
            username = (command.playerColor == ChessGame.TeamColor.BLACK) ? game.blackUsername() : game.whiteUsername();
            if (Objects.equals(user.username(), username) || (!Objects.equals(username, null) && username.isEmpty() || Objects.equals(username, ""))) {
                // Send LOAD_GAME message back to the root client
                sendLoadGameMessage(session, game);

                // Send NOTIFICATION message to all other clients
                NotificationMessage notificationMessage = new NotificationMessage(username + " joined game as " + command.playerColor);
                broadcastMessage(serializeToJson(notificationMessage),session);
            } else {
                sendErrorMessage(session, "Error: another user is already in game, usernames don't match");
            }
        } else {
            // Game not found, send an error message back to root client
            sendErrorMessage(session, "Error: Game with ID " + gameId + " does not exist.");
        }
    }

    public void handleJoinObserverCommand(Session session, JoinObserverCommand command, User user) throws DataAccessException {
        int gameId = command.getGameID();
        Game game = database.findGame(gameId);
        if (game != null) {
            // Send LOAD_GAME message back to the root client (observer)
            sendLoadGameMessage(session, game);

            // Prepare notification message
            String observerUsername = user.username();
            String notificationMessage = observerUsername + " joined game " + gameId + " as an observer.";

            // Send NOTIFICATION message to all other clients
            NotificationMessage notification = new NotificationMessage(notificationMessage);
            broadcastMessage(serializeToJson(notification), session);
        } else {
            // Game not found, send an error message back to root client
            sendErrorMessage(session, "Error: Game with ID " + gameId + " does not exist.");
        }
    }

    public void handleMakeMoveCommand(Session session, MoveCommand command, User user) throws DataAccessException, InvalidMoveException {
        Game game = database.findGame(command.getGameID());
        if (game != null && !game.isGameOver()) {
            if (!Objects.equals(user.username(), game.whiteUsername()) && !Objects.equals(user.username(), game.blackUsername())) {
                sendErrorMessage(session, "Error: Observer can't make a move");
            } else {
                // check if it's their turn
                if (((game.game().getTeamTurn() == ChessGame.TeamColor.WHITE) && (Objects.equals(user.username(), game.blackUsername()))) ||
                        ((game.game().getTeamTurn() == ChessGame.TeamColor.BLACK) && (Objects.equals(user.username(), game.whiteUsername())))) {

                    sendErrorMessage(session, "Error: wrong turn");
                    return;
                }
                // get all the valid moves
                Collection<ChessMove> validMoves = game.game().validMoves(command.move.getStartPosition());

                // Verify the move's validity
                if (validMoves.contains(command.move)) {
                    // Make move
                    game.game().makeMove(command.move);
                    // Update game state
                    if (game.game().isInCheckmate(game.game().getTeamTurn()) && (game.game().getTeamTurn() == ChessGame.TeamColor.BLACK)) {
                        game.setState(Game.GameState.BLACK);
                    } else if (game.game().isInCheckmate(game.game().getTeamTurn()) && (game.game().getTeamTurn() == ChessGame.TeamColor.WHITE)) {
                        game.setState(Game.GameState.WHITE);
                    } else if (game.game().isInStalemate(game.game().getTeamTurn())) {
                        game.setState(Game.GameState.DRAW);
                    } else {
                        game.setState(Game.GameState.UNDECIDED);
                    }
                    // Update the database with updated game
                    database.updateGame(game);

                    // Send LOAD_GAME message back to root client
                    sendLoadGameMessage(session, game);

                    // Send LOAD_GAME message to all other clients
                    LoadGameMessage loadGameMessage = new LoadGameMessage(game);
                    broadcastMessage(serializeToJson(loadGameMessage), session);

                    // Send NOTIFICATION message to all other clients
                    NotificationMessage notificationMessage = new NotificationMessage(user.username() + " made a move.");
                    broadcastMessage(serializeToJson(notificationMessage), session);
                } else {
                    sendErrorMessage(session, "Error: Invalid move");
                }
            }
        } else {
            if (game == null) { sendErrorMessage(session, "Error: Game not found"); }
            assert game != null;
            sendErrorMessage(session, "Error: Game is over");
        }
    }

    public void handleLeaveCommand(Session session, LeaveGameCommand command, User user) throws DataAccessException {
        Game game = database.findGame(command.getGameId());
        if (game != null) {

            // Check if the user is part of the game
            if (!Objects.equals(user.username(), game.whiteUsername()) && !Objects.equals(user.username(), game.blackUsername())) {
                // check if they were an observer and remove them
                if (database.getObserver(game.gameID(), user.username())) {
                    database.removeObserver(game.gameID(), user.username());
                } else {
                    sendErrorMessage(session, "Error: User was not part of the game or observing");
                    return;
                }
            }
            // Update the game to reflect the player's departure
            if (Objects.equals(user.username(), game.whiteUsername())) {
                game = game.setWhite(null); // Remove the white player
            } else if (Objects.equals(user.username(), game.blackUsername())) {
                game = game.setBlack(null); // Remove the black player
            }
            // Update the game in the database
            database.updateGame(game);

            // Prepare and send a notification message to all other clients
            String notificationMessage = user.username() + " has left the game.";
            NotificationMessage notification = new NotificationMessage(notificationMessage);
            broadcastMessage(serializeToJson(notification), session);

            // Notify root as well
            notificationMessage = "You have left the game.";
            notification = new NotificationMessage(notificationMessage);
            returnMessage(session, serializeToJson(notification));

        } else {
            // Game not found
            sendErrorMessage(session, "Error: Game with ID " + command.getGameId() + " does not exist.");
        }
    }


    public void handleResignCommand(Session session, ResignGameCommand command, User user) throws DataAccessException {
        Game game = database.findGame(command.getGameId());
        if (game != null) {
            // Check if the user is part of the game
            if (!Objects.equals(user.username(), game.whiteUsername()) && !Objects.equals(user.username(), game.blackUsername())) {
                sendErrorMessage(session, "Error: User not part of the game");
                return;
            }
            // Check the state of the game, if the game is already over then you can't resign
            if (game.isGameOver()) {
                sendErrorMessage(session, "Error: Game is already over, cannot resign from finished game");
                return;
            }

            // Mark the game as over
            if (Objects.equals(user.username(), game.whiteUsername())) {
                game = game.setWhite(null);               // Remove the white player
                game.setState(Game.GameState.BLACK);    // Black wins if White resigns
            } else {
                game = game.setBlack(null);               // Remove the black player
                game.setState(Game.GameState.WHITE);    // White wins if Black resigns
            }

            // Update the game in the database
            database.updateGame(game);

            // Prepare and send a notification back to root client
            String rootNotification = "You have resigned from the game.";
            NotificationMessage rootNotify = new NotificationMessage(rootNotification);
            returnMessage(session, serializeToJson(rootNotify));

            // Prepare and send a notification message to all clients
            String notificationMessage = user.username() + " has resigned from the game.";
            NotificationMessage notification = new NotificationMessage(notificationMessage);
            broadcastMessage(serializeToJson(notification), session);
        } else {
            // Game not found
            sendErrorMessage(session, "Error: Game with ID " + command.getGameId() + " does not exist.");
        }
    }



    /** Message sending and serialization methods below */

    private void sendErrorMessage(Session session, String errorMessage) {
        // Create and serialize an ERROR message
        ErrorMessage error = new ErrorMessage(errorMessage);
        returnMessage(session, serializeToJson(error));
    }

    private void sendLoadGameMessage(Session session, Game game) {
        // Create and serialize a LOAD_GAME message
        LoadGameMessage loadGameMessage = new LoadGameMessage(game);
        returnMessage(session, serializeToJson(loadGameMessage));
    }

    // Helper method to broadcast a message to all connected clients except the root client
    public void broadcastMessage(String message, Session rootClientSession) {
        for (Session session : sessions) {
            if (session.isOpen()) {
                if (!session.equals(rootClientSession)) {
                    try {
                        session.getRemote().sendString(message);
                    } catch (IOException e) {
                        System.err.println("Error sending message: " + e.getMessage());
                        // Optionally remove the session if it's not valid anymore
                        sessions.remove(session);
                    }
                }
            }
        }
    }

    // Helper method for sending a message back to the root client
    private void returnMessage(Session session, String message) {
        if (session.isOpen()) {
            try {
                session.getRemote().sendString(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Session is closed, cannot send message");
        }
    }

    private String serializeToJson(Object object) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ChessGame.class, new ChessGameTypeAdapter())
                .registerTypeAdapter(ChessBoard.class, new ChessBoardTypeAdapter())
                .registerTypeAdapter(ChessPiece.class, new ChessPieceTypeAdapter())
                .registerTypeAdapter(ChessMove.class, new ChessMoveTypeAdapter())
                .registerTypeAdapter(ChessPosition.class, new ChessPositionTypeAdapter())
                .create();
        return gson.toJson(object);
    }

    private <T> T deserializeFromJson(String json, Class<T> clazz) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ChessGame.class, new ChessGameTypeAdapter())
                .registerTypeAdapter(ChessBoard.class, new ChessBoardTypeAdapter())
                .registerTypeAdapter(ChessPiece.class, new ChessPieceTypeAdapter())
                .registerTypeAdapter(ChessMove.class, new ChessMoveTypeAdapter())
                .registerTypeAdapter(ChessPosition.class, new ChessPositionTypeAdapter())
                .create();
        return gson.fromJson(json, clazz);
    }
}
