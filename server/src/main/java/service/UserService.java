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

    public AuthData register(UserData user) throws DataAccessException {
        if (user.password() == null) {
            System.out.println("no password bruh");
            throw new BadRequestResponse("Error: bad request");
        }
        if (dataAccess.getUser(user.username()) != null) {
            System.out.println("this username is taken bruh");
            throw new ForbiddenResponse("Error: already taken");
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
        if (user.password() == null || dataAccess.getUser(user.username()) == null) {
            System.out.println("either you didn't put your password or your account don't exist bruh");
            throw new BadRequestResponse("Error: bad request");
        }
        var matchedUser = dataAccess.getUser(user.username());
        if (!user.password().equals(matchedUser.password())) {
            System.out.println("wrong password bruh");
            throw new UnauthorizedResponse("Error: unauthorized");
        }
        var authData = new AuthData(user.username(), createAuthToken());
        dataAccess.saveAuth(authData);
        return authData;
    }

    public void logout(AuthData auth) throws DataAccessException {
        System.out.println("trying to logout");
        if (auth.authToken() == null) {
            System.out.println("auth don't exist");
            throw new UnauthorizedResponse("Error: unauthorized");
        }
        dataAccess.deleteAuth(auth);
    }
}
