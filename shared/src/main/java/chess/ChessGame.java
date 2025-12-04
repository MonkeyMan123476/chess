package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    TeamColor teamTurn = TeamColor.WHITE;
    GameState gameState = GameState.NORMAL;
    ChessBoard myBoard = new ChessBoard();
    public ChessGame() {
        this.myBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    public GameState getGameState() {
        return gameState;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(myBoard, chessGame.myBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, myBoard);
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    public enum GameState {
        CHECK,
        STALEMATE,
        CHECKMATE,
        NORMAL
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (getBoard().getPiece(startPosition) == null) {
            return null;
        }
        List<ChessMove> validMoveList = new ArrayList<>();
        for (ChessMove move: getBoard().getPiece(startPosition).pieceMoves(getBoard(), startPosition)) {
            if (!willBeInCheck(move)) {
                validMoveList.add(move);
            }
        }
        return validMoveList;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (getBoard().getPiece(move.getStartPosition()) == null) {
            throw new InvalidMoveException("No piece to move");
        } else if (getBoard().getPiece(move.getStartPosition()).getTeamColor() != getTeamTurn()) {
            throw new InvalidMoveException("Not this team's turn");
        } else if (validMoves(move.getStartPosition()) != null && validMoves(move.getStartPosition()).contains(move)) {
            movePiece(getBoard().getPiece(move.getStartPosition()), move.getStartPosition(), move.getEndPosition(), move.getPromotionPiece());
            if (isInCheckmate(getTeamTurn())) {
                gameState = GameState.CHECKMATE;
            } else if (isInStalemate(getTeamTurn())) {
                gameState = GameState.STALEMATE;
            } else if (isInCheck(getTeamTurn())) {
                gameState = GameState.CHECK;
            } else {
                gameState = GameState.NORMAL;
            }
        } else if (willBeInCheck(move)){
            throw new InvalidMoveException("King may not remain in check");
        } else {
            throw new InvalidMoveException("Invalid move");
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return canKillKing(myBoard, teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return (canKillKing(myBoard, teamColor) && noValidMoves(teamColor));
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return (!isInCheck(teamColor) && noValidMoves(teamColor));
    }

    public boolean noValidMoves(ChessGame.TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition testPosition = new ChessPosition(row, col);
                ChessPiece testPiece = getBoard().getPiece(testPosition);
                if (testPiece != null && testPiece.getTeamColor() == teamColor && !validMoves(testPosition).isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        myBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return myBoard;
    }

    public void movePiece(ChessPiece piece, ChessPosition oldPosition, ChessPosition newPosition, ChessPiece.PieceType promotion) {
        if (promotion != null) {
            // Move Pawn
            getBoard().squares[newPosition.getRow() - 1][newPosition.getColumn() - 1] = new ChessPiece(getTeamTurn(), promotion);
            getBoard().squares[oldPosition.getRow() - 1][oldPosition.getColumn() - 1] = null;
        } else {
            // Move Other Pieces
            getBoard().squares[newPosition.getRow() - 1][newPosition.getColumn() - 1] = piece;
            getBoard().squares[oldPosition.getRow() - 1][oldPosition.getColumn() - 1] = null;
        }
        // Switch turns
        if (getTeamTurn() == TeamColor.WHITE) {
            teamTurn = TeamColor.BLACK;
        } else if (getTeamTurn() == TeamColor.BLACK){
            teamTurn = TeamColor.WHITE;
        }
    }

    public boolean willBeInCheck(ChessMove testMove) {
        ChessBoard testBoard = null;
        testBoard = copyBoard();
        if (testMove.getPromotionPiece() != null) {
            // Move Pawn
            ChessPiece movingPawn = new ChessPiece(getTeamTurn(), testMove.getPromotionPiece());
            testBoard.squares[testMove.getEndPosition().getRow() - 1][testMove.getEndPosition().getColumn() - 1] = movingPawn;
            testBoard.squares[testMove.getStartPosition().getRow() - 1][testMove.getStartPosition().getColumn() - 1] = null;
        } else {
            // Move Other Pieces
            ChessPiece movingPiece = testBoard.getPiece(testMove.getStartPosition());
            testBoard.squares[testMove.getEndPosition().getRow() - 1][testMove.getEndPosition().getColumn() - 1] = movingPiece;
            testBoard.squares[testMove.getStartPosition().getRow() - 1][testMove.getStartPosition().getColumn() - 1] = null;
        }
        return canKillKing(testBoard, getBoard().getPiece(testMove.getStartPosition()).getTeamColor());
    }

    public ChessBoard copyBoard() {
        ChessBoard newBoard = new ChessBoard();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                if (getBoard().getPiece(position) != null) {
                    ChessGame.TeamColor color = getBoard().getPiece(position).getTeamColor();
                    ChessPiece.PieceType type = getBoard().getPiece(position).getPieceType();
                    ChessPiece newPiece = new ChessPiece(color, type);
                    newBoard.addPiece(position, newPiece);
                }
            }
        }
        return newBoard;
    }

    public boolean canKillKing(ChessBoard checkingBoard, TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition testPosition = new ChessPosition(row, col);
                ChessPiece testPiece = checkingBoard.getPiece(testPosition);
                if (testPiece != null && testPiece.getTeamColor() != teamColor) {
                    Collection<ChessMove> potentialMoves = testPiece.pieceMoves(checkingBoard, testPosition);
                    if (canPotentialMovesKillKing(checkingBoard, teamColor, potentialMoves)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean canPotentialMovesKillKing(ChessBoard checkingBoard, TeamColor teamColor, Collection<ChessMove> potentialMoves) {
        for (ChessMove potentialMove: potentialMoves) {
            ChessPiece killedPiece = checkingBoard.getPiece(potentialMove.getEndPosition());
            if (killedPiece != null && killedPiece.getPieceType() == ChessPiece.PieceType.KING && killedPiece.getTeamColor() == teamColor) {
                return true;
            }
        }
        return false;
    }
}