package passoffTests.serverTests.serviceTests;

import dao.DAOInterface;
import dao.MainMemoryDAO;
import dataAccess.UnauthorizedException;
import org.junit.jupiter.api.*;
import dataAccess.DataAccessException;
import requests.RegisterRequest;
import results.UserAuthResult;
import services.UserService;

public class UserServiceTests {
    DAOInterface database = new MainMemoryDAO();
    UserService userService = new UserService(database);

    @Test
    @DisplayName("Register user")
    public void successRegister() throws Exception {
        RegisterRequest request = new RegisterRequest("Adam", "Turner", "myemail@email.com");
        UserAuthResult result = userService.register(request);
        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("Attempt register user, missing info")
    public void noUsernameRegister() throws Exception {
        RegisterRequest request = new RegisterRequest("Adam", null, "myemail@email.com");
        Assertions.assertThrows(UnauthorizedException.class,() -> userService.register(request));
    }

}
