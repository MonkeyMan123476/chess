package dataaccess;

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
    GameData getGame(String gameID);
    void saveGame(GameData game);
}
