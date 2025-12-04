package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import datamodel.GameData;
import datamodel.UserData;
import datamodel.AuthData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MemoryDataAccess implements DataAccess {
    private HashMap<String, UserData> users = new HashMap<>();
    private HashMap<String, AuthData> auths = new HashMap<>();
    private HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public void clear() {
        users.clear();
        auths.clear();
        games.clear();
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
    public void saveAuth(AuthData auth) {
        auths.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return auths.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        auths.remove(authToken);
    }

    @Override
    public List<GameData> listGames(String authToken) {
        List<GameData> gameList = new ArrayList<>();
        for (HashMap.Entry<Integer, GameData> game : games.entrySet()) {
            gameList.add(game.getValue());
        }
        return gameList;
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public void saveGame(GameData game) {
        games.put(game.gameID(), game);
    }

    @Override
    public void updateGame(ChessGame.TeamColor color, int gameID, String username, ChessMove move) {
        GameData oldGameVersion = getGame(gameID);
        if (color == ChessGame.TeamColor.WHITE) {
            games.put(gameID, new GameData(gameID, username, oldGameVersion.blackUsername(), oldGameVersion.gameName(), getGame(gameID).game()));
        }
        if (color == ChessGame.TeamColor.BLACK) {
            games.put(gameID, new GameData(gameID, oldGameVersion.whiteUsername(), username, oldGameVersion.gameName(), getGame(gameID).game()));
        }
    }

    @Override
    public void saveUpdatedGame(ChessGame game, int gameID) throws DataAccessException {

    }

    @Override
    public void removePlayer(int gameID, String username) throws DataAccessException {

    }
}
