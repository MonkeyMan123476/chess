package service;

import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;
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
        MemoryDataAccess da = new MemoryDataAccess();
        UserService service = new UserService(da);
        UserData user = new UserData("joe", "j@j", "j");
        AuthData res = service.register(user);
        assertNotNull(res);
        assertEquals(res.username(), user.username());
        assertNotNull(res.authToken());
        assertEquals(String.class, res.authToken().getClass());
        assertThrows(ForbiddenResponse.class, () -> service.register(user));
        UserData badUser = new UserData(null, "j@", "j");
        assertThrows(BadRequestResponse.class, () -> service.register(badUser));
    }

    @Test
    void login() throws Exception {
        MemoryDataAccess da = new MemoryDataAccess();
        UserService service = new UserService(da);
        UserData user = new UserData("joe", "j@j", "j");
        service.register(user);
        AuthData res = service.login(user);
        assertNotNull(res);
        assertEquals(res.username(), user.username());
        assertNotNull(res.authToken());
        assertEquals(String.class, res.authToken().getClass());
        UserData wrongPasswordUser = new UserData("joe", "WRONG", "j");
        assertThrows(UnauthorizedResponse.class, () -> service.login(wrongPasswordUser));
    }

    @Test
    void logout() throws Exception {
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
        assertThrows(UnauthorizedResponse.class, () -> service.logout(auth));

    }
}