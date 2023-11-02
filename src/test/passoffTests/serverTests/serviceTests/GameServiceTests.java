package passoffTests.serverTests.serviceTests;

import dao.DAOInterface;
import dao.MainMemoryDAO;
import dataAccess.UnauthorizedException;
import handlers.GameHandler;
import models.AuthToken;
import models.User;
import org.junit.jupiter.api.*;
import requests.LoginRequest;
import requests.RegisterRequest;
import results.CreateGameResult;
import results.UserAuthResult;
import services.AuthenticationService;
import services.GameService;
import services.UserService;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameServiceTests {

    static DAOInterface database = new MainMemoryDAO();
    static GameService gameService = new GameService(database);
    static String authToken;
    static String gameName;
    static int gameID;
    static UserService userService = new UserService(database);
    static AuthenticationService authService = new AuthenticationService(database);

    static RegisterRequest request1;
    static RegisterRequest request2;
    static LoginRequest loginRequest1;
    static LoginRequest loginRequest2;


    @BeforeAll
    public static void setup() throws UnauthorizedException {
        // register 2 users
        request1 = new RegisterRequest("Adam", "Turner", "myemail@email.com");
        request2 = new RegisterRequest("Bob", "Turner", "other@email.com");
        userService.register(request1);
        userService.register(request2);
        // login the 2 users
        loginRequest1 = new LoginRequest("Adam", "Turner");
        loginRequest2 = new LoginRequest("Bob", "Turner");
        // get the authToken from the first user login
        UserAuthResult result = authService.login(loginRequest1);
        authToken = result.authToken();
        // set the name of the game
        gameName = "nameofgame";
    }

    @Test
    @Order(1)
    @DisplayName("Create game success")
    public void successCreateGame() throws Exception {
        // use the authToken and game name to create a game
        CreateGameResult createResult = gameService.createGame(gameName,authToken);
        // set the gameID variable so that we can use it for the join game tests
        gameID = createResult.getGameID();
        Assertions.assertNotNull(createResult);
    }

    @Test
    @Order(2)
    @DisplayName("Unauthorized game creation")
    public void failLogin() throws Exception {
        // missing authToken
        Assertions.assertThrows(Exception.class, () -> gameService.createGame(gameName,null));
    }

    @Test
    @Order(3)
    @DisplayName("Join game success")
    public void successJoinGame() throws Exception {
        Assertions.assertNotNull(gameService.joinGame(authToken,null,gameID));
    }

    @Test
    @Order(4)
    @DisplayName("Unauthorized to join game")
    public void failJoinGame() throws Exception {
        Assertions.assertThrows(Exception.class, () -> gameService.joinGame(authToken,"WHITE",0));
    }

    @Test
    @Order(5)
    @DisplayName("List games success")
    public void successListGames() throws Exception {
        Assertions.assertNotNull(gameService.listGames(authToken));
    }

    @Test
    @Order(6)
    @DisplayName("Unauthorized to list games")
    public void failListGames() throws Exception {
        Assertions.assertThrows(Exception.class, () -> gameService.listGames("wrong token"));
    }

}
