package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import datamodel.AuthData;
import datamodel.UserData;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class UserService {
    private DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {

        this.dataAccess = dataAccess;
    }

    public void clear() throws DataAccessException {
        dataAccess.clear();
    }

    public AuthData register(UserData user) throws DataAccessException {
        if (user.password() == null || user.username() == null || user.password().isEmpty() || user.username().isEmpty()) {
            throw new BadRequestResponse();
        }
        if (dataAccess.getUser(user.username()) != null) {
            throw new ForbiddenResponse();
        }
        var hashPwd = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        var storeUser = new UserData(user.username(), hashPwd, user.email());
        dataAccess.saveUser(storeUser);
        dataAccess.saveUser(user);
        var authData = new AuthData(user.username(), createAuthToken());
        dataAccess.saveAuth(authData);
        return authData;
    }

    public String createAuthToken() {
        return UUID.randomUUID().toString();
    }

    public AuthData login(UserData user) throws DataAccessException {
        if (user.password() == null || user.username() == null) {
            throw new BadRequestResponse();
        }
        var matchedUser = dataAccess.getUser(user.username());
        if (dataAccess.getUser(user.username()) == null || !user.password().equals(matchedUser.password())) {
            throw new UnauthorizedResponse();
        }
        var authData = new AuthData(user.username(), createAuthToken());
        dataAccess.saveAuth(authData);
        return authData;
    }

    public void logout(String authToken) throws DataAccessException {
        if (dataAccess.getAuth(authToken) == null) {
            throw new UnauthorizedResponse();
        }
        dataAccess.deleteAuth(authToken);
    }
}
