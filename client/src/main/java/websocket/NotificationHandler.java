package websocket;

import websocket.messages.*;

public interface NotificationHandler {
    void notify(NotificationMessage notification);
    void loadGame(LoadGameMessage loadGameMessage);
    void error(ErrorMessage errorMessage);
}