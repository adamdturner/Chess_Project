package handlers;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import requests.RegisterRequest;
import results.ErrorResult;
import results.Result;
import results.SuccessResult;
import services.UserService;
import spark.Request;
import spark.Response;

public class UserHandler {
    private final UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public Result handleRegisterRequest(Request req, Response res) throws DataAccessException {
        // Parse the request body to get RegisterRequest object
        RegisterRequest request = new Gson().fromJson(req.body(), RegisterRequest.class);

        // Call the UserService to handle the registration
        Result result = userService.register(request);

        if (result instanceof SuccessResult) {
            res.status(200);

        } else if (result instanceof ErrorResult) {
            ErrorResult error = (ErrorResult) result;
            if ("Error: already taken".equals(error.getMessage())) {
                res.status(403);
            } else if ("Error: bad request".equals(error.getMessage())) {
                res.status(400);
            } else {
                res.status(500);
            }
        }
        return result;
    }
}

