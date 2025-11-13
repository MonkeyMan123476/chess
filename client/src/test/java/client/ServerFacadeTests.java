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
    static void stopServer() throws Exception {
        serverFacade.clear();
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
    void registerInvalid() throws Exception {
        serverFacade.register("registerInvalidTestUsername", "password", "email@email.com");
        assertThrows(Exception.class, () -> serverFacade.register("registerInvalidTestUsername", "password", "email@email.com"));
    }

    @Test
    void login() throws Exception {
        serverFacade.register("loginTestUsername", "correctPassword", "email@email.com");
        var authData = serverFacade.login("loginTestUsername", "correctPassword");
        assertNotNull(authData);
        assertEquals("loginTestUsername", authData.username());
    }

    @Test
    void loginInvalid() throws Exception {
        serverFacade.register("loginInvalidTestUsername", "correctPassword", "email@email.com");
        assertThrows(Exception.class, () -> serverFacade.login("loginInvalidTestUsername", "wrongPassword"));
    }

    @Test
    void logout() throws Exception {
        var authData = serverFacade.register("logoutTestUsername", "correctPassword", "email@email.com");
        assertDoesNotThrow(() -> serverFacade.logout(authData.authToken()));
    }

    @Test
    void logoutInvalid() throws Exception {
        serverFacade.register("logoutInvalidTestUsername", "correctPassword", "email@email.com");
        assertThrows(Exception.class, () -> serverFacade.logout("invalidAuthToken"));
    }

    @Test
    void createGame() throws Exception {
        var authData = serverFacade.register("createGameTestUsername", "password", "email@email.com");
        var GameData = serverFacade.createGame(authData.authToken(), "testGameName");
        assertEquals(1, GameData.gameID());
    }

    @Test
    void createGameInvalid() {
        assertThrows(Exception.class, () -> serverFacade.createGame("invalidAuthToken", "testGameName"));
    }



}
