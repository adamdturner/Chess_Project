package passoffTests.serverTests.serviceTests;

import dao.DAOInterface;
import dao.MainMemoryDAO;
import dataAccess.UnauthorizedException;
import handlers.AuthenticationHandler;
import models.AuthToken;
import models.User;
import org.junit.jupiter.api.*;
import requests.LoginRequest;
import results.SuccessResult;
import results.UserAuthResult;
import services.AuthenticationService;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthServiceTests {

    DAOInterface database = new MainMemoryDAO();
    AuthenticationService authService = new AuthenticationService(database);
    String authToken;

    @Test
    @Order(1)
    @DisplayName("Login user success")
    public void successLogin() throws Exception {
        database.AddUser(new User("AdamT", "myPassword123", "myemail@email.com"));
        LoginRequest request = new LoginRequest("AdamT", "myPassword123");
        UserAuthResult result = authService.login(request);
        authToken = result.authToken();
        Assertions.assertNotNull(result);
    }

    @Test
    @Order(2)
    @DisplayName("Unauthorized login attempt")
    public void failLogin() throws Exception {
        database.AddUser(new User("AdamT", "myPassword123", "myemail@email.com"));
        LoginRequest request = new LoginRequest("AdamT", null);
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
