package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;

public class MySqlDataAccess implements DataAccess {

    public MySqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void clear() throws DataAccessException {
        executeClearStatement("TRUNCATE TABLE users");
        executeClearStatement("TRUNCATE TABLE auths");
        executeClearStatement("TRUNCATE TABLE games");
    }

    @Override
    public void saveUser(UserData user) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, user.username());
                ps.setString(2, user.password());
                ps.setString(3, user.email());
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM users WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var un = rs.getString("username");
                        var password = rs.getString("password");
                        var email = rs.getString("email");
                        return new UserData(username, password, email);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void saveAuth(AuthData auth) throws DataAccessException {
        var statement = "INSERT INTO auths (username, authToken) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, auth.username());
                ps.setString(2, auth.authToken());
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, authToken FROM auths WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var username = rs.getString("username");
                        return new AuthData(username, authToken);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM auths WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    @Override
    public List<GameData> listGames(String authToken) throws DataAccessException {
        List<GameData> gameList = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        var gameID = rs.getInt("gameID");
                        var whiteUsername = rs.getString("whiteUsername");
                        var blackUsername = rs.getString("blackUsername");
                        var gameName = rs.getString("gameName");
                        var serializer = new Gson();
                        ChessGame game = serializer.fromJson(rs.getString("game"), ChessGame.class);
                        gameList.add(new GameData(gameID, whiteUsername, blackUsername, gameName, game));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return gameList;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var whiteUsername = rs.getString("whiteUsername");
                        var blackUsername = rs.getString("blackUsername");
                        var gameName = rs.getString("gameName");
                        var serializer = new Gson();
                        ChessGame game = serializer.fromJson(rs.getString("game"), ChessGame.class);
                        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void saveGame(GameData game) throws DataAccessException {
        var statement = "INSERT INTO games (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, game.gameID());
                ps.setString(2, game.whiteUsername());
                ps.setString(3, game.blackUsername());
                ps.setString(4, game.gameName());
                var serializer = new Gson();
                ps.setString(5, serializer.toJson(game.game()));
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    @Override
    public void updateGame(ChessGame.TeamColor color, int gameID, String username, ChessMove move) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            if (color != null && username != null) {
                if (color == ChessGame.TeamColor.WHITE) {
                    addUsername(conn, "UPDATE games SET whiteUsername=? WHERE gameID=?", username, gameID);
                }
                if (color == ChessGame.TeamColor.BLACK) {
                    addUsername(conn, "UPDATE games SET blackUsername=? WHERE gameID=?", username, gameID);
                }
            }
            ChessGame currentGame = getGame(gameID).game();
            if (move != null) {
                try {
                    currentGame.makeMove(move);
                } catch (InvalidMoveException e) {
                    throw new DataAccessException("Invalid move: " + e.getMessage());
                }
            }
            var updateGameStatement = "UPDATE games SET game=? WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(updateGameStatement)) {
                var serializer = new Gson();
                ps.setString(1, serializer.toJson(currentGame));
                ps.setInt(2, gameID);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to update game: %s", e.getMessage()));
        }
    }

    @Override
    public void removePlayer(int gameID, String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            GameData gameData = getGame(gameID);

            if (gameData == null) {
                throw new DataAccessException("Game does not exist");
            }

            String white = gameData.whiteUsername();
            String black = gameData.blackUsername();

            if (username.equals(white)) {
                try (PreparedStatement ps = conn.prepareStatement("UPDATE games SET whiteUsername=NULL WHERE gameID=?")) {
                    ps.setInt(1, gameID);
                    ps.executeUpdate();
                }
            } else if (username.equals(black)) {
                try (PreparedStatement ps = conn.prepareStatement("UPDATE games SET blackUsername=NULL WHERE gameID=?")) {
                    ps.setInt(1, gameID);
                    ps.executeUpdate();
                }
            }

        } catch (Exception e) {
            throw new DataAccessException("Unable to remove player: " + e.getMessage());
        }
    }

    private void addUsername(Connection conn, String statement, String username, int gameID) throws DataAccessException {
        try (PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.setString(1, username);
            ps.setInt(2, gameID);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    private void executeClearStatement(String statement) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to clear data: %s", e.getMessage()));
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              `username` varchar(50) NOT NULL,
              `password` char(100) NOT NULL,
              `email` varchar(50) NOT NULL,
              PRIMARY KEY (`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """,
            
            """
            CREATE TABLE IF NOT EXISTS  auths (
              `username` varchar(50) NOT NULL,
              `authToken` varchar(50) NOT NULL,
              PRIMARY KEY (`authToken`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """,
            
            """
            CREATE TABLE IF NOT EXISTS  games (
              `gameID` int NOT NULL,
              `whiteUsername` varchar(50),
              `blackUsername` varchar(50),
              `gameName` varchar(50) NOT NULL,
              `game` varchar(2000) NOT NULL,
              PRIMARY KEY (`gameID`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
