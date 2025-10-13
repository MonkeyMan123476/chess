import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import dataaccess.*;
import service.*;
import datamodel.*;

public class ServiceTest {

    @Test
    public void registerNormal() {
        var dataAccess = new MemoryDataAccess();
        var userService = new UserService(dataAccess);

        var res = userService.register(new User("cow", "rat", "john"));

        Assertions.assertNotNull(res);
    }
}
