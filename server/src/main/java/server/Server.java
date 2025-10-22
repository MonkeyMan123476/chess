package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.MemoryDataAccess;
import datamodel.AuthData;
import datamodel.GameData;
import datamodel.JoinData;
import datamodel.UserData;
import io.javalin.*;
import io.javalin.http.Context;
import service.*;
import dataaccess.DataAccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {

    private final Javalin server;
    private UserService userService;
    private GameService gameService;
    private DataAccess dataAccess;

    public Server() {
        dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);
        server = Javalin.create(config -> config.staticFiles.add("web"));

        server.delete("db", this::clear);
        server.post("user", this::register);
        server.post("session", this::login);
        server.delete("session", this::logout);
        server.get("game", this::listGames);
        server.post("game", this::createGame);
        server.put("game", this::joinGame);

        // Register your endpoints and exception handlers here.
        server.error(400, this::badRequest);
        server.error(401, this::unauthorized);
        server.error(403, this::alreadyTaken);
        server.error(500, this::error);

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
        var serializer = new Gson();
        String reqJson = ctx.header("authorization");
        var req = serializer.fromJson(reqJson, String.class);
        userService.logout(req);
        ctx.result("{}");
    }

    private void listGames(Context ctx) throws Exception {
        var serializer = new Gson();
        String reqJson = ctx.header("authorization");
        var req = serializer.fromJson(reqJson, String.class);
        var resList = gameService.listGames(req);
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
        System.out.println(serializer.toJson(res));
        ctx.result(serializer.toJson(res));
    }

    private void createGame(Context ctx) throws Exception {
        var serializer = new Gson();
        String reqAuth = ctx.header("authorization");
        String reqGameJson = ctx.body();
        var reqGame = serializer.fromJson(reqGameJson, GameData.class);
        var res = Map.of("gameID", gameService.createGame(reqAuth, reqGame).gameID());
        ctx.result(serializer.toJson(res));
    }

    private void joinGame(Context ctx) throws Exception {
        var serializer = new Gson();
        String reqAuth = ctx.header("authorization");
        String reqGameJson = ctx.body();
        var reqGame = serializer.fromJson(reqGameJson, JoinData.class);
        gameService.joinGame(reqAuth, reqGame);
        ctx.result("{}");
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
        context.json(new Gson().toJson(Map.of("message", "Error: bad request")));
    }

    private void alreadyTaken(Context context) {
        context.json(new Gson().toJson(Map.of("message", "Error: already taken")));
    }

    private void unauthorized(Context context) {
        context.json(new Gson().toJson(Map.of("message", "Error: unauthorized")));
    }

    private void error(Context context) {
        context.json(new Gson().toJson(Map.of("message", "Error:")));
    }
}
