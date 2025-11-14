package server;

import chess.ChessGame;
import com.google.gson.Gson;
import datamodel.*;
import exception.ResponseException;

import java.lang.reflect.Type;
import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import com.google.gson.reflect.TypeToken;


public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    // JUST FOR TESTING
    public void clear() throws Exception {
        var request = buildRequest("DELETE", "/db", null, null);
        var response = sendRequest(request);
        try {
            handleResponse(response, null);
        } catch (Exception e) {
            throw new Exception("Unable to clear database: " + e.getMessage());
        }
    }

    public AuthData login(String username, String password) throws Exception {
        var request = buildRequest("POST", "/session", new UserData(username, password, null), null);
        var response = sendRequest(request);
        try {
            return handleResponse(response, AuthData.class);
        } catch (Exception e) {
            throw new Exception("Unable to login: " + e.getMessage());
        }
    }

    public AuthData register(String username, String password, String email) throws Exception {
        var request = buildRequest("POST", "/user", new UserData(username, password, email), null);
        var response = sendRequest(request);
        try {
            return handleResponse(response, AuthData.class);
        } catch (Exception e) {
            throw new Exception("Unable to register: " + e.getMessage());
        }
    }

    public void logout(String authToken) throws Exception {
        var request = buildRequest("DELETE", "/session", null, authToken);
        var response = sendRequest(request);
        try {
            handleResponse(response, null);
        } catch (Exception e) {
            throw new Exception("Unable to logout: " + e.getMessage());
        }
    }

    public GameData createGame(String authToken, String gameName) throws Exception {
        var request = buildRequest("POST", "/game", new GameData(0, null, null, gameName, null), authToken);
        var response = sendRequest(request);
        try {
            return handleResponse(response, GameData.class);
        } catch (Exception e) {
            throw new Exception("Unable to create game: " + e.getMessage());
        }
    }

    public ArrayList<GameData> listGames(String authToken) throws Exception {
        var request = buildRequest("GET", "/game", null, authToken);
        var response = sendRequest(request);
        Type wrapperType = new TypeToken<GameListResponse>(){}.getType();
        try {
            GameListResponse wrapper = handleResponse(response, wrapperType);
            assert wrapper != null;
            return wrapper.games;
        } catch (Exception e) {
            throw new Exception("Unable to list games: " + e.getMessage());
        }
    }

    public void joinGame(String authToken, int gameNumber, ChessGame.TeamColor color) throws Exception {
        var request = buildRequest("PUT", "/game", new JoinData(color, gameNumber), authToken);
        var response = sendRequest(request);
        try {
            handleResponse(response, null);
        } catch (Exception e) {
            throw new Exception("Unable to join game: " + e.getMessage());
        }
    }

    public ChessGame getGame(int gameNumber) throws Exception {
        var request = buildRequest("GET", "/game/" + gameNumber, null, null);
        var response = sendRequest(request);
        try {
            return handleResponse(response, ChessGame.class);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }


    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if (authToken != null && !authToken.isBlank()) {
            request.header("authorization", authToken);
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws Exception {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Type responseType) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw ResponseException.fromJson(body);
            }

            throw new ResponseException(ResponseException.fromHttpStatusCode(status), "other failure: " + status);
        }

        if (responseType != null) {
            return new Gson().fromJson(response.body(), responseType);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}

class GameListResponse {
    ArrayList<GameData> games;
}