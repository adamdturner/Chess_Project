package passoffTests.serverTests.serviceTests;

import dao.DAOInterface;
import dao.SQLDAO;
import dataAccess.DataAccessException;
import models.AuthToken;
import models.User;
import org.junit.jupiter.api.*;
import requests.LoginRequest;
import results.UserAuthResult;
import services.AuthenticationService;

import java.sql.SQLException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthServiceTests {

    //    DAOInterface database = new MainMemoryDAO();
    DAOInterface database = new SQLDAO();
    AuthenticationService authService = new AuthenticationService(database);
    String authToken;

    public AuthServiceTests() throws SQLException, DataAccessException {
    }

    @Test
    @Order(1)
    @DisplayName("Login user success")
    public void successLogin() throws Exception {
        database.addUser(new User("AdamT", "myPassword123", "myemail@email.com"));
        LoginRequest request = new LoginRequest("AdamT", "myPassword123");
        UserAuthResult result = authService.login(request);
        authToken = result.authToken();
        Assertions.assertNotNull(result);
    }

    @Test
    @Order(2)
    @DisplayName("Unauthorized login attempt")
    public void failLogin() throws Exception {
        database.addUser(new User("AdamTurner", "myPassword123", "myemail@email.com"));
        LoginRequest request = new LoginRequest("AdamTurner", null);
        Assertions.assertNull(authService.login(request));
    }


    @Test
    @Order(3)
    @DisplayName("Logout user success")
    public void successLogout() throws Exception {
        Assertions.assertDoesNotThrow(() -> authService.logout(authToken));
    }

    @Test
    @Order(4)
    @DisplayName("Attempt to Logout twice")
    public void failLogout() throws Exception {
        Assertions.assertFalse(authService.logout(authToken));
    }

}
