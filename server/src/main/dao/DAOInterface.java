package dao;

import dataAccess.DataAccessException;
import models.AuthToken;
import models.Game;
import models.User;

import java.util.Collection;


/**
 * DAOInterface is responsible for storing and retrieving the server’s data (users, games, etc.)
 * It is an interface that can be implemented based on where the data is stored
 * Most methods are CRUD operations (Create, Read, Update, Delete)
 */
public interface DAOInterface {

    /** Delete:
     * A method for clearing all data from the database
     *
     * @throws DataAccessException
     */
    void clearAll() throws DataAccessException;



    //User

    /** Create:
     * A method to add a new User to the database
     *
     * @param user
     * @return the created user
     * @throws DataAccessException
     */
    void addUser(User user) throws DataAccessException;

    /** READ:
     * A method to find a user in the database
     *
     * @param username
     * @return the user if found, else return null
     * @throws DataAccessException
     */
    User getUser(String username) throws DataAccessException;

    /** Update:
     * The player's username is provied and should be saved as the whitePlayer in the database
     *
     * @param username
     * @throws DataAccessException
     */
    void setWhitePlayer(int gameID, String username) throws DataAccessException;

    /** Update:
     * The player's username is provied and should be saved as the blackPlayer in the database
     *
     * @param username
     * @throws DataAccessException
     */
    void setBlackPlayer(int gameID, String username) throws DataAccessException;



    //Game

    /** Create:
     * A method to create a new game with the given game name
     *
     * @param gameName
     * @return the created game
     * @throws DataAccessException
     */
    Game createGame(String gameName) throws DataAccessException;

    /** Read:
     * A method for retrieving a specified game from the database by gameID
     *
     * @param gameID
     * @return the game if found else return null
     * @throws DataAccessException
     */
    Game findGame(int gameID) throws DataAccessException;

    /** Read:
     * A method for retrieving all games from the database
     *
     * @return a list of all the games
     * @throws DataAccessException
     */
    Collection<Game> findAllGames() throws DataAccessException;


    /** Update:
     * A method for updating a given game in the database
     *
     * @param game
     * @throws DataAccessException
     */
    public void updateGame(Game game) throws DataAccessException;

    /**
     * A method for adding an observer with username to a given game with gameID
     *
     * @param gameID
     * @param username
     * @throws DataAccessException
     */
    void addObserver(int gameID, String username) throws DataAccessException;



    //AuthToken

    boolean getObserver(int gameID, String username) throws DataAccessException;

    boolean removeObserver(int gameID, String username) throws DataAccessException;

    /** Create:
     * A method to create a new token with the given username and adds it to the database
     * First check if username has a token associated with it, if not then make one. If it already
     * has a token associated then delete it and make a new one.
     *
     * @param username
     * @return the created token
     * @throws DataAccessException
     */
    AuthToken createAuthToken(String username) throws DataAccessException;

    /** Read:
     * A method to read the token indicated by the authToken string parameter
     *
     * @param authToken
     * @return the token if it exists else return null
     * @throws DataAccessException
     */
    AuthToken getAuthToken(String authToken) throws DataAccessException;

    /** Delete:
     * A method to delete the token indicated by the authToken string parameter
     *
     * @param authToken
     * @throws DataAccessException
     */
    void deleteAuthToken(String authToken) throws DataAccessException;

}
