package handlers;

import com.google.gson.Gson;
import requests.RegisterRequest;
import results.ErrorResult;
import results.UserAuthResult;
import services.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class UserHandler implements Route {

    private final UserService userService;
    private final Gson gson = new Gson();

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public Object handle(Request request, Response response) {
        try {
            RegisterRequest regRequest = gson.fromJson(request.body(), RegisterRequest.class);

            // Check if username, password, or email are missing or empty
            if (regRequest.username() == null || regRequest.username().trim().isEmpty() ||
                    regRequest.password() == null || regRequest.password().trim().isEmpty() ||
                    regRequest.email() == null || regRequest.email().trim().isEmpty()) {
                response.status(400);
                return gson.toJson(new ErrorResult("Error: bad request"));
            }

            UserAuthResult result = userService.register(regRequest);

            if (result != null) {
                response.status(200);
                return gson.toJson(result);
            } else {
                response.status(403);
                return gson.toJson(new ErrorResult("Error: already taken"));
            }

        } catch (Exception e) {
            response.status(500);
            return gson.toJson(new ErrorResult("Error: " + e.getMessage()));
        }
    }
}
