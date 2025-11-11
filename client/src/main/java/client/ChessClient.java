package client;


import server.ServerFacade;
import ui.EscapeSequences;

import java.util.Arrays;
import java.util.Scanner;

public class ChessClient {
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;


    public ChessClient(String serverUrl) throws Exception {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println("♕ Welcome to Chess. Type Help to get started." + EscapeSequences.WHITE_QUEEN + "\n");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("Help")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + EscapeSequences.RESET_TEXT_BLINKING + ">>> " + EscapeSequences.SET_TEXT_COLOR_GREEN);
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
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
