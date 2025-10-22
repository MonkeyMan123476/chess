package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;
import org.junit.jupiter.api.Test;
import datamodel.*;
import dataaccess.MemoryDataAccess;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void clear() throws DataAccessException {
        MemoryDataAccess da = new MemoryDataAccess();
        UserService userService = new UserService(da);
        GameService gameService = new GameService(da);
        UserData user = new UserData("joe", "j@j", "j");
        AuthData auth = userService.register(user);
        assertNotNull(da.getUser(user.username()));
        assertNotNull(da.getAuth(auth.authToken()));
        gameService.createGame(auth.authToken(), new GameData(123, null, null, "Fun", new ChessGame()));
        assertNotNull(da.getGame(123));
        userService.clear();
        assertNull(da.getUser(user.username()));
        assertNull(da.getAuth(auth.authToken()));
        assertNull(da.getGame(123));
    }

    @Test
    void registerValid() throws Exception {
        MemoryDataAccess da = new MemoryDataAccess();
        UserService service = new UserService(da);
        UserData user = new UserData("joe", "j@j", "j");
        AuthData res = service.register(user);
        assertNotNull(res);
        assertEquals(res.username(), user.username());
        assertNotNull(res.authToken());
        assertEquals(String.class, res.authToken().getClass());
    }

    @Test
    void registerInvalid() throws Exception {
        MemoryDataAccess da = new MemoryDataAccess();
        UserService service = new UserService(da);
        UserData user = new UserData("joe", "j@j", "j");
        service.register(user);
        assertThrows(ForbiddenResponse.class, () -> service.register(user));
        UserData badUser = new UserData(null, "j@", "j");
        assertThrows(BadRequestResponse.class, () -> service.register(badUser));
    }

    @Test
    void loginValid() throws Exception {
        MemoryDataAccess da = new MemoryDataAccess();
        UserService service = new UserService(da);
        UserData user = new UserData("joe", "j@j", "j");
        service.register(user);
        AuthData res = service.login(user);
        assertNotNull(res);
        assertEquals(res.username(), user.username());
        assertNotNull(res.authToken());
        assertEquals(String.class, res.authToken().getClass());
    }

    @Test
    void loginInvalid() throws Exception {
        MemoryDataAccess da = new MemoryDataAccess();
        UserService service = new UserService(da);
        UserData user = new UserData("joe", "j@j", "j");
        service.register(user);
        UserData wrongPasswordUser = new UserData("joe", "WRONG", "j");
        assertThrows(UnauthorizedResponse.class, () -> service.login(wrongPasswordUser));
    }

    @Test
    void logoutValid() throws Exception {
        MemoryDataAccess da = new MemoryDataAccess();
        UserService service = new UserService(da);
        UserData user = new UserData("joe", "j@j", "j");
        service.register(user);
        String auth = service.login(user).authToken();
        assertNotNull(auth);
        assertEquals(String.class, auth.getClass());
        assertNotNull(da.getUser(user.username()));
        service.logout(auth);
        assertNull(da.getAuth(user.username()));
    }

    @Test
    void logoutInvalid() throws Exception {
        MemoryDataAccess da = new MemoryDataAccess();
        UserService service = new UserService(da);
        UserData user = new UserData("joe", "j@j", "j");
        String auth = service.register(user).authToken();
        service.logout(auth);
        assertThrows(UnauthorizedResponse.class, () -> service.logout(auth));
    }
}