package client;


import server.ServerFacade;
import ui.EscapeSequences;

import java.util.Arrays;
import java.util.Scanner;

public class ChessClient {
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;
    private String authToken;


    public ChessClient(String serverUrl) throws Exception {
        server = new ServerFacade(serverUrl);
        authToken = "";
    }

    public void run() {
        System.out.println("♕ Welcome to Chess. Type Help to get started." + EscapeSequences.WHITE_QUEEN + "\n");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (true) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                if (result.equalsIgnoreCase("quit")) {
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + "Goodbye!");
                    break;
                }
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
            if (state == State.SIGNEDOUT) {
                return switch (cmd) {
                    case "help" -> help();
                    case "login" -> login();
                    case "register" -> register();
                    case "quit" -> "quit";
                    default -> "♕ Welcome to Chess. Type Help to get started." + EscapeSequences.WHITE_QUEEN + "\n";
                };
            } else {
                return switch (cmd) {
                    case "help" -> help();
                    case "logout" -> logout();
                    case "create" -> createGame();
                    //case "list games" -> listGames();
                    //case "play game" -> playGame();
                    //case "observe game" -> observeGame();
                    default -> "♕ Type Help to see what actions you can take." + EscapeSequences.WHITE_QUEEN + "\n";
                };
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String login() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();
            authToken = server.login(username, password).authToken();
            state = State.SIGNEDIN;
            String returnStatement = String.format("You signed in as %s.\n", username);
            return returnStatement + help();
        } catch (Exception e) {
            return "Unable to login. Please check your username and password.\n" + help();
        }
    }

    public String register() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();
            System.out.print("Enter email: ");
            String email = scanner.nextLine();
            authToken = server.register(username, password, email).authToken();
            state = State.SIGNEDIN;
            String returnStatement = String.format("You registered and signed in as %s.\n", username);
            return returnStatement + help();
        } catch (Exception e) {
            return "Unable to register. Please choose a new username.\n" + help();
        }
    }

    public String logout() {
        try {
            server.logout(authToken);
            authToken = "";
            state = State.SIGNEDOUT;
            return "You signed out.\n" + help();
        } catch (Exception e) {
            return "Unauthorized\n" + help();
        }
    }

    public String createGame() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Name your game: ");
            String gameName = scanner.nextLine();
            var GameData = server.createGame(authToken, gameName);
            String returnStatement = String.format("You created game %s with the gameID %s.\n", gameName, GameData.gameID());
            return returnStatement + help();
        } catch (Exception e) {
            return "Unable to create game. Please choose a new game name.\n" + help();
        }
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - Help
                    - Quit
                    - Login - to play chess
                    - Register - to create an account
                    """;
        }
        return """
                - Help
                - Logout
                - Create - create a game
                - List - list all games
                - Play - play a game
                - Observe - observe a game
                """;
    }

    private void assertSignedIn() throws Exception {
        if (state == State.SIGNEDOUT) {
            throw new Exception("You must sign in");
        }
    }
}
