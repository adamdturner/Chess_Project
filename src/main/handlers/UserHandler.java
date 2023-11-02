package handlers;

import com.google.gson.Gson;
import dataAccess.UnauthorizedException;
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
            UserAuthResult result = userService.register(regRequest);

            if (result != null) {
                response.status(200);
                return gson.toJson(result);
            } else {
                response.status(403);
                return gson.toJson(new ErrorResult("Error: already taken"));
            }

        } catch (UnauthorizedException ue) {
            response.status(400);
            return gson.toJson(new ErrorResult(ue.getMessage()));
        } catch (Exception e) {
            response.status(500);
            return gson.toJson(new ErrorResult("Error: " + e.getMessage()));
        }
    }
}
