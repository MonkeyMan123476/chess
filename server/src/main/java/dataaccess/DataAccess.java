package dataaccess;

import datamodel.*;

public interface DataAccess {
    void clear() throws DataAccessException;
    void saveUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void saveAuth(AuthData auth) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;

}
