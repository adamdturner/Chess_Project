package server;

import dao.DAOInterface;
import dao.MainMemoryDAO;
import handlers.AuthenticationHandler;
import handlers.ClearApplicationHandler;
import handlers.UserHandler;
import services.AuthenticationService;
import services.ClearApplicationService;

import services.UserService;
import spark.Spark;
import static spark.Spark.delete;
import static spark.Spark.post;


public class MyServer {
    private final DAOInterface database;
    private final ClearApplicationService clearService;
    private final ClearApplicationHandler clearHandler;

    private final UserService userService;
    private final UserHandler userHandler;

    private final AuthenticationHandler authHandler;
    private final AuthenticationService authService;


    public static void main(String[] args) throws Exception {
        // Switch the DAO by uncommenting/commenting the desired line:
        // new Server(new SQLDAO()).run(8080);
        new MyServer(new MainMemoryDAO()).run(8080);
    }

    public MyServer(DAOInterface database) {
        this.database = database;
        this.clearService = new ClearApplicationService(database);
        this.clearHandler = new ClearApplicationHandler(clearService);
        this.userService = new UserService(database);
        this.userHandler = new UserHandler(userService);
        this.authService = new AuthenticationService(database);
        this.authHandler = new AuthenticationHandler(database);
    }

    public void run(int port) {
        // Initialize your web server here, set port, etc.
        // Register the clearHandler to handle specific HTTP requests.

        Spark.port(port);
        delete("/db", clearHandler::handleClearRequest);
        post("/user",userHandler::handleRegisterRequest);
        //post("/session", authHandler::handleLoginRequest);
        //delete("/session", authHandler::handleLogoutRequest);

        System.out.printf("Running server on port %d\n", port);
    }

}
