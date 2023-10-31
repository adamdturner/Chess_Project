package handlers;

import com.google.gson.Gson;
import dao.DAOInterface;
import dataAccess.DataAccessException;
import requests.LoginRequest;
import results.Result;
import services.AuthenticationService;

public class AuthenticationHandler {
    private final AuthenticationService authenticationService;

    public AuthenticationHandler(DAOInterface database) {
        this.authenticationService = new AuthenticationService(database);
    }

    public Result handleLoginRequest(String body) throws DataAccessException {
        // Use GSON to parse the body into a LoginRequest object
        Gson gson = new Gson();
        LoginRequest request = gson.fromJson(body, LoginRequest.class);

        return authenticationService.login(request.getUsername(), request.getPassword());
    }

    public Result handleLogoutRequest(String authTokenHeader) throws DataAccessException {
        // Assuming authToken is passed as a header
        return authenticationService.logout(authTokenHeader);
    }
}

