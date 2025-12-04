package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;
import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.*;
import websocket.messages.*;

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
            Gson gson = new Gson();
            UserGameCommand base = gson.fromJson(ctx.message(), UserGameCommand.class);
            switch (base.getCommandType()) {
                case CONNECT -> {
                    UserGameCommand action = gson.fromJson(ctx.message(), UserGameCommand.class);
                    connect(action, ctx.session);
                }
                case LEAVE -> {
                    UserGameCommand action = gson.fromJson(ctx.message(), UserGameCommand.class);
                    leave(action, ctx.session);
                }
                case MAKE_MOVE -> {
                    MakeMoveCommand action = gson.fromJson(ctx.message(), MakeMoveCommand.class);
                    makeMove(action, ctx.session);
                }
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
                    new Gson().toJson(new ErrorMessage("Error: invalid authToken"))
            );
            return;
        }
        UserData user = dataAccess.getUser(authData.username());
        GameData gameData = dataAccess.getGame(cmd.getGameID());

        if (user == null || gameData == null) {
            session.getRemote().sendString(
                    new Gson().toJson(new ErrorMessage("Error: invalid authToken or gameID"))
            );
            return;
        }

        String username = user.username();
        boolean isPlayer = username.equals(gameData.whiteUsername()) || username.equals(gameData.blackUsername());

        connections.add(session, username, cmd.getAuthToken(), cmd.getGameID(), isPlayer);
        session.getRemote().sendString(new Gson().toJson(new LoadGameMessage(gameData.game())));

        String note;
        if (isPlayer) {
            String color = username.equals(gameData.whiteUsername()) ? "white" : "black";
            note = username + " joined as " + color;
        } else {
            note = username + " is observing the game";
        }

        connections.broadcast(session, new NotificationMessage(note));
    }

    private void leave(UserGameCommand cmd, Session session) throws IOException, DataAccessException {
        var info = connections.get(session);
        if (info == null) return;

        connections.remove(session);

        DataAccess dataAccess = new MySqlDataAccess();
        GameData gameData = dataAccess.getGame(info.gameID);

        if (gameData != null) {
            if (info.username.equals(gameData.whiteUsername())) {
                dataAccess.removePlayer(gameData.gameID(), gameData.whiteUsername());
            } else if (info.username.equals(gameData.blackUsername())) {
                dataAccess.removePlayer(gameData.gameID(), gameData.blackUsername());
            }
        }

        connections.broadcast(session, new NotificationMessage(info.username + " left the game"));
    }

    private void makeMove(MakeMoveCommand cmd, Session session) throws DataAccessException, IOException {
        var info = connections.get(session);
        if (info == null) return;

        DataAccess dataAccess = new MySqlDataAccess();
        AuthData authData = dataAccess.getAuth(cmd.getAuthToken());
        if (authData == null) {
            session.getRemote().sendString(
                    new Gson().toJson(new ErrorMessage("Error: invalid authToken"))
            );
            return;
        }

        GameData gameData = dataAccess.getGame(info.gameID);
        if (gameData == null) return;

        boolean isPlayer = info.username.equals(gameData.whiteUsername()) || info.username.equals(gameData.blackUsername());
        if (!isPlayer) {
            try {
                session.getRemote().sendString(new Gson().toJson(new ErrorMessage("Only players can make moves")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        ChessMove move = cmd.getMove();
        ChessGame game = gameData.game();

        ChessGame.TeamColor playerColor = info.username.equals(gameData.whiteUsername())
                ? ChessGame.TeamColor.WHITE
                : ChessGame.TeamColor.BLACK;

        if (game.getTeamTurn() != playerColor) {
            try {
                session.getRemote().sendString(new Gson().toJson(new ErrorMessage("Not your turn")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        try {
            dataAccess.updateGame(playerColor, info.gameID, info.username, move);
        } catch (DataAccessException e) {
            try {
                session.getRemote().sendString(new Gson().toJson(new ErrorMessage("Invalid move")));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            return;
        }

        GameData updatedGameData = dataAccess.getGame(info.gameID);
        String notificationText = info.username + " moved from " + move.getStartPosition() + " to " + move.getEndPosition();
        connections.broadcast(session, new NotificationMessage(notificationText));
        connections.broadcastAll(new LoadGameMessage(updatedGameData.game()));
    }
}