package services;

import chess.ChessGame;
import chess.MyGame;
import dao.DAOInterface;
import dataAccess.DataAccessException;
import models.AuthToken;
import models.Game;
import results.CreateGameResult;
import results.ErrorResult;
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
     * Gives a list of games in the database wherever they are stored
     * doesn't take parameters because it will be checking its own private data member called data
     * @return a list of all games
     */
    public ListGamesResult listGames(String authToken) throws DataAccessException {
        try {
            // Step 1: Verify authorization using authToken
            AuthToken token = database.GetAuthToken(authToken);
            if (token == null) {
                throw new Exception("Unauthorized");
            }

            // Step 2: Retrieve all games from database
            List<Game> allGames = new ArrayList<>(database.FindAllGames());

            return new ListGamesResult(allGames);

        } catch (Exception e) {
            // Handle exceptions and return appropriate error result
            throw new DataAccessException("Error: " + e.getMessage());
        }
    }


    /** HTTP Method: POST
     * Creates a new game
     * @param gameName
     * @return
     */
    public CreateGameResult createGame(String gameName, String authToken) throws DataAccessException {
        try {
            // Step 1: Verify authorization using authToken
            AuthToken token = database.GetAuthToken(authToken);
            if (token == null) {
                throw new Exception("Unauthorized");
            }

            // Step 2: Create a new game
            Game newGame = database.CreateGame(gameName);

            return new CreateGameResult(newGame.gameID());

        } catch (Exception e) {
            // Handle exceptions and return appropriate error result
            throw new DataAccessException("Error: " + e.getMessage());
        }
    }

    /** HTTP Method: PUT
     * Verifies that the game exists. If color is specified, add the caller as the requested color to the game.
     * If no color is specified then the user is joined as an observer
     *
     * @return the game that was joined
     */
    public Object joinGame(int gameID, String playerColor) {
        return null;
    }


}
