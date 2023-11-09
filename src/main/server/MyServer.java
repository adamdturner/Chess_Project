package server;

import dao.DAOInterface;
import dao.MainMemoryDAO;
import dao.SQLDAO;
import handlers.AuthenticationHandler;
import handlers.ClearApplicationHandler;
import handlers.GameHandler;
import handlers.UserHandler;
import services.AuthenticationService;
import services.ClearApplicationService;

import services.GameService;
import services.UserService;
import spark.*;

import static spark.Spark.*;


public class MyServer {
    final DAOInterface database;    // database
    final ClearApplicationService clearService; // clear
    final ClearApplicationHandler clearHandler;

    final UserService userService;  // register
    final UserHandler userHandler;

    final AuthenticationHandler authHandler;    // login / logout
    final AuthenticationService authService;

    final GameHandler gameHandler;  // list / create / join
    final GameService gameService;

    public MyServer(DAOInterface database) {
        this.database = database;
        clearService = new ClearApplicationService(database);
        clearHandler = new ClearApplicationHandler(clearService);
        userService = new UserService(database);
        userHandler = new UserHandler(userService);
        authService = new AuthenticationService(database);
        authHandler = new AuthenticationHandler(authService);
        gameService = new GameService(database);
        gameHandler = new GameHandler(gameService);
    }

    public static void main(String[] args) throws Exception {
        // Switch the DAO by uncommenting/commenting the desired line:
        new MyServer(new SQLDAO()).run(8080);
//        new MyServer(new MainMemoryDAO()).run(8080);
    }


    public void run(int port) {
        // Initialize web server here, set port, etc.
        // Register the handlers to handle specific HTTP requests.

        Spark.port(port);
        Spark.externalStaticFileLocation("web");
        delete("/db", clearHandler::handle);    // handle clear request
        post("/user",userHandler::handle);  // handle register request
        post("/session", authHandler::handle);  // handle login request
        delete("/session", authHandler::handle);   // handle logout request
        get("/game", gameHandler::handle); // handle list game request
        post("/game", gameHandler::handle);  // handle create game request
        put("/game", gameHandler::handle); // handle join game request

        System.out.printf("Running server on port %d\n", port);
    }

}