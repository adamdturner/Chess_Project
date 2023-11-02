package passoffTests.serverTests.serviceTests;

import dao.DAOInterface;
import dao.MainMemoryDAO;
import org.junit.jupiter.api.*;
import dataAccess.DataAccessException;
import services.ClearApplicationService;

public class ClearServiceTests {

    DAOInterface database = new MainMemoryDAO();
    ClearApplicationService clearService = new ClearApplicationService(database);

    @Test
    @DisplayName("Clear database")
    public void successClear() throws Exception {
        Assertions.assertDoesNotThrow(() -> clearService.ClearDatabase());
    }

}

