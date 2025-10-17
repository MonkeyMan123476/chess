package service;

import dataaccess.DataAccess;
import datamodel.AuthData;
import datamodel.UserData;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

public class UserService {
    private DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {

        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData user) throws Exception {
        if (user.password() == null) {
            throw new Exception("Error: bad request");
        }
        if (dataAccess.getUser(user.username()) != null) {
            throw new AccessDeniedException("Error: already taken");
        }
        dataAccess.saveUser(user);
        var authData = new AuthData(user.username(), createAuthToken());

        return authData;
    }

    public String createAuthToken() {
        return UUID.randomUUID().toString();
    }
}
