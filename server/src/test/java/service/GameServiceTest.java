package service;

import chess.ChessGame;
import dataaccess.MemoryDataAccess;
import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.UnauthorizedResponse;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    @Test
    void listGamesValid() throws Exception {
        MemoryDataAccess da = new MemoryDataAccess();
        UserService service = new UserService(da);
        GameService gameService = new GameService(da);
        UserData user = new UserData("joe", "j@j", "j");
        AuthData auth = service.register(user);
        assertNull(da.getGame(1));
        gameService.createGame(auth.authToken(), new GameData(0, "white", null, "game1", new ChessGame()));
        gameService.createGame(auth.authToken(), new GameData(0, null, "black", "game2", new ChessGame()));
        assertNotNull(da.getGame(1));
        assertNotNull(da.getGame(2));
        gameService.listGames(auth.authToken());
        assertEquals("game1", gameService.listGames(auth.authToken()).get(0).gameName());
        assertEquals("game2", gameService.listGames(auth.authToken()).get(1).gameName());
    }

    @Test
    void listGamesInvalid() throws Exception {
        MemoryDataAccess da = new MemoryDataAccess();
        UserService service = new UserService(da);
        GameService gameService = new GameService(da);
        UserData user = new UserData("joe", "j@j", "j");
        AuthData auth = service.register(user);
        assertNull(da.getGame(1));
        gameService.createGame(auth.authToken(), new GameData(0, "white", null, "game1", new ChessGame()));
        assertThrows(UnauthorizedResponse.class, () -> gameService.listGames("badToken"));
    }

    @Test
    void createGameValid() throws Exception {
        MemoryDataAccess da = new MemoryDataAccess();
        UserService service = new UserService(da);
        GameService gameService = new GameService(da);
        UserData user = new UserData("joe", "j@j", "j");
        AuthData auth = service.register(user);
        assertNull(da.getGame(123));
        gameService.createGame(auth.authToken(), new GameData(123, "hello", null, "Fun", new ChessGame()));
        assertNotNull(da.getGame(123));
    }

    @Test
    void createGameInvalid() throws Exception {
        MemoryDataAccess da = new MemoryDataAccess();
        UserService service = new UserService(da);
        GameService gameService = new GameService(da);
        UserData user = new UserData("joe", "j@j", "j");
        AuthData auth = service.register(user);
        GameData unauthorizedGameData = new GameData(123, "hello", null, "Fun", new ChessGame());
        assertThrows(UnauthorizedResponse.class, () -> gameService.createGame("bad authToken", unauthorizedGameData));
        gameService.createGame(auth.authToken(), new GameData(123, "hello", null, "Fun", new ChessGame()));
        GameData badGameData = new GameData(123, "hello", null, "", new ChessGame());
        assertThrows(BadRequestResponse.class, () -> gameService.createGame(auth.authToken(), badGameData));
    }

    @Test
    void joinGameValid() throws Exception {
        MemoryDataAccess da = new MemoryDataAccess();
        UserService service = new UserService(da);
        GameService gameService = new GameService(da);
        UserData user = new UserData("joe", "j@j", "j");
        AuthData auth = service.register(user);
        gameService.createGame(auth.authToken(), new GameData(0, "white", null, "game1", new ChessGame()));

    }

    @Test
    void joinGameInvalid() throws Exception {

    }
}
