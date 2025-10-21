package server;

import com.google.gson.Gson;
import dataaccess.MemoryDataAccess;
import datamodel.AuthData;
import datamodel.UserData;
import io.javalin.*;
import io.javalin.http.Context;
import service.UserService;
import dataaccess.DataAccess;

import java.util.Map;

public class Server {

    private final Javalin server;
    private UserService userService;
    private DataAccess dataAccess;

    public Server() {
        dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
        server = Javalin.create(config -> config.staticFiles.add("web"));

        server.delete("db", this::clear);
        //server.delete("db", ctx -> ctx.result("{}"));
        server.post("user", this::register);
        server.post("session", this::login);
        server.delete("session", this::logout);

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
