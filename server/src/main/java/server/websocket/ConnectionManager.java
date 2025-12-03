package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    public static class ConnectionInfo {
        public Session session;
        public String username;
        public String authToken;
        public int gameID;
        public boolean isPlayer;
    }

    public final ConcurrentHashMap<Session, ConnectionInfo> connections = new ConcurrentHashMap<>();

    public void add(Session session, String username, String token, int gameID, boolean isPlayer) {
        ConnectionInfo info = new ConnectionInfo();
        info.session = session;
        info.username = username;
        info.authToken = token;
        info.gameID = gameID;
        info.isPlayer = isPlayer;
        connections.put(session, info);
    }

    public void remove(Session session) {
        connections.remove(session);
    }

    public void broadcast(Session excludeSession, ServerMessage message) throws IOException {
        String json = message.toString();

        for (ConnectionInfo c : connections.values()) {
            if (c.session.isOpen()) {
                if (c.session != excludeSession) {
                    c.session.getRemote().sendString(json);
                }
            }
        }
    }

    public ConnectionInfo get(Session session) {
        return connections.get(session);
    }
}