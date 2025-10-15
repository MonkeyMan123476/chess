package service;

import org.junit.jupiter.api.Test;
import datamodel.*;
import dataaccess.MemoryDataAccess;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void clear() {

    }

    @Test
    void register() throws Exception {
        var user = new UserData("joe", "j@j", "j");
        var at = "xyz";

        var da = new MemoryDataAccess();
        var service = new UserService(da);
        AuthData res = service.register(user);
        assertNotNull(res);
        assertEquals(res.username(), user.username());
        assertNotNull(res.authToken());
        assertEquals(String.class, res.authToken().getClass());
    }
}