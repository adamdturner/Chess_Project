package passoffTests.serverTests.serviceTests;
import chess.ChessGame;
import org.junit.jupiter.api.*;
import passoffTests.TestFactory;
import passoffTests.obfuscatedTestClasses.TestServerFacade;
import passoffTests.testClasses.TestModels;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthServiceTests {

    @Test
    @Order(1)
    @DisplayName("Normal User Login")
    public void successLogin() {}


    @Test
    @Order(2)
    @DisplayName("Invalid User Login")
    public void invalidLogin() {}


    @Test
    @Order(3)
    @DisplayName("Wrong Password Login")
    public void wrongPasswordLogin() {}


}
