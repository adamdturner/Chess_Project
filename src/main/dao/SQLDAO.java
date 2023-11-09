package dao;

import chess.ChessGame;
import chess.MyGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataAccess.DataAccessException;
import dataAccess.Database;
import models.AuthToken;
import models.Game;
import models.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SQLDAO implements DAOInterface {

    private Database database;

    public SQLDAO() throws SQLException, DataAccessException {
        database = new Database();
        configureDatabase();
    }

    void configureDatabase() throws SQLException, DataAccessException {
        var conn = database.getConnection();
        try {
            var createDbStatement = conn.prepareStatement("CREATE DATABASE IF NOT EXISTS " + Database.DB_NAME);
            createDbStatement.executeUpdate();

            conn.setCatalog(Database.DB_NAME);

            var createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                username VARCHAR(255) NOT NULL,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL,
                PRIMARY KEY (username)
            )""";

            var createTokensTable = """
            CREATE TABLE IF NOT EXISTS tokens (
                authToken VARCHAR(255) NOT NULL,
                username VARCHAR(255) NOT NULL,
                PRIMARY KEY (authToken)
            )""";

            var createGamesTable = """
            CREATE TABLE IF NOT EXISTS games (
                gameID INT NOT NULL AUTO_INCREMENT,
                whiteUsername VARCHAR(255) DEFAULT NULL,
                blackUsername VARCHAR(255) DEFAULT NULL,
                gameName VARCHAR(255) DEFAULT NULL,
                game longtext NOT NULL,
                PRIMARY KEY (gameID)
            )""";

            var createObserversTable = """
            CREATE TABLE IF NOT EXISTS observers (
                gameID INT NOT NULL,
                username VARCHAR(255) NOT NULL,
                PRIMARY KEY (gameID, username)
            )""";


            try (var createTableStatement = conn.prepareStatement(createUsersTable)) {
                createTableStatement.executeUpdate();
            }
            try (var createTableStatement = conn.prepareStatement(createTokensTable)) {
                createTableStatement.executeUpdate();
            }
            try (var createTableStatement = conn.prepareStatement(createGamesTable)) {
                createTableStatement.executeUpdate();
            }
            try (var createTableStatement = conn.prepareStatement(createObserversTable)) {
                createTableStatement.executeUpdate();
            } finally {
                database.returnConnection(conn);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }




    @Override
    public void clearAll() throws DataAccessException {
        var conn = database.getConnection();
        try {
            var truncateUsers = "DELETE FROM users";            // could write "TRUNCATE TABLE users" instead
            var truncateTokens = "DELETE FROM tokens";          //                     "
            var truncateGames = "DELETE FROM games";            //                     "
            var truncateObservers = "DELETE FROM observers";    //                     "

            try (var statement = conn.prepareStatement(truncateUsers)) {
                statement.executeUpdate();
            }
            try (var statement = conn.prepareStatement(truncateTokens)) {
                statement.executeUpdate();
            }
            try (var statement = conn.prepareStatement(truncateGames)) {
                statement.executeUpdate();
            }
            try (var statement = conn.prepareStatement(truncateObservers)) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing all tables: " + e.getMessage());
        } finally {
            database.returnConnection(conn);
        }
    }

    @Override
    public void addUser(User user) throws DataAccessException {
        String insertUserSQL = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        var conn = database.getConnection();
        try (var statement = conn.prepareStatement(insertUserSQL)) {
            statement.setString(1, user.username());
            statement.setString(2, user.password());
            statement.setString(3, user.email());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error adding user: " + e.getMessage());
        } finally {
            database.returnConnection(conn);
        }
    }

    @Override
    public User getUser(String username) throws DataAccessException {
        String getUserSQL = "SELECT * FROM users WHERE username = ?";
        var conn = database.getConnection();
        try (var statement = conn.prepareStatement(getUserSQL)) {
            statement.setString(1, username);
            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new User(resultSet.getString("username"), resultSet.getString("password"), resultSet.getString("email"));
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error getting user: " + e.getMessage());
        } finally {
            database.returnConnection(conn);
        }
    }


    @Override
    public void setWhitePlayer(int gameID, String username) throws DataAccessException {
        String sql = "UPDATE games SET whiteUsername = ? WHERE gameID = ?";
        var conn = database.getConnection();
        try (var stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setInt(2, gameID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error setting white player: " + e.getMessage());
        } finally {
            database.returnConnection(conn);
        }
    }


    @Override
    public void setBlackPlayer(int gameID, String username) throws DataAccessException {
        String sql = "UPDATE games SET blackUsername = ? WHERE gameID = ?";
        var conn = database.getConnection();
        try (var stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setInt(2, gameID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error setting black player: " + e.getMessage());
        } finally {
            database.returnConnection(conn);
        }
    }


    @Override
    public Game createGame(String gameName) throws DataAccessException {
        ChessGame chessGame = new MyGame(); // Create a new ChessGame instance
        Gson gson = new GsonBuilder().create(); // Serialize the object
        String json = gson.toJson(chessGame);

        String sql = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";

        var conn = database.getConnection();
        try (var stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Set whiteUsername and blackUsername to null initially
            stmt.setNull(1, Types.VARCHAR);  // whiteUsername
            stmt.setNull(2, Types.VARCHAR);  // blackUsername
            stmt.setString(3, gameName);    // gameName
            stmt.setString(4, json);        // ChessGame game as a json string
            stmt.executeUpdate();

            try (var generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int gameID = generatedKeys.getInt(1);
                    // Return the new Game with whiteUsername and blackUsername as null
                    return new Game(gameID, null, null, gameName, chessGame);
                } else {
                    throw new DataAccessException("Creating game failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error creating game: " + e.getMessage());
        } finally {
            database.returnConnection(conn);
        }
    }

    @Override
    public Game findGame(int gameID) throws DataAccessException {
        String sql = "SELECT * FROM games WHERE gameID = ?";
        var conn = database.getConnection();
        try (var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameID);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String json = rs.getString("game");
                    Gson gson = new GsonBuilder().create(); // Deserialize the object
                    ChessGame chessGame = gson.fromJson(json, MyGame.class); // Assuming MyGame implements ChessGame

                    return new Game(
                            gameID,
                            rs.getString("whiteUsername"),
                            rs.getString("blackUsername"),
                            rs.getString("gameName"),
                            chessGame);
                } else {
                    return null; // Game not found
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding game: " + e.getMessage());
        } finally {
            database.returnConnection(conn);
        }
    }



    @Override
    public List<Game> findAllGames() throws DataAccessException {
        List<Game> games = new ArrayList<>();
        String sql = "SELECT * FROM games";

        var conn = database.getConnection();
        try (var stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            Gson gson = new GsonBuilder().create(); // For deserializing JSON

            while (rs.next()) {
                int gameID = rs.getInt("gameID");
                String whiteUsername = rs.getString("whiteUsername");
                String blackUsername = rs.getString("blackUsername");
                String gameName = rs.getString("gameName");
                String json = rs.getString("game");

                // Deserialize the JSON string back into a ChessGame object
                ChessGame chessGame = gson.fromJson(json, MyGame.class);

                // Create a Game object and add it to the list
                Game game = new Game(gameID, whiteUsername, blackUsername, gameName, chessGame);
                games.add(game);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving all games: " + e.getMessage());
        } finally {
            database.returnConnection(conn);
        }

        return games;
    }



    @Override
    public void addObserver(int gameID, String username) throws DataAccessException {
        String sql = "INSERT INTO observers (gameID, username) VALUES (?, ?)";

        var conn = database.getConnection();
        try (var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameID);
            stmt.setString(2, username);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DataAccessException("Adding observer failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error adding observer to the database: " + e.getMessage());
        } finally {
            database.returnConnection(conn);
        }
    }



    @Override
    public AuthToken createAuthToken(String username) throws DataAccessException {
        String token = UUID.randomUUID().toString(); // Generates a unique token
        String insertTokenSQL = "INSERT INTO tokens (authToken, username) VALUES (?, ?)";
        var conn = database.getConnection();
        try (var statement = conn.prepareStatement(insertTokenSQL)) {
            statement.setString(1, token);
            statement.setString(2, username);
            statement.executeUpdate();
            return new AuthToken(token, username);
        } catch (SQLException e) {
            throw new DataAccessException("Error creating auth token: " + e.getMessage());
        } finally {
            database.returnConnection(conn);
        }
    }

    @Override
    public AuthToken getAuthToken(String authToken) throws DataAccessException {
        String sql = "SELECT username FROM tokens WHERE authToken = ?";
        var conn = database.getConnection();
        try (var statement = conn.prepareStatement(sql)) {

            statement.setString(1, authToken);
            try (var rs = statement.executeQuery()) {
                if (rs.next()) {
                    return new AuthToken(authToken, rs.getString("username"));
                } else {
                    return null; // or throw an exception based on how I want to handle this case
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error getting auth token: " + e.getMessage());
        } finally {
            database.returnConnection(conn);
        }
    }

    @Override
    public void deleteAuthToken(String authToken) throws DataAccessException {
        String sql = "DELETE FROM tokens WHERE authToken = ?";
        var conn = database.getConnection();
        try (var statement = conn.prepareStatement(sql)) {

            statement.setString(1, authToken);
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new DataAccessException("Auth token not found: " + authToken);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting auth token: " + e.getMessage());
        } finally {
            database.returnConnection(conn);
        }
    }

}
