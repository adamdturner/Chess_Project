package services;

import dao.DAOInterface;
import dataAccess.DataAccessException;
import dataAccess.UnauthorizedException;
import models.AuthToken;
import models.Game;
import results.CreateGameResult;
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
    public ListGamesResult listGames(String authToken) throws UnauthorizedException, DataAccessException {
        try {
            // Step 1: Verify authorization using authToken
            AuthToken token = database.GetAuthToken(authToken);
            if (token == null) {
                throw new UnauthorizedException("Unauthorized");
            }

            // Step 2: Retrieve all games from database
            List<Game> allGames = new ArrayList<>(database.FindAllGames());

            return new ListGamesResult(allGames);

        } catch (Exception e) {
            // Handle exceptions and return appropriate error result
            throw new UnauthorizedException("Unauthorized");
        }
    }


    /** HTTP Method: POST
     * Creates a new game
     * @param gameName
     * @return
     */
    public CreateGameResult createGame(String gameName, String authToken) throws DataAccessException, UnauthorizedException {
        // Ensure valid gameName here if needed
        // edit this to have any other checks to make sure the gameName is valid
        if (gameName.isEmpty()) {
            throw new DataAccessException("Error: Invalid game name");
        }

        // Step 1: Verify authorization using authToken
        AuthToken token = database.GetAuthToken(authToken);
        if (token == null) {
            throw new UnauthorizedException("Unauthorized");
        }

        // Step 2: Create a new game
        Game newGame = database.CreateGame(gameName);

        return new CreateGameResult(newGame.gameID());
    }

    /** HTTP Method: PUT
     * Verifies that the game exists. If color is specified, add the caller as the requested color to the game.
     * If no color is specified then the user is joined as an observer
     *
     * @return the game that was joined
     */
    public SuccessResult joinGame(String authToken, String playerColor, int gameID) throws UnauthorizedException, Exception, DataAccessException {
        try {
            // Step 1: Verify authorization using authToken
            AuthToken token = database.GetAuthToken(authToken);
            if (token == null) {
                throw new UnauthorizedException("Error: Unauthorized");
            }

            // Step 2: Ensure the game exists
            Game existingGame = database.FindGame(gameID);
//            if (existingGame == null) {
//                throw new DataAccessException("Error: Game not found");
//            }

            // Step 3: Check and assign player based on color or add as observer
            if (playerColor == null || playerColor.isEmpty()) {
                // Add as an observer
                database.AddObserver(gameID, token.username());
            } else if ("WHITE".equalsIgnoreCase(playerColor)) {
                if (existingGame.whiteUsername() != null) {
                    throw new Exception("Error: already taken");
                }
                database.SetWhitePlayer(gameID, token.username());
            } else if ("BLACK".equalsIgnoreCase(playerColor)) {
                if (existingGame.blackUsername() != null) {
                    throw new Exception("Error: already taken");
                }
                database.SetBlackPlayer(gameID, token.username());
            } else {
                throw new Exception("Invalid player color");
            }

            return new SuccessResult(true);

        } catch (UnauthorizedException ue) {
            throw ue; // Pass it up to be caught and processed by the handler
        } catch (DataAccessException e) {
            throw new DataAccessException("Error: " + e.getMessage());
        } catch (Exception e) {
            throw new Exception("Error: already taken");
        }
    }










}
