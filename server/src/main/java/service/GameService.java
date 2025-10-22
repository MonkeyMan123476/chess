package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import datamodel.AuthData;
import datamodel.GameData;
import datamodel.JoinData;
import datamodel.UserData;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


public class GameService {
    private DataAccess dataAccess;
    private int gameNumber = 1;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public List<GameData> listGames(String authToken) throws DataAccessException {
        List<GameData> gameList;
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
        GameData gameData;
        if (game.gameID() == 0) {
            gameData = new GameData(gameNumber, game.whiteUsername(), game.blackUsername(), game.gameName(), new ChessGame());
            gameNumber++;
        } else {
            gameData = new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), new ChessGame());
        }
        dataAccess.saveGame(gameData);
        return gameData;
    }

    public void joinGame(String authToken, JoinData joinData) throws DataAccessException {
        ChessGame.TeamColor color = joinData.playerColor();
        int gameID = joinData.gameID();
        if (dataAccess.getAuth(authToken) == null) {
            System.out.println("auth don't exist bruh");
            throw new UnauthorizedResponse();
        }
        AuthData auth = dataAccess.getAuth(authToken);
        if (gameID == 0) {
            System.out.println("bad game id bruh");
            throw new BadRequestResponse();
        }
        if (!Objects.equals(color, ChessGame.TeamColor.BLACK) && !Objects.equals(color, ChessGame.TeamColor.WHITE)) {
            System.out.println("bad color bruh");
            throw new BadRequestResponse();
        }
        UserData user = dataAccess.getUser(auth.username());
        GameData gameBeingJoined = dataAccess.getGame(gameID);
        if ((color.equals(ChessGame.TeamColor.BLACK) && gameBeingJoined.blackUsername() != null ) || (color.equals(ChessGame.TeamColor.WHITE) && gameBeingJoined.whiteUsername() != null)) {
            System.out.println("this color is taken bruh");
            throw new ForbiddenResponse();
        }
        dataAccess.updateGame(color, gameID, user.username(), gameBeingJoined.game());
    }

}
