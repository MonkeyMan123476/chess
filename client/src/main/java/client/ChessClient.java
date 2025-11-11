package client;


import server.ServerFacade;

public class ChessClient {
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;


    public ChessClient(String serverUrl) throws Exception {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println(" Welcome to Chess. Here are your options: ");
        System.out.println(help());
    }







    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - Help
                    - Login
                    - Register
                    - Quit
                    """;
        }
        return """
                - Help
                - Logout
                - Create Game
                - List Games
                - Play Game
                - Observe Game
                """;
    }
}
