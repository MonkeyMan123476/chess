package client;


import chess.*;
import datamodel.GameData;
import server.ServerFacade;
import ui.EscapeSequences;

import java.util.Arrays;
import java.util.List;
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
        System.out.print("\n" + EscapeSequences.SET_TEXT_COLOR_BLUE + EscapeSequences.RESET_TEXT_BLINKING + ">>> " + EscapeSequences.SET_TEXT_COLOR_GREEN);
    }

    public String eval(String input) {
        ChessBoard testDrawBoard = new ChessBoard();
        testDrawBoard.resetBoard();
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
                    case "list" -> listGames();
                    case "play" -> playGame();
                    //case "observe" -> observeGame();
                    //for testing
                    case "drawwhite" -> drawBoard(ChessGame.TeamColor.WHITE, testDrawBoard);
                    case "drawblack" -> drawBoard(ChessGame.TeamColor.BLACK, testDrawBoard);
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
            server.createGame(authToken, gameName);
            String returnStatement = String.format("You created game %s.\n", gameName);
            return returnStatement + help();
        } catch (Exception e) {
            return "Unable to create game. Please choose a new game name.\n" + help();
        }
    }

    public String listGames() {
        try {
            List<GameData> gameList = server.listGames(authToken);
            System.out.println(gameList);
            return "put list here";
        } catch (Exception e) {
            return "Unable to list games.";
        }
    }

    public String playGame() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Enter the game number you would like to join: ");
            int gameNumber = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter the team you would like to play as (White or Black): ");
            ChessGame.TeamColor team = ChessGame.TeamColor.valueOf(scanner.nextLine().toUpperCase());
            ChessBoard board = server.getGame(gameNumber).getBoard();
            server.joinGame(authToken, gameNumber, team);
            String returnStatement = String.format("You joined game %s as the %s team\n", gameNumber, team);
            return returnStatement + drawBoard(team, board);
        } catch (Exception e) {
            return "Unable to join game. Please enter a valid game number\n" + help();
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

    private String drawBoard(ChessGame.TeamColor perspective, ChessBoard board) {
        String drawnBoard = "";
        if (perspective == ChessGame.TeamColor.BLACK) {
            drawnBoard += blackColumnLabel();
            for (int row = 1; row <= 8; row++) {
                drawnBoard += EscapeSequences.SET_BG_COLOR_MAGENTA + EscapeSequences.SET_TEXT_COLOR_BLACK + " " + row + " ";
                for (int col = 8; col >= 1; col--) {
                    drawnBoard += makeSquare(board, row, col);
                }
                drawnBoard += EscapeSequences.SET_BG_COLOR_MAGENTA + EscapeSequences.SET_TEXT_COLOR_BLACK + " " + row + " ";
                drawnBoard += EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR + "\n";
            }
            drawnBoard += blackColumnLabel();
        } else {
            drawnBoard += whiteColumnLabel();
            for (int row = 8; row >= 1; row--) {
                drawnBoard += EscapeSequences.SET_BG_COLOR_MAGENTA + EscapeSequences.SET_TEXT_COLOR_BLACK + " " + row + " ";
                for (int col = 1; col <= 8; col++) {
                    drawnBoard += makeSquare(board, row, col);
                }
                drawnBoard += EscapeSequences.SET_BG_COLOR_MAGENTA + EscapeSequences.SET_TEXT_COLOR_BLACK + " " + row + " ";
                drawnBoard += EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR + "\n";
            }
            drawnBoard += whiteColumnLabel();
        }
        return drawnBoard;
    }

    private String blackColumnLabel() {
        String label = EscapeSequences.SET_BG_COLOR_MAGENTA + EscapeSequences.SET_TEXT_COLOR_BLACK + "    ";
        label += "h" + " \u2003";
        label += "g" + " \u2003";
        label += "f" + " \u2003";
        label += "e" + " \u2003";
        label += "d" + " \u2003";
        label += "c" + " \u2003";
        label += "b" + " \u2003";
        label += "a" + " \u2003";
        label += "  " + EscapeSequences.RESET_BG_COLOR  + EscapeSequences.RESET_TEXT_COLOR + "\n";
        return label;
    }

    private String whiteColumnLabel() {
        String label = EscapeSequences.SET_BG_COLOR_MAGENTA + EscapeSequences.SET_TEXT_COLOR_BLACK + "    ";
        label += "a" + " \u2003";
        label += "b" + " \u2003";
        label += "c" + " \u2003";
        label += "d" + " \u2003";
        label += "e" + " \u2003";
        label += "f" + " \u2003";
        label += "g" + " \u2003";
        label += "h" + " \u2003";
        label += "  " + EscapeSequences.RESET_BG_COLOR  + EscapeSequences.RESET_TEXT_COLOR + "\n";
        return label;
    }

    private String makeSquare(ChessBoard board, int row, int col) {
        String chessSquare = "";
        boolean isWhiteSquare = !((row + col) % 2 == 0);
        String bgColor = isWhiteSquare ? EscapeSequences.SET_BG_COLOR_WHITE : EscapeSequences.SET_BG_COLOR_BLUE;
        ChessPiece piece = board.getPiece(new ChessPosition(row, col));
        chessSquare += bgColor + EscapeSequences.SET_TEXT_COLOR_BLACK;
        if (piece != null) {
            chessSquare += pieceIcon(piece);
        } else {
            chessSquare += EscapeSequences.EMPTY;
        }
        chessSquare += EscapeSequences.RESET_BG_COLOR;
        return chessSquare;
    }

    private String pieceIcon(ChessPiece piece) {
        switch (piece.getPieceType()) {
            case BISHOP:
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    return EscapeSequences.WHITE_BISHOP;
                }
                return EscapeSequences.BLACK_BISHOP;
            case KING:
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    return EscapeSequences.WHITE_KING;
                }
                return EscapeSequences.BLACK_KING;
            case KNIGHT:
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    return EscapeSequences.WHITE_KNIGHT;
                }
                return EscapeSequences.BLACK_KNIGHT;
            case PAWN:
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    return EscapeSequences.WHITE_PAWN;
                }
                return EscapeSequences.BLACK_PAWN;
            case QUEEN:
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    return EscapeSequences.WHITE_QUEEN;
                }
                return EscapeSequences.BLACK_QUEEN;
            case ROOK:
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                  return EscapeSequences.WHITE_ROOK;
                }
                return EscapeSequences.BLACK_ROOK;
            default:
                return EscapeSequences.EMPTY;
        }
    }
}
