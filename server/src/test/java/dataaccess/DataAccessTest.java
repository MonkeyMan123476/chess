package dataaccess;

import chess.ChessGame;
import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;
import org.junit.jupiter.api.*;

import javax.xml.crypto.Data;

import static org.junit.jupiter.api.Assertions.*;

class DataAccessTest {

    @Test
    void clear() throws DataAccessException {
        UserData user = new UserData("joe", "j@j", "j@jmail.com");
        DataAccess da = new MemoryDataAccess();
        assertNull(da.getUser(user.username()));
        da.saveUser(user);
        assertNotNull(da.getUser(user.username()));
        da.clear();
        assertNull(da.getUser(user.username()));
    }

    @Test
    void saveUser() throws DataAccessException {
        DataAccess da = new MemoryDataAccess();
        assertNull(da.getUser("joe"));
        da.saveUser(new UserData("joe", "j@j", "j@jmail.com"));
        assertNotNull(da.getUser("joe"));
    }

    @Test
    void getUser() throws DataAccessException {
        DataAccess da = new MemoryDataAccess();
        assertNull(da.getUser("joe"));
        da.saveUser(new UserData("joe", "j@j", "j@jmail.com"));
        assertNotNull(da.getUser("joe"));
        assertEquals("j@jmail.com", da.getUser("joe").email());
    }

    @Test
    void saveAuth() throws DataAccessException {
        DataAccess da = new MemoryDataAccess();
        assertNull(da.getAuth("joeAuthToken"));
        da.saveAuth(new AuthData("joe", "joeAuthToken"));
        assertNotNull(da.getAuth("joeAuthToken"));
    }

    @Test
    void getAuth() throws DataAccessException {
        DataAccess da = new MemoryDataAccess();
        assertNull(da.getAuth("joeAuthToken"));
        da.saveAuth(new AuthData("joe", "joeAuthToken"));
        assertNotNull(da.getAuth("joeAuthToken"));
        assertEquals("joe", da.getAuth("joeAuthToken").username());
    }

    @Test
    void deleteAuth() throws DataAccessException {
        DataAccess da = new MemoryDataAccess();
        da.saveAuth(new AuthData("joe", "joeAuthToken"));
        assertNotNull(da.getAuth("joeAuthToken"));
        da.deleteAuth("joeAuthToken");
        assertNull(da.getAuth("joeAuthToken"));
    }


    @Test
    void listGames() throws DataAccessException {
        DataAccess da = new MemoryDataAccess();
        da.saveAuth(new AuthData("joe", "joeAuthToken"));
        GameData game1 = new GameData(111, "white", "black", "testGame1", new ChessGame());
        GameData game2 = new GameData(222, "white", "black", "testGame1", new ChessGame());
        da.saveGame(game1);
        da.saveGame(game2);
        assertTrue(da.listGames("joeAuthToken").contains(game1));
        assertTrue(da.listGames("joeAuthToken").contains(game2));
    }

    @Test
    void getGame() throws DataAccessException {
        DataAccess da = new MemoryDataAccess();
        assertNull(da.getGame(333));
        da.saveGame(new GameData(333, "white", "black", "testGame", new ChessGame()));
        assertNotNull(da.getGame(333));
        assertEquals("testGame", da.getGame(333).gameName());
    }

    @Test
    void saveGame() throws DataAccessException {
        DataAccess da = new MemoryDataAccess();
        assertNull(da.getGame(333));
        da.saveGame(new GameData(333, "white", "black", "testGame", new ChessGame()));
        assertNotNull(da.getGame(333));
    }

    @Test
    void updateGame() throws DataAccessException {
        DataAccess da = new MemoryDataAccess();
        da.saveAuth(new AuthData("joe", "joeAuthToken"));
        GameData testGame = new GameData(111, "white", null, "testGame1", new ChessGame());
        da.saveGame(testGame);
        assertNull(da.getGame(111).blackUsername());
        da.updateGame(ChessGame.TeamColor.BLACK, 111, "joe", new ChessGame());
        assertEquals("joe", da.getGame(111).blackUsername());
    }
}