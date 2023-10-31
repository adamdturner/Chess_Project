package services;


import dao.DAOInterface;
import models.AuthToken;
import models.Game;
import models.User;
import results.ErrorResult;
import results.Result;
import results.SuccessResult;


/**
 * Service class used to implement the clear endpoint
 */
public class ClearApplicationService {

    /**
     * Private DAOInterface object. Use the interface because the services don't need
     * to know where the data is stored.
     */
    private final DAOInterface database;

    /**
     * Constructor
     * @param database
     */
    public ClearApplicationService(DAOInterface database) {
        this.database = database;
    }

    //Clear application
    //property:	                value:
    //Description	            Clears the database. Removes all users, games, and authTokens.
    //URL path	                /db
    //HTTP Method	            DELETE
    //Success response	        [200]
    //Failure response	        [500] { "message": "Error: description" }

    /** HTTP Method: DELETE
     * Clears the database. Removes all users, games and authTokens
     * No need to pass in any parameter or return anything, the function
     * will use the private data member called data which is the database
     */
    public Result clearDatabase() {
        try {
            database.ClearAll();
            return new SuccessResult();
        } catch (Exception e) {
            return new ErrorResult("Error: " + e.getMessage());
        }
    }

}
