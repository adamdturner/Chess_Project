package services;

import dao.DAOInterface;
import dataAccess.DataAccessException;
import models.AuthToken;
import models.User;
import requests.RegisterRequest;
import results.ErrorResult;
import results.Result;
import results.SuccessResult;

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
    public Result register(RegisterRequest request) throws DataAccessException {
        User user = database.GetUser(request.getUsername());

        // Check if user already exists
        if (user != null) {
            return new ErrorResult("Error: already taken");
        }

        // Create a new user and save to database
        User newUser = new User(request.getUsername(), request.getPassword(), request.getEmail());
        database.AddUser(newUser);

        // Generate an authToken for the user
        // This next line both creates the authtoken and adds it to the database at the same time
        AuthToken authToken = database.CreateAuthToken(request.getUsername());

        return new SuccessResult(newUser.username(), authToken.authToken());
    }

}
