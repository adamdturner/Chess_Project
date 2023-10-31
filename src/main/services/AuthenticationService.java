package services;

import dao.DAOInterface;
import dataAccess.DataAccessException;
import models.AuthToken;
import models.User;
import requests.LoginRequest;
import results.ErrorResult;
import results.Result;
import results.SuccessResult;

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

    //Login
    //property:	                value:
    //Description	            Logs in an existing user (returns a new authToken).
    //URL path	                /session
    //HTTP Method	            POST
    //Body	                    { "username":"", "password":"" }
    //Success response	        [200] { "username":"", "authToken":"" }
    //Failure response	        [401] { "message": "Error: unauthorized" }
    //Failure response	        [500] { "message": "Error: description" }

    /** HTTP Method: POST
     * A method for carrying out the login request of the specified user
     * logs in an existing user
     * @param username,password
     * @return a SuccessResult with a message, authToken and username or return an ErrorResult
     */
    public Result login(String username, String password) throws DataAccessException {
        User user = database.GetUser(username);

        if (user != null && user.password().equals(password)) {
            // Generate an authToken (this can be any method you like, for now, just creating a simple one
            AuthToken authToken = database.CreateAuthToken(username);

            return new SuccessResult(authToken.authToken(), username);
        } else {
            return new ErrorResult("Error: unauthorized");
        }
    }


    //Logout
    //property:	                value:
    //Description	            Logs out the user represented by the authToken.
    //URL path	                /session
    //HTTP Method	            DELETE
    //Headers	                authorization: <authToken>
    //Success response	        [200]
    //Failure response	        [401] { "message": "Error: unauthorized" }
    //Failure response	        [500] { "message": "Error: description" }

    /** HTTP Method: DELETE
     * A method for carrying out the logout request of the specified user
     * logs out the user represented by the authToken
     * @param authToken
     */
    public Result logout(String authToken) throws DataAccessException {
        AuthToken token = database.FindAuthToken(authToken);

        if (token != null) {
            database.DeleteAuthToken(authToken);
            return new SuccessResult();
        } else {
            return new ErrorResult("Error: unauthorized");
        }
    }

}
