import models.AuthToken;
import models.Game;
import org.junit.jupiter.api.*;
import ui.PostLoginFacade;
import ui.PreLoginFacade;

import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FacadeTests {

    private PreLoginFacade preLoginFacade;
    private PostLoginFacade postLoginFacade;
    static private AuthToken testAuthToken;
    static private int gameID = 0;
    static private int gameID2 = 0;

    @BeforeEach
    public void setUp() {
        // Use a test server URL here
        preLoginFacade = new PreLoginFacade("http://localhost:8080");
        postLoginFacade = new PostLoginFacade("http://localhost:8080");
    }

    @Test
    @Order(1)
    @DisplayName("clear database success")
    public void successClear() {
        Assertions.assertTrue(preLoginFacade.clearAll());
    }

    @Test
    @Order(2)
    @DisplayName("Register user success")
    public void successRegister() {
        // This will actually send a request to your server
        testAuthToken = preLoginFacade.register("testUser", "password123", "test@example.com");

        // Perform assertions based on the expected behavior of your actual server
        Assertions.assertNotNull(testAuthToken);
        // Add more detailed assertions here based on the actual token received
    }

    @Test
    @Order(2)
    @DisplayName("failed register attempt")
    public void failRegister() {
        // This will actually send a request to your server
        AuthToken authToken = preLoginFacade.register("testUser", "password123", "test@example.com");

        // Perform assertions based on the expected behavior of your actual server
        Assertions.assertNull(authToken);
        // Add more detailed assertions here based on the actual response received
    }

    @Test
    @Order(3)
    @DisplayName("Logout Success")
    public void logoutSuccess() {
        // Assume logout is always successful
        boolean result = postLoginFacade.logout(testAuthToken);

        Assertions.assertTrue(result);
    }

    @Test
    @Order(4)
    @DisplayName("Logout Failure")
    public void logoutFailure() {
        // Assume logout fails due to invalid token or network issue
        // In a real test, you would simulate this condition
        boolean result = postLoginFacade.logout(new AuthToken("invalidToken", "testUser"));

        Assertions.assertFalse(result);
    }

    @Test
    @Order(5)
    @DisplayName("Login user success")
    public void successLogin() {
        // This will actually send a request to your server
        testAuthToken = preLoginFacade.login("testUser", "password123");

        // Perform assertions based on the expected behavior of your actual server
        Assertions.assertNotNull(testAuthToken);
        // Add more detailed assertions here based on the actual token received
    }

    @Test
    @Order(6)
    @DisplayName("Fail login attempt")
    public void failLogin() {
        // This will actually send a request to your server
        AuthToken authToken = preLoginFacade.login("testUser", "wrongPassword");

        // Perform assertions based on the expected behavior of your actual server
        Assertions.assertNull(authToken);
        // Add more detailed assertions here based on the actual response received
    }

    @Test
    @Order(7)
    @DisplayName("Create Game Success")
    public void createGameSuccess() {
        // Assume game creation is successful and returns a game ID of 1
        gameID = postLoginFacade.createGame(testAuthToken, "testGame");

        Assertions.assertNotEquals(0, gameID); // gameId 0 would indicate a failure
    }

    @Test
    @Order(8)
    @DisplayName("Create Game Failure")
    public void createGameFailure() {
        // Assume game creation fails due to server error or invalid input
        // In a real test, you would simulate this condition
        int gameId = postLoginFacade.createGame(testAuthToken, "");

        Assertions.assertEquals(0, gameId);
    }

    @Test
    @Order(9)
    @DisplayName("List Games Success")
    public void listGamesSuccess() {
        gameID = postLoginFacade.createGame(testAuthToken, "game1");
        gameID2 = postLoginFacade.createGame(testAuthToken, "game2");

        // Assume listing games is successful and returns a non-empty list
        List<Game> games = postLoginFacade.listGames(testAuthToken);

        Assertions.assertNotNull(games);
        Assertions.assertFalse(games.isEmpty());
    }

    @Test
    @Order(10)
    @DisplayName("List Games Failure")
    public void listGamesFailure() {
        // Assume listing games fails and returns null or an empty list
        List<Game> games = postLoginFacade.listGames(new AuthToken("invalidToken", "testUser"));

        Assertions.assertNull(games); // Or assert that the list is empty
    }

    @Test
    @Order(11)
    @DisplayName("Join Game Success")
    public void joinGameSuccess() {
        // Assume joining game is successful and returns a Game object
        Game game = postLoginFacade.joinGame(testAuthToken, gameID, "BLACK");

        Assertions.assertNotNull(game);
    }

    @Test
    @Order(12)
    @DisplayName("Join Game Failure")
    public void joinGameFailure() {
        // Assume joining game fails due to game being full or invalid game ID
        Game game = postLoginFacade.joinGame(testAuthToken, 999, "WHITE");

        Assertions.assertNull(game);
    }

    @Test
    @Order(13)
    @DisplayName("Join Observer Success")
    public void joinObserverSuccess() {
        // Assume joining as observer is successful and returns a Game object
        Game game = postLoginFacade.joinObserver(testAuthToken, gameID2);

        Assertions.assertNotNull(game);
    }

    @Test
    @Order(14)
    @DisplayName("Join Observer Failure")
    public void joinObserverFailure() {
        // Assume joining as observer fails due to invalid game ID
        Game game = postLoginFacade.joinObserver(testAuthToken, 999);

        Assertions.assertNull(game);
    }


}
