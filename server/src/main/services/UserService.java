package services;

import dao.DAOInterface;
import dataAccess.UnauthorizedException;
import models.AuthToken;
import models.User;
import requests.RegisterRequest;
import results.UserAuthResult;

/**
 * Service class used to implement the register endpoint
 */
public class UserService {

    /**
     * Private DAOInterface object. Use the interface because the services don't need
     * to know where the data is stored.
     */
    private final DAOInterface database;

    /**
     * Constructor
     * @param database
     */
    public UserService(DAOInterface database) {
        this.database = database;
    }


    /** HTTP Method: POST
     * registers a new user with a username, password and email from the request object
     *
     * @param request
     */
    public UserAuthResult register(RegisterRequest request) throws UnauthorizedException {
        try {
            if (database.getUser(request.username()) != null) {
                // User already exists
                return null;
            }
            if (request.username() == null || request.username().isEmpty() ||
                    request.password() == null || request.password().isEmpty() ||
                    request.email() == null || request.email().isEmpty()) {
                throw new UnauthorizedException("Error: bad request");
            }

            // Here, you would typically hash the password before saving it
            User newUser = new User(request.username(), request.password(), request.email());
            database.addUser(newUser);

            // Create and store an authentication token for the new user
            // Assuming CreateAuthToken method is implemented and returns a new token for the given user
            AuthToken token = database.createAuthToken(newUser.username());

            return new UserAuthResult(newUser.username(), token.authToken());

        } catch (UnauthorizedException ue) {
            throw ue;
        } catch (Exception e) {
            // Logging the error might be a good idea here
            throw new RuntimeException("Failed to register the user: " + e.getMessage());
        }
    }

}
