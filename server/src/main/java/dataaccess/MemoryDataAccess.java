package dataaccess;

import datamodel.GameData;
import datamodel.UserData;
import datamodel.AuthData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MemoryDataAccess implements DataAccess {
    private HashMap<String, UserData> users = new HashMap<>();
    private HashMap<String, AuthData> auths = new HashMap<>();
    private HashMap<String, GameData> games = new HashMap<>();

    @Override
    public void clear() {
        users.clear();
        auths.clear();
    }

    @Override
    public void saveUser(UserData user) {
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public void saveAuth(AuthData auth) throws DataAccessException {
        auths.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return auths.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        auths.remove(authToken);
    }

    @Override
    public List<GameData> listGames(String authToken) throws DataAccessException {
        List<GameData> gameList = new ArrayList<>();
        games.forEach((gameID, game) -> gameList.add(game));
        return gameList;
    }

    @Override
    public GameData getGame(String gameID) {
        return games.get(gameID);
    }

    @Override
    public void saveGame(GameData game) {
        games.put(game.gameID(), game);
    }
}
