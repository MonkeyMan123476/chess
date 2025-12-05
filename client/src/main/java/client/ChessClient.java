package client;

import chess.*;
import datamodel.GameData;
import exception.ResponseException;
import server.ServerFacade;
import websocket.*;
import ui.EscapeSequences;
import websocket.messages.*;

import java.util.*;

public class ChessClient implements NotificationHandler {
    private final ServerFacade server;
    private final WebSocketFacade ws;
    private State state = State.SIGNEDOUT;
    private String authToken;
    private List<Integer> gameNumbers;
    private int myGameID;
    private ChessGame.TeamColor myTeam;
    private String myUsername;


    public ChessClient(String serverUrl) throws Exception {
        server = new ServerFacade(serverUrl);
        ws = new WebSocketFacade(serverUrl, (NotificationHandler) this);
        authToken = "";
        gameNumbers = new ArrayList<>();
        myGameID = 0;
        myTeam = null;
        myUsername = null;
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
        System.out.print("\n" + EscapeSequences.SET_TEXT_COLOR_BLUE);
        System.out.print(EscapeSequences.RESET_TEXT_BLINKING + ">>> " + EscapeSequences.SET_TEXT_COLOR_GREEN);
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
            } else if (state == State.SIGNEDIN){
                return switch (cmd) {
                    case "help" -> help();
                    case "logout" -> logout();
                    case "create" -> createGame();
                    case "list" -> listGames();
                    case "play" -> playGame();
                    case "observe" -> observeGame();
                    default -> "♕ Type Help to see what actions you can take." + EscapeSequences.WHITE_QUEEN + "\n";
                };
            } else if (state == State.GAMEPLAY && server.getGame(myGameID).game().getTeamTurn() == myTeam) {
                return switch (cmd) {
                    case "help" -> help();
                    case "redraw" -> drawBoard(myTeam, server.getGame(myGameID).game().getBoard(), null);
                    case "leave" -> leave();
                    case "move" -> move();
                    case "resign" -> resign();
                    case "highlight" -> highlight();
                    default -> "♕ Type Help to see what actions you can take." + EscapeSequences.WHITE_QUEEN + "\n";
                };
            } else if (state == State.OBSERVING || state == State.GAMEOVER) {
                return switch (cmd) {
                    case "help" -> help();
                    case "redraw" -> drawBoard(ChessGame.TeamColor.WHITE, server.getGame(myGameID).game().getBoard(), null);
                    case "leave" -> leave();
                    case "highlight" -> highlight();
                    default -> "♕ Type Help to see what actions you can take." + EscapeSequences.WHITE_QUEEN + "\n";
                };
            } else if (state == State.GAMEPLAY) {
                return switch (cmd) {
                    case "help" -> help();
                    case "redraw" -> drawBoard(myTeam, server.getGame(myGameID).game().getBoard(), null);
                    case "leave" -> leave();
                    case "resign" -> resign();
                    case "highlight" -> highlight();
                    default -> "♕ Type Help to see what actions you can take." + EscapeSequences.WHITE_QUEEN + "\n";
                };
            }
            return "broke the client bruh";
        } catch (Exception e) {
            return "Error: " + e.getMessage() + "\n" + help();
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
            myUsername = username;
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
            myUsername = username;
            String returnStatement = String.format("You registered and signed in as %s.\n", username);
            return returnStatement + help();
        } catch (Exception e) {
            return "Unable to register. Please choose a new username and password.\n" + help();
        }
    }

    public String logout() {
        try {
            server.logout(authToken);
            authToken = "";
            state = State.SIGNEDOUT;
            myUsername = null;
            return "You signed out.\n" + help();
        } catch (Exception e) {
            return "Unauthorized\n" + help();
        }
    }

    public String createGame() {
        Scanner scanner = new Scanner(System.in);
        try {
            for (int i = 1; i <= server.listGames(authToken).size(); i++) {
                gameNumbers.add(i);
            }
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
            ArrayList<GameData> gameList = server.listGames(authToken);
            String listPrinted = "\n" + EscapeSequences.SET_TEXT_COLOR_MAGENTA;
            gameNumbers.clear();
            int counter = 1;
            if (gameList.isEmpty()) {
                listPrinted += "No games have been created.\n\n";
            }
            for (GameData game : gameList) {
                gameNumbers.add(counter);
                listPrinted += "\nGame Number: " + counter + "\nName: " + game.gameName() + "\nWhite Player: ";
                if (game.whiteUsername() != null) {
                    listPrinted += game.whiteUsername();
                }
                listPrinted += "\nBlack Player: ";
                if (game.blackUsername() != null) {
                    listPrinted += game.blackUsername();
                }
                listPrinted += "\n";
                counter++;
            }
            return listPrinted + "\n" + EscapeSequences.SET_TEXT_COLOR_BLUE + help();
        } catch (Exception e) {
            return "Unable to list games." + EscapeSequences.SET_TEXT_COLOR_BLUE + help();
        }
    }

    public String playGame() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Enter the game number you would like to join: ");
            int gameNumber = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter the team you would like to play as (White or Black): ");
            ChessGame.TeamColor team = ChessGame.TeamColor.valueOf(scanner.nextLine().toUpperCase());
            GameData gameJoined = server.getGame(gameNumber);
            myGameID = gameJoined.gameID();
            myTeam = team;
            server.joinGame(authToken, gameNumber, team);
            if (gameJoined.game().getGameState() == ChessGame.GameState.CHECKMATE) {
                state = State.GAMEOVER;
            } else if (gameJoined.game().getGameState() == ChessGame.GameState.STALEMATE) {
                state = State.GAMEOVER;
            } else {
                state = State.GAMEPLAY;
            }
            ws.joinGame(authToken, myGameID);
            return String.format("You joined %s as the %s team\n", gameJoined.gameName(), team);
        } catch (Exception e) {
            return "Unable to join game. Please enter a valid game number and empty team color.\n" + EscapeSequences.SET_TEXT_COLOR_BLUE + help();
        }
    }

    public String observeGame() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Enter the game number you would like to observe: ");
            int gameNumber = Integer.parseInt(scanner.nextLine());
            GameData gameObserving = server.getGame(gameNumber);
            myGameID = gameObserving.gameID();
            myTeam = null;
            state = State.OBSERVING;
            ws.observeGame(authToken, myGameID);
            return String.format("You are now observing %s\n", gameObserving.gameName());
        } catch (Exception e) {
            return "Unable to observe game. Please enter a valid game number.\n" + help();
        }
    }

    public String move() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Please enter a chess move (example: a2a4): ");
            String moveInput = scanner.nextLine().trim().toLowerCase();
            if (moveInput.length() < 4) {
                return "Invalid input. Please enter a move in a format similar to a2a4.\n" + help();
            }
            int pieceColumn = columnToInteger(String.valueOf(moveInput.charAt(0)));
            int pieceRow = Character.getNumericValue(moveInput.charAt(1));
            int positionColumn = columnToInteger(String.valueOf(moveInput.charAt(2)));
            int positionRow = Character.getNumericValue(moveInput.charAt(3));
            ChessPosition oldPosition = new ChessPosition(pieceRow, pieceColumn);
            ChessPiece movingPiece = server.getGame(myGameID).game().getBoard().getPiece(oldPosition);
            ChessPosition newPosition = new ChessPosition(positionRow, positionColumn);
            ChessPiece.PieceType newType = null;
            if (movingPiece.getPieceType() == ChessPiece.PieceType.PAWN && (newPosition.getRow() == 1 || newPosition.getRow() == 8)) {
                System.out.println("How would you like to promote your pawn? (Queen, Knight, Rook, or Bishop): ");
                newType = ChessPiece.PieceType.valueOf(scanner.nextLine().toUpperCase());
            }
            ChessMove attemptedMove = new ChessMove(oldPosition, newPosition, newType);
            ws.makeMove(authToken, myGameID, attemptedMove);
            return "Move sent to server. Waiting for confirmation...";
        } catch (Exception e) {
            return "Unable to move piece. Please select a valid piece and square to move to.\n" + help();
        }
    }

    public String highlight() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Please enter a chess position (example: a2): ");
            String moveInput = scanner.nextLine().trim().toLowerCase();
            if (moveInput.length() < 2) {
                return "Invalid input. Please enter a position in a format similar to a2.\n" + help();
            }
            int pieceColumn = columnToInteger(String.valueOf(moveInput.charAt(0)));
            int pieceRow = Character.getNumericValue(moveInput.charAt(1));
            ChessPosition position = new ChessPosition(pieceRow, pieceColumn);
            return drawBoard(myTeam, server.getGame(myGameID).game().getBoard(), position);
        } catch (Exception e) {
            return "Unable to highlight pieces.\n" + help();
        }
    }

    public String leave() throws ResponseException {
        ws.leaveGame(authToken, myGameID);
        state = State.SIGNEDIN;
        return "You left the game.\n" + help();
    }

    public String resign() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Please type 'resign' again to confirm your resignation: ");
            String confirmation = scanner.nextLine().toLowerCase();
            if (!confirmation.contains("resign")) {
                return "Resignation cancelled.\n" + help();
            }
            ws.resign(authToken, myGameID);
            state = State.GAMEOVER;
            return "You resigned and forfeited the game.";
        } catch (Exception e) {
            return "Unable to resign.\n" + help();
        }
    }

    public int columnToInteger(String columnLetter) {
        char c = columnLetter.toLowerCase().charAt(0);
        if (c < 'a' || c > 'h') {
            return 0;
        }
        return c - 'a' + 1;
    }

    public String help(){
        String base = """
                        - Help
                        - Redraw - show the chess board
                        - Leave - leave the game
                        """;
        try {
            if (state == State.SIGNEDOUT) {
                return """
                        - Help
                        - Quit
                        - Login - to play chess
                        - Register - to create an account
                        """;
            } else if (state == State.SIGNEDIN) {
                return """
                        - Help
                        - Logout
                        - Create - create a new game
                        - List - see games you can join
                        - Play - join a game
                        - Observe - observe a game
                        """;
            } else if (state == State.OBSERVING) {
                return base + """
                        - Highlight - show legal moves
                        """;
            } else if (state == State.GAMEOVER) {
                return """
                        Game Over
                        """ + base;
            } else if (server.getGame(myGameID).game().getTeamTurn() == myTeam) {
                return base + """
                        - Move - make a chess move
                        - Resign - forfeit the game
                        - Highlight - show legal moves
                        """;
            }
        } catch (Exception e) {
            return "Client error. Thank you for your patience.\n" + help();
        }
        return base + """
                - Highlight - show legal moves
                - Resign - forfeit the game
                """;
    }


    private String drawBoard(ChessGame.TeamColor perspective, ChessBoard board, ChessPosition highlightPosition) {
        boolean shouldHighlight;
        String drawnBoard = "\n";
        try {
            if (perspective == ChessGame.TeamColor.BLACK) {
                drawnBoard += blackColumnLabel();
                for (int row = 1; row <= 8; row++) {
                    drawnBoard += EscapeSequences.SET_BG_COLOR_MAGENTA + EscapeSequences.SET_TEXT_COLOR_BLACK + " " + row + " ";
                    for (int col = 8; col >= 1; col--) {
                        shouldHighlight = highlightPosition != null && shouldHighlightSquare(row, col, highlightPosition);
                        drawnBoard += makeSquare(board, row, col, shouldHighlight);
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
                        shouldHighlight = highlightPosition != null && shouldHighlightSquare(row, col, highlightPosition);
                        drawnBoard += makeSquare(board, row, col, shouldHighlight);
                    }
                    drawnBoard += EscapeSequences.SET_BG_COLOR_MAGENTA + EscapeSequences.SET_TEXT_COLOR_BLACK + " " + row + " ";
                    drawnBoard += EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR + "\n";
                }
                drawnBoard += whiteColumnLabel();
            }
            return drawnBoard + EscapeSequences.SET_TEXT_COLOR_BLUE;
        } catch (Exception e) {
            return "Unable to draw board.\n" + help();
        }
    }

    private boolean shouldHighlightSquare(int row, int col, ChessPosition position) throws Exception {
        ChessMove potentialMove = new ChessMove(position, new ChessPosition(row, col), null);
        if (server.getGame(myGameID).game().validMoves(position).contains(potentialMove)) {
            return true;
        }
        return false;
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
        label += "a" + " \u2003" + "b" + " \u2003" + "c" + " \u2003" + "d" + " \u2003";
        label += "e" + " \u2003" + "f" + " \u2003" + "g" + " \u2003" + "h" + " \u2003";
        label += "  " + EscapeSequences.RESET_BG_COLOR  + EscapeSequences.RESET_TEXT_COLOR + "\n";
        return label;
    }

    private String makeSquare(ChessBoard board, int row, int col, boolean shouldHighlight) {
        String chessSquare = "";
        boolean isWhiteSquare = !((row + col) % 2 == 0);
        String bgColor = isWhiteSquare ? EscapeSequences.SET_BG_COLOR_WHITE : EscapeSequences.SET_BG_COLOR_BLUE;
        if (shouldHighlight) {
            bgColor = isWhiteSquare ? EscapeSequences.SET_BG_COLOR_GREEN : EscapeSequences.SET_BG_COLOR_DARK_GREEN;
        }
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
        boolean white = piece.getTeamColor() == ChessGame.TeamColor.WHITE;

        return switch (piece.getPieceType()) {
            case BISHOP -> white ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case KING -> white ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
            case KNIGHT -> white ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case PAWN -> white ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
            case QUEEN -> white ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case ROOK -> white ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
        };
    }

    @Override
    public void notify(NotificationMessage notification) {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_MAGENTA + notification.message);
        printPrompt();
    }

    @Override
    public void loadGame(LoadGameMessage loadGameMessage) {
        ChessGame.GameState stateOfGame = loadGameMessage.game.getGameState();
        if (stateOfGame == ChessGame.GameState.CHECKMATE ||
                stateOfGame == ChessGame.GameState.STALEMATE ||
                stateOfGame == ChessGame.GameState.RESIGNED) {
            state = State.GAMEOVER;
        }
        System.out.println(drawBoard(myTeam, loadGameMessage.game.getBoard(), null));
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + help());
        printPrompt();
    }

    @Override
    public void error(ErrorMessage errorMessage) {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + errorMessage.errorMessage);
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + help());
        printPrompt();
    }
}