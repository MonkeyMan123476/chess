package dataaccess;

import datamodel.*;

public interface DataAccess {
    void clear();
    void saveUser(UserData user);
    UserData getUser(String username);
}
