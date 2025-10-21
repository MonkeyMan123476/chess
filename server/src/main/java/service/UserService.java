package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import datamodel.AuthData;
import datamodel.UserData;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;

import java.util.Objects;
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
            System.out.println("you forgor a username or password bruh");
            throw new BadRequestResponse();
        }
        if (dataAccess.getUser(user.username()) != null) {
            System.out.println("this username is taken bruh");
            throw new ForbiddenResponse();
        }
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
            System.out.println("you gotta give a username and password bruh");
            throw new BadRequestResponse();
        }
        var matchedUser = dataAccess.getUser(user.username());
        if (dataAccess.getUser(user.username()) == null || !user.password().equals(matchedUser.password())) {
            System.out.println("wrong username or password bruh");
            throw new UnauthorizedResponse();
        }
        var authData = new AuthData(user.username(), createAuthToken());
        dataAccess.saveAuth(authData);
        return authData;
    }

    public void logout(String authToken) throws DataAccessException {
        if (dataAccess.getAuth(authToken) == null) {
            System.out.println("auth don't exist bruh");
            throw new UnauthorizedResponse();
        }
        dataAccess.deleteAuth(authToken);
    }
}
