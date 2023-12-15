package ui;

import chess.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.AuthToken;
import models.Game;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.ListGamesRequest;
import results.CreateGameResult;
import results.JoinResult;
import results.ListGamesResult;
import results.SuccessResult;

import java.net.HttpURLConnection;
import java.util.List;

public class PostLoginFacade {
    private final ServerConnection serverConnection;

    public PostLoginFacade(String serverUrl) {
        this.serverConnection = new ServerConnection(serverUrl);
    }

    public boolean logout(AuthToken authToken) {
        try {
            // Register the ChessGameTypeAdapter with Gson
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(ChessGame.class, new ChessGameTypeAdapter())
                    .registerTypeAdapter(ChessBoard.class, new ChessBoardTypeAdapter())
                    .registerTypeAdapter(ChessPiece.class, new ChessPieceTypeAdapter())
                    .create();
            String response = serverConnection.sendRequest("/session", null, "DELETE", authToken.authToken());

            // Parse the response to a SuccessResult object
            SuccessResult result = gson.fromJson(response, SuccessResult.class);

            // Return the success value from the SuccessResult
            return result.success();
        } catch (Exception e) {
            System.out.println("Logout error: " + e.getMessage());
            return false;
        }
    }

    public int createGame(AuthToken authToken, String gameName) {
        try {
            // Register the ChessGameTypeAdapter with Gson
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(ChessGame.class, new ChessGameTypeAdapter())
                    .registerTypeAdapter(ChessBoard.class, new ChessBoardTypeAdapter())
                    .registerTypeAdapter(ChessPiece.class, new ChessPieceTypeAdapter())
                    .create();
            CreateGameRequest request = new CreateGameRequest(gameName, authToken.authToken());

            // Assuming the endpoint for creating a game is something like "/games"
            String response = serverConnection.sendRequest("/game", request, "POST", authToken.authToken());

            // Parse the response
            CreateGameResult result = gson.fromJson(response, CreateGameResult.class);

            // Check if a valid game ID was returned
            return result.getGameID();
        } catch (Exception e) {
            System.out.println("Error creating game: " + e.getMessage());
            return 0;
        }
    }

    public List<Game> listGames(AuthToken authToken) {
        try {
            // Register the ChessGameTypeAdapter with Gson
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(ChessGame.class, new ChessGameTypeAdapter())
                    .registerTypeAdapter(ChessBoard.class, new ChessBoardTypeAdapter())
                    .registerTypeAdapter(ChessPiece.class, new ChessPieceTypeAdapter())
                    .create();
            ListGamesRequest request = new ListGamesRequest(authToken.authToken());

            // Send the GET request to the server
            String response = serverConnection.sendRequest("/game", request, "GET", authToken.authToken());

            // Parse the response to get the list of games
            ListGamesResult result = gson.fromJson(response, ListGamesResult.class);

            // Return the list of games
            return result.getGames();
        } catch (Exception e) {
            System.out.println("Error listing games: " + e.getMessage());
            return null;  // Return null or an empty list as appropriate
        }
    }

    public Game joinGame(AuthToken authToken, int gameID, String playerColor) {
        try {
            // Register the ChessGameTypeAdapter with Gson
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(ChessGame.class, new ChessGameTypeAdapter())
                    .registerTypeAdapter(ChessBoard.class, new ChessBoardTypeAdapter())
                    .registerTypeAdapter(ChessPiece.class, new ChessPieceTypeAdapter())
                    .create();
            JoinGameRequest request = new JoinGameRequest(playerColor, gameID, authToken.authToken());
            String response = serverConnection.sendRequest("/game", request, "PUT", authToken.authToken());

            // Check the response code from serverConnection
            int responseCode = serverConnection.getLastResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                JoinResult result = gson.fromJson(response, JoinResult.class);
                return result.game();
            } else {
                // Handle failed joinGame
                System.out.println("Join failed: check if color is already taken");
                return null;
            }
        } catch (Exception e) {
            System.out.println("Error joining game: " + e.getMessage());
            return null;
        }
    }

    public Game joinObserver(AuthToken authToken, int gameID) {
        try {
            // Register the ChessGameTypeAdapter with Gson
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(ChessGame.class, new ChessGameTypeAdapter())
                    .registerTypeAdapter(ChessBoard.class, new ChessBoardTypeAdapter())
                    .registerTypeAdapter(ChessPiece.class, new ChessPieceTypeAdapter())
                    .create();
            // Assuming observer joins without specifying color
            JoinGameRequest request = new JoinGameRequest(null, gameID, authToken.authToken());
            String response = serverConnection.sendRequest("/game", request, "PUT", authToken.authToken());

            int responseCode = serverConnection.getLastResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                JoinResult result = gson.fromJson(response, JoinResult.class);
                return result.game();
            } else {
                System.out.println("Join as observer failed");
                return null;
            }
        } catch (Exception e) {
            System.out.println("Error joining game as an observer: " + e.getMessage());
            return null;
        }
    }

}

