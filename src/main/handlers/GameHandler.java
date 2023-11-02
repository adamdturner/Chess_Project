package handlers;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.UnauthorizedException;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.ListGamesRequest;
import results.CreateGameResult;
import results.ErrorResult;
import results.ListGamesResult;
import results.SuccessResult;
import services.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Arrays;

/**
 * GameHandler is used for handling the GET, POST and PUT request methods that are used for
 * a game listing, creation or join. It uses Gson to read the request and sends it to the GameService.
 * Methods in this class return a Json string back to the server.
 */
public class GameHandler implements Route {
    private final GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * handle() determines if it's a GET, POST or PUT method and calls the corresponding
     * function that handles that method
     */
    @Override
    public Object handle(Request req, Response res) throws Exception {
        String method = req.requestMethod();
        switch (method) {
            case "GET":
                return listGames(req, res);
            case "POST":
                return createGame(req, res);
            case "PUT":
                return joinGame(req, res);
            default:
                throw new Exception("Invalid HTTP method");
        }
    }

    /**
     * Calls the GameService listGames() function to carry out the request
     * recieves a list of games back in the result.
     */
    private Object listGames(Request request, Response response) {
        Gson gson = new Gson();
        try {

            // Get the authToken from header (assuming the auth token is passed in the header)
            String authToken = request.headers("Authorization");

            // Calling the service
            ListGamesResult result = gameService.listGames(authToken);

            // Return the result to Json
            response.status(200);
            return gson.toJson(result);

        } catch (DataAccessException e) {
            response.status(500);
            return gson.toJson(new ErrorResult("Error: " + e.getMessage()));
        } catch (UnauthorizedException err) {
            response.status(401);
            return gson.toJson(new ErrorResult("Error: unauthorized"));
        }
    }

    /**
     * Calls the createGame() function in GameService which carries out the game creation in the database
     */
    private Object createGame(Request request, Response response) {
        Gson gson = new Gson();
        try {
            CreateGameRequest createGameRequest = gson.fromJson(request.body(), CreateGameRequest.class);
            createGameRequest.setAuthToken(request.headers("Authorization"));
            if (createGameRequest.getAuthToken() == null || createGameRequest.getGameName() == null
            || createGameRequest.getAuthToken().isEmpty() || createGameRequest.getGameName().isEmpty()) {
                response.status(400);  // Bad request
                return gson.toJson(new ErrorResult("Error: Bad request"));
            }

            CreateGameResult result = gameService.createGame(createGameRequest.getGameName(), createGameRequest.getAuthToken());
            response.status(200);  // OK
            return gson.toJson(result);

        } catch (UnauthorizedException ue) {
            response.status(401);  // Unauthorized
            return gson.toJson(new ErrorResult("Error: Unauthorized"));
        } catch (Exception e) {
            response.status(500);  // Internal Server Error
            return gson.toJson(new ErrorResult("Error: " + e.getMessage()));
        }
    }

    /**
     * Calls the joinGame() function in the GameService to let the user join a game
     */
    private Object joinGame(Request request, Response response) {
        Gson gson = new Gson();
        try {
            JoinGameRequest joinGameRequest = gson.fromJson(request.body(), JoinGameRequest.class);
            joinGameRequest.setAuthToken(request.headers("Authorization"));


            if (joinGameRequest.getAuthToken() == null || joinGameRequest.getAuthToken().isEmpty() || joinGameRequest.getGameID() == 0) {
                response.status(400);  // Bad request
                return gson.toJson(new ErrorResult("Error: Bad request"));
            }
            if (joinGameRequest.getPlayerColor() != null && !Arrays.asList("WHITE", "BLACK").contains(joinGameRequest.getPlayerColor().toUpperCase())) {
                response.status(400);  // Bad request
                return gson.toJson(new ErrorResult("Error: Bad request"));
            }

            SuccessResult result = gameService.joinGame(joinGameRequest.getAuthToken(),joinGameRequest.getPlayerColor(), joinGameRequest.getGameID());
            response.status(200);  // OK
            return gson.toJson(result);

        } catch (UnauthorizedException ue) {
            response.status(401);  // Unauthorized
            return gson.toJson(new ErrorResult("Error: Unauthorized"));
        } catch (DataAccessException e) {
            response.status(500);  // Internal Server Error
            return gson.toJson(new ErrorResult("Error: " + e.getMessage()));
        } catch (Exception pte) {
            response.status(403);  // Forbidden (Position already taken)
            return gson.toJson(new ErrorResult("Error: already taken"));
        }
    }
}
