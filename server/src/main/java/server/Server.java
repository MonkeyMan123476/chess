package server;

import chess.ChessGame;
import com.google.gson.Gson;
import datamodel.GameData;
import datamodel.JoinData;
import datamodel.UserData;
import exception.ResponseException;
import io.javalin.*;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;
import service.*;
import dataaccess.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {

    private final Javalin server;
    private final UserService userService;
    private final GameService gameService;
    private final DataAccess dataAccess;

    public Server() {
        server = Javalin.create(config -> config.staticFiles.add("web"));
        try {
            dataAccess = new MySqlDataAccess();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize DataAccess", e);
        }
        this.userService = new UserService(dataAccess);
        this.gameService = new GameService(dataAccess);

        server.delete("db", ctx -> {
            try {
                clear(ctx);
            } catch (DataAccessException e) {
                error(ctx, e);
            }
        });
        server.post("user", ctx -> {
            try {
                register(ctx);
            } catch (BadRequestResponse r) {
                badRequest(ctx);
            } catch (ForbiddenResponse r) {
                alreadyTaken(ctx);
            } catch (DataAccessException e) {
                error(ctx, e);
            }
        });
        server.post("session",ctx -> {
            try {
                login(ctx);
            } catch (BadRequestResponse r) {
                badRequest(ctx);
            } catch (UnauthorizedResponse r) {
                unauthorized(ctx);
            } catch (DataAccessException e) {
                error(ctx, e);
            }
        });
        server.delete("session", ctx -> {
            try {
                logout(ctx);
            } catch (UnauthorizedResponse r) {
                unauthorized(ctx);
            } catch (DataAccessException e) {
                error(ctx, e);
            }
        });
        server.get("game", ctx -> {
            try {
                listGames(ctx);
            } catch (UnauthorizedResponse r) {
                unauthorized(ctx);
            } catch (DataAccessException e) {
                error(ctx, e);
            }
        });
        server.post("game", ctx -> {
            try {
                createGame(ctx);
            } catch (BadRequestResponse r) {
                badRequest(ctx);
            } catch (UnauthorizedResponse r) {
                unauthorized(ctx);
            } catch (DataAccessException e) {
                error(ctx, e);
            }
        });
        server.put("game", ctx -> {
            try {
                joinGame(ctx);
            } catch (BadRequestResponse r) {
                badRequest(ctx);
            } catch (UnauthorizedResponse r) {
                unauthorized(ctx);
            } catch (ForbiddenResponse r) {
                alreadyTaken(ctx);
            } catch (DataAccessException e) {
                error(ctx, e);
            }
        });
        server.get("game/{id}", ctx -> {
            try {
                getGame(ctx);
            } catch (Exception e) {
                error(ctx, e);
            }
        });
    }

    private void clear(Context ctx) throws Exception {
        userService.clear();
        ctx.result("{}");
    }

    private void register(Context ctx) throws Exception {
        var serializer = new Gson();
        String reqJson = ctx.body();
        var req = serializer.fromJson(reqJson, UserData.class);

        var res = userService.register(req);

        ctx.result(serializer.toJson(res));

    }

    private void login(Context ctx) throws Exception {
        var serializer = new Gson();
        String reqJson = ctx.body();
        var req = serializer.fromJson(reqJson, UserData.class);
        var res = userService.login(req);
        ctx.result(serializer.toJson(res));
    }

    private void logout(Context ctx) throws Exception {
        String authToken = ctx.header("authorization");
        userService.logout(authToken);
        ctx.result("{}");
    }

    private void listGames(Context ctx) throws Exception {
        var serializer = new Gson();
        String authToken = ctx.header("authorization");
        var resList = gameService.listGames(authToken);
        List<Map<String, Object>> newList = new ArrayList<>();
        for (GameData gameData : resList) {
            Map<String, Object> gameValues = new HashMap<>();
            gameValues.put("gameID", gameData.gameID());
            gameValues.put("whiteUsername", gameData.whiteUsername());
            gameValues.put("blackUsername", gameData.blackUsername());
            gameValues.put("gameName", gameData.gameName());
            newList.add(gameValues);
        }
        var res = Map.of("games", newList);
        ctx.result(serializer.toJson(res));
    }

    private void createGame(Context ctx) throws Exception {
        var serializer = new Gson();
        String authToken = ctx.header("authorization");
        String reqGameJson = ctx.body();
        var reqGame = serializer.fromJson(reqGameJson, GameData.class);
        var res = Map.of("gameID", gameService.createGame(authToken, reqGame).gameID());
        ctx.result(serializer.toJson(res));
    }

    private void joinGame(Context ctx) throws Exception {
        var serializer = new Gson();
        String authToken = ctx.header("authorization");
        String reqGameJson = ctx.body();
        var reqGame = serializer.fromJson(reqGameJson, JoinData.class);
        gameService.joinGame(authToken, reqGame);
        ctx.result("{}");
    }

    private void getGame(Context ctx) throws Exception {
        var serializer = new Gson();
        var req = Integer.parseInt(ctx.pathParam("id"));
        var res = gameService.getGame(req);
        ctx.result(serializer.toJson(res));
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }



    // Error types

    private void badRequest(Context context) {
        context.status(400);
        context.json(new Gson().toJson(Map.of("message", "Error: bad request")));
    }

    private void alreadyTaken(Context context) {
        context.status(403);
        context.json(new Gson().toJson(Map.of("message", "Error: already taken")));
    }

    private void unauthorized(Context context) {
        context.status(401);
        context.json(new Gson().toJson(Map.of("message", "Error: unauthorized")));
    }

    private void error(Context ctx, Exception e) {
        ctx.status(500);
        ctx.json(new Gson().toJson(Map.of("message", "Error:" + e.getMessage())));
    }
}
