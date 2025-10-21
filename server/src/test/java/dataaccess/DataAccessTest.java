package dataaccess;

import datamodel.AuthData;
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

    }

    @Test
    void getGame() throws DataAccessException {

    }

    @Test
    void saveGame() throws DataAccessException {

    }
}