package handlers;

import com.google.gson.Gson;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import results.CreateGameResult;
import results.ErrorResult;
import results.ListGamesResult;
import services.GameService;
import spark.Request;
import spark.Response;

public class GameHandler {
    private final GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

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

    private Object listGames(Request request, Response response) {
        try {
            // Get the authToken from header (assuming the auth token is passed in the header)
            String authToken = request.headers("authorization");

            // Calling the service
            ListGamesResult result = gameService.listGames(authToken);

            // Serialize result using Gson
            Gson gson = new Gson();
            String jsonResult = gson.toJson(result);

            response.status(200);
            return jsonResult;

        } catch (Exception e) {
            // Handle exceptions accordingly, e.g. unauthorized, database error, etc.
            response.status(500); // or any other appropriate error code
            return "Error: " + e.getMessage();
        }
    }

    private Object createGame(Request request, Response response) {
        Gson gson = new Gson();
        try {
            CreateGameRequest createGameRequest = gson.fromJson(request.body(), CreateGameRequest.class);
            if (createGameRequest.authToken() == null || createGameRequest.gameName() == null
            || createGameRequest.authToken().isEmpty() || createGameRequest.gameName().isEmpty()) {
                response.status(400);  // Bad request
                return gson.toJson(new ErrorResult("Error: Bad request"));
            }

            CreateGameResult result = gameService.createGame(createGameRequest.gameName(), createGameRequest.authToken());
            response.status(200);  // OK
            return gson.toJson(result);

        } catch (Exception e) {
            response.status(500);  // Internal Server Error
            return gson.toJson(new ErrorResult("Error: " + e.getMessage()));
        }
    }

    private Object joinGame(Request req, Response res) {
        return null;
    }
}
