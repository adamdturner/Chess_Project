package dao;

import dataAccess.DataAccessException;
import models.Game;
import models.User;
import models.AuthToken;

import java.util.Collection;


/**
 * DAOInterface is responsible for storing and retrieving the server’s data (users, games, etc.)
 * It is an interface that can be implemented based on where the data is stored
 * Most methods are CRUD operations (Create, Read, Update, Delete)
 */
public interface DAOInterface {

    // Model objects are parameters and return values for DAOInterface methods
    // Provide all data storage and retrieval operations needed by the server
    // The method interfaces on your DAOInterface classes should not need to change when they are re-implemented,
    // because the rest of your server code should be unaware of where data is being stored (main memory vs. database)

    // Methods on your DAOInterface classes will be CRUD operations:
    // ->Create objects in data storage
    // ->Read objects in data storage
    // ->Update objects in data storage
    // ->Delete objects from the data storage



    /** Delete:
     * A method for clearing all data from the database
     *
     * @throws DataAccessException
     */
    void ClearAll() throws DataAccessException;



    //User


    /** Create:
     * A method to add a new User to the database
     *
     * @param user
     * @return the created user
     * @throws DataAccessException
     */
    void AddUser(User user) throws DataAccessException;

    /** READ:
     * A method to find a user in the database
     *
     * @param username
     * @return the user if found, else return null
     * @throws DataAccessException
     */
    User GetUser(String username) throws DataAccessException;

    /** Update:
     * The player's username is provied and should be saved as the whitePlayer in the database
     *
     * @param username
     * @throws DataAccessException
     */
    void SetWhitePlayer(String username) throws DataAccessException;

    /** Update:
     * The player's username is provied and should be saved as the blackPlayer in the database
     *
     * @param username
     * @throws DataAccessException
     */
    void SetBlackPlayer(String username) throws DataAccessException;

    /** Delete:
     * A method to delete the given user
     *
     * @param username
     * @throws DataAccessException
     */
    void DeleteUser(String username) throws DataAccessException;




    //Game


    /** Create:
     * A method to create a new game with the given game name
     *
     * @param gameName
     * @return the created game
     * @throws DataAccessException
     */
    Game CreateGame(String gameName) throws DataAccessException;

    /** Update:
     * Update game by replacing the chessGame string corresponding to a given gameID with a new chessGame string
     *
     * @param g
     * @throws DataAccessException
     */
    void UpdateGame(Game g) throws DataAccessException;

    /** Read:
     * A method for retrieving a specified game from the database by gameID
     *
     * @param gameID
     * @return the game if found else return null
     * @throws DataAccessException
     */
    Game FindGame(int gameID) throws DataAccessException;

    /** Read:
     * A method for retrieving all games from the database
     *
     * @return a list of all the games
     * @throws DataAccessException
     */
    Collection<Game> FindAllGames() throws DataAccessException;

    /** Delete:
     * A method for deleting a given game
     *
     * @param gameID
     * @throws DataAccessException
     */
    void DeleteGame(int gameID) throws DataAccessException;



    //AuthToken


    /** Create:
     * A method to create a new token with the given username and adds it to the database
     * First check if username has a token associated with it, if not then make one. If it already
     * has a token associated then delete it and make a new one.
     *
     * @param username
     * @return the created token
     * @throws DataAccessException
     */
    AuthToken CreateAuthToken(String username) throws DataAccessException;


    /** Read:
     * A method to read the token indicated by the authToken string parameter
     *
     * @param authToken
     * @return the token if it exists else return null
     * @throws DataAccessException
     */
    AuthToken GetAuthToken(String authToken) throws DataAccessException;

    /** Delete:
     * A method to delete the token indicated by the authToken string parameter
     *
     * @param authToken
     * @throws DataAccessException
     */
    void DeleteAuthToken(String authToken) throws DataAccessException;

}
