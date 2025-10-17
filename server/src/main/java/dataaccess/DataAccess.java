package dataaccess;

import datamodel.*;

public interface DataAccess {
    void clear() throws DataAccessException;
    void saveUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
}
