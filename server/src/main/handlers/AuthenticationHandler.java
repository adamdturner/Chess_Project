package handlers;

import com.google.gson.Gson;
import requests.LoginRequest;
import results.ErrorResult;
import results.UserAuthResult;
import services.AuthenticationService;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * AuthenticationHandler is used for handling the POST and DELETE request methods that are used for
 * a session login or logout. It uses Gson to read the request and sends it to the AuthenticationService.
 * Methods in this class return a Json string back to the server.
 */
public class AuthenticationHandler implements Route {

    private final AuthenticationService authService;
    private final Gson gson = new Gson();

    public AuthenticationHandler(AuthenticationService authService) {
        this.authService = authService;
    }

    /**
     * handle() determines if it's a post or delete method and calls the corresponding
     * function that handles that method
     */
    @Override
    public Object handle(Request request, Response response) {
        String method = request.requestMethod();

        switch (method) {
            case "POST":
                return handleLoginRequest(request, response);
            case "DELETE":
                return handleLogoutRequest(request, response);
            default:
                response.status(500);
                return gson.toJson(new ErrorResult("Error: Invalid request method"));
        }
    }

    /**
     * Method for handling login. This method calls the AuthenticationService login() function
     */
    private Object handleLoginRequest(Request request, Response response) {
        try {
            LoginRequest loginRequest = gson.fromJson(request.body(), LoginRequest.class);
            UserAuthResult result = authService.login(loginRequest);

            if (result != null) {
                response.status(200);
                return gson.toJson(result);
            } else {
                response.status(401);
                return gson.toJson(new ErrorResult("Error: unauthorized"));
            }
        } catch (Exception e) {
            response.status(500);
            return gson.toJson(new ErrorResult("Error: " + e.getMessage()));
        }
    }

    /**
     * Method for handling logout. This method calls the AuthenticationService logout() function
     */
    private Object handleLogoutRequest(Request request, Response response) {
        try {
            String authToken = request.headers("authorization");
            boolean success = authService.logout(authToken);

            if (success) {
                response.status(200);
                return gson.toJson(new results.SuccessResult(true));
            } else {
                response.status(401);
                return gson.toJson(new ErrorResult("Error: unauthorized"));
            }
        } catch (Exception e) {
            response.status(500);
            return gson.toJson(new ErrorResult("Error: " + e.getMessage()));
        }
    }
}
