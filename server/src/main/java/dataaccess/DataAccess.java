package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import datamodel.*;

import java.util.List;

public interface DataAccess {
    void clear() throws DataAccessException;
    void saveUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void saveAuth(AuthData auth) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    List<GameData> listGames(String authToken) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    void saveGame(GameData game) throws DataAccessException;
    void updateGame(ChessGame.TeamColor color, int gameID, String username, ChessMove move) throws DataAccessException;

    void saveUpdatedGame(ChessGame game, int gameID) throws DataAccessException;

    void removePlayer(int gameID, String username) throws DataAccessException;

}
