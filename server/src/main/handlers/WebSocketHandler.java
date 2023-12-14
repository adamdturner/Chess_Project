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
    public void onMessage(Session session, String message) throws DataAccessException {
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
                            handleJoinObserverCommand(session, joinObserverCommand);
                            break;
                        case MAKE_MOVE:
                            MoveCommand moveCommand = deserializeFromJson(message, MoveCommand.class);
                            handleMakeMoveCommand(session, moveCommand);
                            break;
                        case LEAVE:
                            LeaveGameCommand leaveGameCommand = deserializeFromJson(message, LeaveGameCommand.class);
                            handleLeaveCommand(session, leaveGameCommand);
                            break;
                        case RESIGN:
                            LeaveGameCommand resignGameCommand = deserializeFromJson(message, LeaveGameCommand.class);
                            handleResignCommand(session, resignGameCommand);
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

                // Notify other clients
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

    public void handleJoinObserverCommand(Session session, JoinObserverCommand command) {

    }

    public void handleMakeMoveCommand(Session session, MoveCommand command) {

    }

    public void handleLeaveCommand(Session session, LeaveGameCommand command) {

    }

    public void handleResignCommand(Session session, LeaveGameCommand command) {

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
            if (session.isOpen() && !session.equals(rootClientSession)) {
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
                .create();
        return gson.toJson(object);
    }

    private <T> T deserializeFromJson(String json, Class<T> clazz) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ChessGame.class, new ChessGameTypeAdapter())
                .registerTypeAdapter(ChessBoard.class, new ChessBoardTypeAdapter())
                .registerTypeAdapter(ChessPiece.class, new ChessPieceTypeAdapter())
                .create();
        return gson.fromJson(json, clazz);
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        System.err.println("Error on WebSocket session: " + error.getMessage());
        // Additional error handling
    }

}
