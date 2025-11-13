package client;

import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() throws Exception {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port);
        serverFacade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    void register() throws Exception {
        assertThrows(Exception.class, () -> serverFacade.login("registerTestUsername", "password"));
        var authData1 = serverFacade.register("registerTestUsername", "password", "email@email.com");
        assertNotNull(authData1.authToken());
        var authData2 = serverFacade.login("registerTestUsername", "password");
        assertNotNull(authData2.authToken());
        assertEquals(authData1.username(), authData2.username());
    }

    @Test
    void login() throws Exception {
        serverFacade.register("loginTestUsername", "correctPassword", "email@email.com");
        assertThrows(Exception.class, () -> serverFacade.login("loginTestUsername", "wrongPassword"));
        var authData = serverFacade.login("loginTestUsername", "correctPassword");
        assertNotNull(authData);
        assertEquals("loginTestUsername", authData.username());
    }

    @Test
    void logout() throws Exception {
        var authData = serverFacade.register("logoutTestUsername", "correctPassword", "email@email.com");
        assertThrows(Exception.class, () -> serverFacade.logout("invalidAuthToken"));
        serverFacade.logout(authData.authToken());
        assertThrows(Exception.class, () -> serverFacade.logout(authData.authToken()));
    }
}
