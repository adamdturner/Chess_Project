package services;


import dao.DAOInterface;
import dataAccess.DataAccessException;


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


    /** HTTP Method: DELETE
     * Clears the database. Removes all users, games and authTokens
     * No need to pass in any parameter or return anything, the function
     * will use the private data member database
     */
    public void ClearDatabase() throws DataAccessException {
        database.ClearAll();
    }

}
