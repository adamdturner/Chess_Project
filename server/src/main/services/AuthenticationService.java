package services;

import dao.DAOInterface;
import models.AuthToken;
import models.User;
import requests.LoginRequest;
import results.UserAuthResult;

/**
 * Service class used to implement the login and logout endpoints
 */
public class AuthenticationService {

    /**
     * Private DAOInterface object. Use the interface because the services don't need
     * to know where the data is stored.
     */
    private final DAOInterface database;

    /**
     * Constructor
     * @param database
     */
    public AuthenticationService(DAOInterface database) {
        this.database = database;
    }

    /** HTTP Method: POST
     * A method for carrying out the login request of the specified user
     * @return a SuccessResult with a message, authToken and username or return an ErrorResult
     */
    public UserAuthResult login(LoginRequest request) {
        try {
            User user = database.getUser(request.username());

            // If user exists and password matches
            if (user != null && user.password().equals(request.password())) {
                // Generate a new authentication token
                AuthToken token = database.createAuthToken(user.username());
                return new UserAuthResult(user.username(), token.authToken());
            }

            return null;
        } catch (Exception e) {
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }

    /** HTTP Method: DELETE
     * A method for carrying out the logout request of the specified user if authorized by passing in a valid authToken
     * @param authToken
     */
    public boolean logout(String authToken) throws Exception {
        try {
            AuthToken token = database.getAuthToken(authToken);

            if (token != null) {
                database.deleteAuthToken(authToken);
                return true;
            }

            return false;
        } catch (Exception e) {
            throw new Exception("Logout failed: " + e.getMessage());
        }
    }

}
