package server;

import com.google.gson.Gson;
import dataaccess.MemoryDataAccess;
import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;
import io.javalin.*;
import io.javalin.http.Context;
import service.*;
import dataaccess.DataAccess;

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

    }

    private void clear(Context ctx) throws Exception {
        userService.clear();
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
    }

    private void listGames(Context ctx) throws Exception {
        var serializer = new Gson();
        String reqJson = ctx.header("authorization");
        var req = serializer.fromJson(reqJson, String.class);
        var res = gameService.listGames(req);
        ctx.result(serializer.toJson(res));
    }

    private void createGame(Context ctx) throws Exception {
        var serializer = new Gson();
        String reqAuthJson = ctx.header("authorization");
        String reqGameJson = ctx.body();
        var reqAuth = serializer.fromJson(reqAuthJson, String.class);
        var reqGame = serializer.fromJson(reqGameJson, GameData.class);
        var res = gameService.createGame(reqAuth, reqGame);
        ctx.result(serializer.toJson(res));
    }

    private void joinGame(Context ctx) throws Exception {
        var serializer = new Gson();
        String reqAuthJson = ctx.header("authorization");
        String reqGameJson = ctx.body();
        var reqAuth = serializer.fromJson(reqAuthJson, String.class);
        var reqGame = serializer.fromJson(reqGameJson, GameData.class);
        gameService.joinGame(reqAuth, reqGame);
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


}
