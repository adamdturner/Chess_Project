package dao;

import chess.ChessGame;
import chess.MyGame;
import dataAccess.DataAccessException;
import models.AuthToken;
import models.Game;
import models.User;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** Provide all data storage and retrieval operations needed by the server.
 * MainMemoryDAO will store the server’s data in main memory (RAM)
 * using standard data structures (maps, sets, lists)
 */
public class MainMemoryDAO implements DAOInterface {

    private final Map<String, User> users = new HashMap<>();      // map of users which can be found by their username
    private final Map<String, AuthToken> tokens = new HashMap<>();    // map of tokens associated with a String authToken
    private final Map<Integer, Game> games = new HashMap<>();     // map of games associated by their gameID

    private int currentID;
    private int currentTokenNum;

    public MainMemoryDAO() {
        currentID = 1000;
        currentTokenNum = 1000;
    }

    @Override
    public void ClearAll() throws DataAccessException {
        users.clear();
        tokens.clear();
        games.clear();
//        currentID = 1000;                 fixme not sure if I need to reset ID's when doing clear
//        currentTokenNum = 1000;
    }

    @Override
    public void AddUser(User user) throws DataAccessException {
        users.putIfAbsent(user.username(), user);
    }

    @Override
    public User GetUser(String username) throws DataAccessException {
        return users.get(username);
    }

    @Override
    public void SetWhitePlayer(String username) throws DataAccessException {        //fixme

    }

    @Override
    public void SetBlackPlayer(String username) throws DataAccessException {        //fixme

    }

    @Override
    public void DeleteUser(String username) throws DataAccessException {
        if (users.get(username) != null) {
            users.remove(username);
        }
        else {
            throw new DataAccessException("Tried to delete user that doesn't exist");
        }
    }

    @Override
    public Game CreateGame(String gameName) {
        ChessGame chessGame = new MyGame();

        // Use currentID to set gameID and then increment it for the next game
        int gameID = currentID++;
        Game newGame = new Game(gameID, null, null, gameName, chessGame);

        // Save the game to the games map
        games.put(gameID, newGame);

        return newGame;
    }

    @Override
    public void UpdateGame(Game g) throws DataAccessException {
        games.put(g.gameID(), g);
    }

    @Override
    public Game FindGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    @Override
    public Collection<Game> FindAllGames() throws DataAccessException {
        // Return an immutable collection to ensure it's not modified externally
        return Collections.unmodifiableCollection(games.values());
    }

    @Override
    public void DeleteGame(int gameID) throws DataAccessException {
        if (games.get(gameID) != null) {
            games.remove(gameID);
        }
        else {
            throw new DataAccessException("Tried to delete game that doesn't exist");
        }
    }

    @Override
    public AuthToken CreateAuthToken(String username) throws DataAccessException {
        // Ensure the username exists in the users map
        if (!users.containsKey(username)) {
            throw new DataAccessException("Tried to create an authToken for a user that doesn't exist");
        }

        // Generate a unique token using the username and the currentTokenNum
        String token = username + "_" + currentTokenNum;
        currentTokenNum++;

        // Create an AuthToken object
        AuthToken authToken = new AuthToken(token, username);

        // Store the AuthToken in the tokens map
        tokens.put(token, authToken);

        // Return the newly created AuthToken
        return authToken;
    }

    @Override
    public AuthToken GetAuthToken(String authToken) throws DataAccessException {
        return tokens.get(authToken);
    }

    @Override
    public void DeleteAuthToken(String authToken) throws DataAccessException {
        if (tokens.get(authToken) != null) {
            tokens.remove(authToken);
        }
        else {
            throw new DataAccessException("Tried to delete authToken that doesn't exist");
        }
    }
}
