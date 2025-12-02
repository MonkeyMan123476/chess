package server.websocket;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;
import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;
import exception.ResponseException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.eclipse.jetty.websocket.api.Session;
import service.UserService;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand action = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (action.getCommandType()) {
                case CONNECT -> connect(action, ctx.session);
                case LEAVE -> leave(action, ctx.session);
            }
        } catch (IOException | DataAccessException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(UserGameCommand cmd, Session session) throws IOException, DataAccessException {
        DataAccess dataAccess = new MySqlDataAccess();
        AuthData authData = dataAccess.getAuth(cmd.getAuthToken());
        if (authData == null) {
            session.getRemote().sendString(
                    new Gson().toJson(new ResponseException(ResponseException.Code.ServerError, "Error: invalid authToken"))
            );
            return;
        }
        UserData user = dataAccess.getUser(authData.username());
        GameData gameData = dataAccess.getGame(cmd.getGameID());

        if (user == null || gameData == null) {
            String message = "Error: invalid authToken or gameID";
            session.getRemote().sendString(
                    new Gson().toJson(new NotificationMessage(message)) // or use a dedicated ErrorMessage class
            );
            return;
        }

        String username = user.username();
        boolean isPlayer =
                username.equals(gameData.whiteUsername()) ||
                        username.equals(gameData.blackUsername());

        connections.add(session, username, cmd.getAuthToken(), cmd.getGameID(), isPlayer);

        session.getRemote().sendString(
                new Gson().toJson(new LoadGameMessage(gameData.game()))
        );

        String note;
        if (isPlayer) {
            String color = username.equals(gameData.whiteUsername()) ? "white" : "black";
            note = username + " joined as " + color;
        } else {
            note = username + " is observing the game";
        }

        connections.broadcast(cmd.getGameID(), session, new NotificationMessage(note));
    }

    private void leave(UserGameCommand cmd, Session session) throws IOException {
        var info = connections.get(session);
        if (info == null) return;

        connections.remove(session);

        connections.broadcast(info.gameID, session,
                new NotificationMessage(info.username + " left the game"));
    }
}