package services;

import dao.DAOInterface;
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
     * @return the authentication token of the new user
     */
    public UserAuthResult register(RegisterRequest request) {
        try {
            if (database.GetUser(request.username()) != null) {
                // User already exists
                return null;
            }

            // Here, you would typically hash the password before saving it
            models.User newUser = new models.User(request.username(), request.password(), request.email());
            database.AddUser(newUser);

            // Create and store an authentication token for the new user
            // Assuming CreateAuthToken method is implemented and returns a new token for the given user
            models.AuthToken token = database.CreateAuthToken(newUser.username());

            return new UserAuthResult(newUser.username(), token.authToken());

        } catch (Exception e) {
            // Logging the error might be a good idea here
            throw new RuntimeException("Failed to register the user: " + e.getMessage());
        }
    }

}
