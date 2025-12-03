package websocket.messages;

public class ErrorMessage extends ServerMessage {

    public String errorMessage;

    public ErrorMessage(String errorMessage) {
        super(ServerMessageType.ERROR);
        if (!errorMessage.toLowerCase().contains("error")) {
            errorMessage = "Error: " + errorMessage;
        }
        this.errorMessage = errorMessage;
    }


}
