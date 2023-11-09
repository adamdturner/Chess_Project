package passoffTests.serverTests.serviceTests;

import dao.DAOInterface;
import dao.MainMemoryDAO;
import dao.SQLDAO;
import org.junit.jupiter.api.*;
import dataAccess.DataAccessException;
import services.ClearApplicationService;

import java.sql.SQLException;

public class ClearServiceTests {

//    DAOInterface database = new MainMemoryDAO();
    DAOInterface database = new SQLDAO();
    ClearApplicationService clearService = new ClearApplicationService(database);

    public ClearServiceTests() throws SQLException, DataAccessException {
    }

    @Test
    @DisplayName("Clear database")
    public void successClear() throws Exception {
        Assertions.assertDoesNotThrow(() -> clearService.ClearDatabase());
    }

}

