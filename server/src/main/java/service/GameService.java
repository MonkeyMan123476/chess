package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class GameService {
    private DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public Collection<GameData> listGames(String authToken) throws DataAccessException {
        List<GameData> gameList = new ArrayList<>();
        if (dataAccess.getAuth(authToken) == null) {
            System.out.println("auth don't exist bruh");
            throw new UnauthorizedResponse();
        }
        gameList = dataAccess.listGames(authToken);

        return gameList;
    }

    public GameData createGame(String authToken, GameData game) throws DataAccessException {
        if (dataAccess.getAuth(authToken) == null) {
            System.out.println("auth don't exist bruh");
            throw new UnauthorizedResponse();
        }
        if (game.gameName() == null || game.gameName().isEmpty()) {
            System.out.println("no game name bruh");
            throw new BadRequestResponse();
        }

        var gameData = new GameData("1234", game.whiteUsername(), game.blackUsername(), game.gameName());
        dataAccess.saveGame(gameData);
        return gameData;
    }

    public void joinGame(String authToken, GameData game) throws DataAccessException {

    }

}
