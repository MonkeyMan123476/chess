package dataaccess;

import datamodel.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class DataAccessTest {

    @Test
    void clear() throws DataAccessException {
        var user = new UserData("joe", "j@j", "j@jmail.com");
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
        var user1 = new UserData("joe", "j@j", "j@jmail.com");
        DataAccess da = new MemoryDataAccess();
        assertNull(da.getUser("joe"));
        da.saveUser(user1);
        assertNotNull(da.getUser("joe"));
    }
}