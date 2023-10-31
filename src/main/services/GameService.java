package services;

import chess.ChessGame;
import dao.DAOInterface;
import models.Game;

import java.util.Collection;

/**
 * Service class used to implement the list game, create game and join game endpoints
 */
public class GameService {

    /**
     * Private DAOInterface object. Use the interface because the services don't need
     * to know where the data is stored.
     */
    private final DAOInterface data;

    /**
     * Constructor
     * @param data
     */
    public GameService(DAOInterface data) {
        this.data = data;
    }

    //List Games
    //Note that whiteUsername and blackUsername may be null.
    //
    //property:	                value:
    //Description	            Gives a list of all games.
    //URL path	                /game
    //HTTP Method	            GET
    //Headers	                authorization: <authToken>
    //Success response	        [200] { "games": [{"gameID": 1234, "whiteUsername":"", "blackUsername":"", "gameName:""} ]}
    //Failure response	        [401] { "message": "Error: unauthorized" }
    //Failure response	        [500] { "message": "Error: description" }

    /** HTTP Method: GET
     * Gives a list of games in the database wherever they are stored
     * doesn't take parameters because it will be checking its own private data member called data
     * @return a list of all games
     */
    public Collection<Game> ListGames(){ return null; }


    //Create Game
    //property:	                value:
    //Description	            Creates a new game.
    //URL path	                /game
    //HTTP Method	            POST
    //Headers	                authorization: <authToken>
    //Body	                    { "gameName":"" }
    //Success response	        [200] { "gameID": 1234 }
    //Failure response	        [400] { "message": "Error: bad request" }
    //Failure response	        [401] { "message": "Error: unauthorized" }
    //Failure response	        [500] { "message": "Error: description" }

    /** HTTP Method: POST
     * Creates a new game
     * @param gameName
     * @return
     */
    public Game newGame(String gameName) { return null; }


    //Join Game
    //property:	                value:
    //Description	            Verifies that the specified game exists, and, if a color is specified,
    //                          adds the caller as the requested color to the game.
    //                          If no color is specified the user is joined as an observer.
    //                          This request is idempotent.
    //URL path	                /game
    //HTTP Method	            PUT
    //Headers	                authorization: <authToken>
    //Body	                    { "playerColor":"WHITE/BLACK", "gameID": 1234 }
    //Success response	        [200]
    //Failure response	        [400] { "message": "Error: bad request" }
    //Failure response	        [401] { "message": "Error: unauthorized" }
    //Failure response	        [403] { "message": "Error: already taken" }
    //Failure response	        [500] { "message": "Error: description" }

    /** HTTP Method: PUT
     * Verifies that the game exists. If color is specified, add the caller as the requested color to the game.
     * If no color is specified then the user is joined as an observer
     * @param gameName
     * @param teamColor
     * @param gameID
     * @return the game that was joined
     */
    public Game joinGame(String gameName, ChessGame.TeamColor teamColor, int gameID) { return null; }


}
