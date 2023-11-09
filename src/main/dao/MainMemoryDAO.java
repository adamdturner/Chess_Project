package dao;

import chess.ChessGame;
import chess.MyGame;
import dataAccess.DataAccessException;
import models.AuthToken;
import models.Game;
import models.User;

import java.util.*;

/** Provide all data storage and retrieval operations needed by the server.
 * MainMemoryDAO will store the server’s data in main memory (RAM)
 * using standard data structures (maps, sets, lists)
 */
public class MainMemoryDAO implements DAOInterface {

    private final Map<String, User> users = new HashMap<>();      // map of users which can be found by their username
    private final Map<String, AuthToken> tokens = new HashMap<>();    // map of tokens associated with a String authToken
    private final Map<Integer, Game> games = new HashMap<>();     // map of games associated by their gameID
    private final Map<Integer, List<String>> observers = new HashMap<>(); // map of observers with int gameID and list of usernames

    private int currentID;
    private int currentTokenNum;

    public MainMemoryDAO() {
        currentID = 1000;
        currentTokenNum = 1000;
    }

    @Override
    public void clearAll() throws DataAccessException {
        users.clear();
        tokens.clear();
        games.clear();
        observers.clear();
        currentID = 1000;
        currentTokenNum = 1000;
    }

    @Override
    public void addUser(User user) throws DataAccessException {
        users.putIfAbsent(user.username(), user);
    }

    @Override
    public User getUser(String username) throws DataAccessException {
        return users.get(username);
    }

    @Override
    public void setWhitePlayer(int gameID, String username) throws DataAccessException {
        Game oldGame = games.get(gameID);
        Game newGame = new Game(oldGame.gameID(), username, oldGame.blackUsername(), oldGame.gameName(), oldGame.game());
        games.put(gameID, newGame);
    }

    @Override
    public void setBlackPlayer(int gameID, String username) throws DataAccessException {
        Game oldGame = games.get(gameID);
        Game newGame = new Game(oldGame.gameID(), oldGame.whiteUsername(), username, oldGame.gameName(), oldGame.game());
        games.put(gameID, newGame);
    }

    @Override
    public Game createGame(String gameName) {
        ChessGame chessGame = new MyGame(); // ChessGame interface implemented by MyGame class

        // Use currentID to set gameID and then increment it for the next game
        int gameID = currentID++;
        Game newGame = new Game(gameID, null, null, gameName, chessGame);

        // Save the game to the games map
        games.put(gameID, newGame);

        return newGame;
    }

    @Override
    public Game findGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    @Override
    public Collection<Game> findAllGames() throws DataAccessException {
        // Return an immutable collection to ensure it's not modified externally
        return Collections.unmodifiableCollection(games.values());
    }

    @Override
    public void addObserver(int gameID, String username) throws DataAccessException {
        observers.computeIfAbsent(gameID, k -> new ArrayList<>()).add(username);
    }


    @Override
    public AuthToken createAuthToken(String username) throws DataAccessException {
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
    public AuthToken getAuthToken(String authToken) throws DataAccessException {
        return tokens.get(authToken);
    }

    @Override
    public void deleteAuthToken(String authToken) throws DataAccessException {
        if (tokens.get(authToken) != null) {
            tokens.remove(authToken);
        }
        else {
            throw new DataAccessException("Tried to delete authToken that doesn't exist");
        }
    }
}
