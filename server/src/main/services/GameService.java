package services;

import dao.DAOInterface;
import dataAccess.DataAccessException;
import dataAccess.UnauthorizedException;
import models.AuthToken;
import models.Game;
import results.CreateGameResult;
import results.JoinResult;
import results.ListGamesResult;
import results.SuccessResult;

import java.util.ArrayList;
import java.util.List;


/**
 * Service class used to implement the list game, create game and join game endpoints
 */
public class GameService {

    /**
     * Private DAOInterface object. Use the interface because the services don't need
     * to know where the data is stored.
     */
    private final DAOInterface database;

    /**
     * Constructor
     * @param database
     */
    public GameService(DAOInterface database) {
        this.database = database;
    }

    /** HTTP Method: GET
     * Method to access the list of game in the database if authToken is valid
     * @param authToken
     * @return a list of all games stored in database
     */
    public ListGamesResult listGames(String authToken) throws UnauthorizedException, DataAccessException {
        try {
            // Step 1: Verify authorization using authToken
            AuthToken token = database.getAuthToken(authToken);
            if (token == null) {
                throw new UnauthorizedException("Unauthorized");
            }

            // Step 2: Retrieve all games from database
            List<Game> allGames = new ArrayList<>(database.findAllGames());

            return new ListGamesResult(allGames);

        } catch (Exception e) {
            // Handle exceptions and return appropriate error result
            throw new UnauthorizedException("Unauthorized");
        }
    }

    /** HTTP Method: POST
     * Creates a new game with a game name if a valid authToken is passed in
     * @param gameName
     * @param authToken
     */
    public CreateGameResult createGame(String gameName, String authToken) throws DataAccessException, UnauthorizedException {
        // Ensure valid gameName here if needed
        // edit this to have any other checks to make sure the gameName is valid
        if (gameName.isEmpty()) {
            throw new DataAccessException("Error: Invalid game name");
        }

        // Step 1: Verify authorization using authToken
        AuthToken token = database.getAuthToken(authToken);
        if (token == null) {
            throw new UnauthorizedException("Unauthorized");
        }

        // Step 2: Create a new game
        Game newGame = database.createGame(gameName);

        return new CreateGameResult(newGame.gameID());
    }

    /** HTTP Method: PUT
     * Verifies that the game exists. If color is specified, add the caller as the requested color to the game.
     * If no color is specified then the user is joined as an observer
     *
     * @param authToken
     * @param playerColor
     * @param gameID
     */
    public JoinResult joinGame(String authToken, String playerColor, int gameID) throws UnauthorizedException, Exception, DataAccessException {
        try {
            // Step 1: Verify authorization using authToken
            AuthToken token = database.getAuthToken(authToken);
            if (token == null) {
                throw new UnauthorizedException("Error: Unauthorized");
            }

            // Step 2: Get the existing game
            Game existingGame = database.findGame(gameID);

            // Step 3: Check and assign player based on color or add as observer
            if (playerColor == null || playerColor.isEmpty()) {
                // Add as an observer
                database.addObserver(gameID, token.username());
            } else if ("WHITE".equalsIgnoreCase(playerColor)) {
                if (existingGame.whiteUsername() != null) {
                    throw new Exception("Error: already taken");
                }
                database.setWhitePlayer(gameID, token.username());
            } else if ("BLACK".equalsIgnoreCase(playerColor)) {
                if (existingGame.blackUsername() != null) {
                    throw new Exception("Error: already taken");
                }
                database.setBlackPlayer(gameID, token.username());
            } else {
                throw new Exception("Invalid player color");
            }

            return new JoinResult(true, existingGame);

        } catch (UnauthorizedException ue) {
            throw ue; // Pass it up to be caught and processed by the handler
        } catch (DataAccessException e) {
            throw new DataAccessException("Error: " + e.getMessage());
        } catch (Exception e) {
            throw new Exception("Error: already taken");
        }
    }

}
