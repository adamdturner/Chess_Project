package passoffTests.serverTests;

import dao.DAOInterface;
import dao.SQLDAO;
import dataAccess.DataAccessException;
import dataAccess.UnauthorizedException;
import models.Game;
import models.User;
import models.AuthToken;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DataAccessTests {

    static DAOInterface database;
    static int gameID;
    static User user1;
    static User user2;
    static User user3;


    @BeforeAll
    public static void setup() throws UnauthorizedException, DataAccessException {
        user1 = new User("Adam","mypassword","myemail@gmail.com");
        user2 = new User("Timmy","timPassword","tim@email.com");
        user3 = new User("Benny","benPassword","ben@email.com");
        database = new SQLDAO();
    }

    @Test
    @Order(1)
    @DisplayName("Success clearing database")
    public void clearDatabase() {
        Assertions.assertDoesNotThrow(() -> database.clearAll());
    }

    @Test
    @Order(2)
    @DisplayName("Success adding user")
    public void addUserSuccess() throws Exception {
        Assertions.assertDoesNotThrow(() -> database.addUser(user1));
    }

    @Test
    @Order(3)
    @DisplayName("Fail adding user")
    public void addUserFail() throws Exception {
        Assertions.assertThrows(NullPointerException.class, () -> database.addUser(null));
    }

    @Test
    @Order(4)
    @DisplayName("Success finding user")
    public void successGetUser() throws Exception {
        Assertions.assertNotNull(database.getUser(user1.username()));
    }

    @Test
    @Order(5)
    @DisplayName("Fail finding user, user doesn't exist")
    public void failGetUser() throws Exception {
        Assertions.assertNull(database.getUser("Joe"));
    }

    @Test
    @Order(6)
    @DisplayName("Success creating game")
    public void successCreateGame() throws Exception {
        Assertions.assertDoesNotThrow(() -> database.createGame("GameOne"));
        gameID = database.createGame("GameOne").gameID();
    }

    @Test
    @Order(7)
    @DisplayName("Fail creating game, game already exists")
    public void failCreateGame() throws Exception {
        Assertions.assertThrows(DataAccessException.class, () -> database.createGame(null));
    }

    @Test
    @Order(8)
    @DisplayName("Success finding game")
    public void successFindGame() throws Exception {
        Assertions.assertNotNull(database.findGame(gameID));
    }

    @Test
    @Order(9)
    @DisplayName("Fail to find game, wrong ID")
    public void failFindGame() throws Exception {
        Assertions.assertNull(database.findGame(12345));
    }

    @Test
    @Order(10)
    @DisplayName("Success adding white player")
    public void successWhiteAdd() throws Exception {
        database.addUser(user2);
        database.addUser(user3);
        Assertions.assertDoesNotThrow(() -> database.setWhitePlayer(gameID, user2.username()));
    }

    @Test
    @Order(11)
    @DisplayName("Fail adding white player, wrong username")
    public void failWhiteAdd() throws Exception {
        Assertions.assertThrows(DataAccessException.class, () -> database.setWhitePlayer(gameID, "wrongName"));
    }

    @Test
    @Order(12)
    @DisplayName("Success adding black player")
    public void successBlackAdd() throws Exception {
        Assertions.assertDoesNotThrow(() -> database.setBlackPlayer(gameID, user3.username()));
    }

    @Test
    @Order(13)
    @DisplayName("Fail adding black player")
    public void failBlackAdd() throws Exception {
        Assertions.assertThrows(DataAccessException.class, () -> database.setBlackPlayer(gameID, "wrongName"));
    }

    @Test
    @Order(14)
    @DisplayName("Successfully add observer")
    public void successAddObserver() throws Exception {
        String observerName = "ObserverUser";
        database.addUser(new User(observerName, "observerPass", "observer@email.com"));
        Assertions.assertDoesNotThrow(() -> database.addObserver(gameID, observerName));
    }


    @Test
    @Order(15)
    @DisplayName("Fail to add observer")
    public void failAddObserver() throws Exception {
        Assertions.assertThrows(DataAccessException.class, () -> database.addObserver(gameID, "nonExistentUser"));
    }


    @Test
    @Order(16)
    @DisplayName("Successfully create authToken")
    public void successCreateToken() throws Exception {
        AuthToken token = Assertions.assertDoesNotThrow(() -> database.createAuthToken(user1.username()));
        Assertions.assertNotNull(token);
    }


    @Test
    @Order(17)
    @DisplayName("Fail to create authToken")
    public void failCreateToken() throws Exception {
        Assertions.assertThrows(DataAccessException.class, () -> database.createAuthToken("nonExistentUser"));
    }


    @Test
    @Order(18)
    @DisplayName("Successfully retrieve authToken")
    public void successGetToken() throws Exception {
        AuthToken token = database.createAuthToken("Adam");
        Assertions.assertEquals(token, database.getAuthToken(token.authToken()));
    }


    @Test
    @Order(19)
    @DisplayName("Fail to retrieve authToken")
    public void failGetToken() throws Exception {
        Assertions.assertNull(database.getAuthToken("invalidToken"));
    }


    @Test
    @Order(20)
    @DisplayName("Successfully delete authToken")
    public void successDeleteToken() throws Exception {
        AuthToken token = database.createAuthToken("Adam");
        Assertions.assertDoesNotThrow(() -> database.deleteAuthToken(token.authToken()));
    }


    @Test
    @Order(21)
    @DisplayName("Fail to delete authToken")
    public void failDeleteToken() throws Exception {
        Assertions.assertThrows(DataAccessException.class, () -> database.deleteAuthToken("invalidToken"));
    }


    @Test
    @Order(22)
    @DisplayName("Success listing all games")
    public void successListGames() throws Exception {
        Assertions.assertDoesNotThrow(() -> database.findAllGames());
    }

    @Test
    @Order(23)
    @DisplayName("List games when no games exist")
    public void failListGames() throws Exception {
        database.clearAll();
        List<Game> myList = new ArrayList<>(); // empty arrayList
        Assertions.assertEquals(myList, database.findAllGames(), "database games list should be empty");
    }


}
