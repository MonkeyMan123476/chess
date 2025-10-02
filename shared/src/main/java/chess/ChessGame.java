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
    ChessBoard myBoard;
    public ChessGame() {

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
                System.out.println("yeah this move won't put king in check.");
                System.out.println(move);
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
            System.out.println("No piece to move");
            throw new InvalidMoveException("No piece to move");
        } else if (getBoard().getPiece(move.getStartPosition()).getTeamColor() != getTeamTurn()) {
            System.out.println("wrong team");
            throw new InvalidMoveException("Not this team's turn");
        } else if (validMoves(move.getStartPosition()) != null && validMoves(move.getStartPosition()).contains(move)) {
            System.out.println("moving piece");
            movePiece(getBoard().getPiece(move.getStartPosition()), move.getStartPosition(), move.getEndPosition(), move.getPromotionPiece());
        } else if (willBeInCheck(move)){
            System.out.println("king still in check");
            throw new InvalidMoveException("King may not remain in check");
        } else {
            System.out.println("nah");
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
        } else {
            teamTurn = TeamColor.WHITE;
        }
    }

    public boolean willBeInCheck(ChessMove testMove) {
        ChessBoard testBoard = null;
        testBoard = copyBoard();
        if (testMove.getPromotionPiece() != null) {
            // Move Pawn
            testBoard.squares[testMove.getEndPosition().getRow() - 1][testMove.getEndPosition().getColumn() - 1] = new ChessPiece(getTeamTurn(), testMove.getPromotionPiece());
            testBoard.squares[testMove.getStartPosition().getRow() - 1][testMove.getStartPosition().getColumn() - 1] = null;
        } else {
            // Move Other Pieces
            testBoard.squares[testMove.getEndPosition().getRow() - 1][testMove.getEndPosition().getColumn() - 1] = testBoard.getPiece(testMove.getStartPosition());
            testBoard.squares[testMove.getStartPosition().getRow() - 1][testMove.getStartPosition().getColumn() - 1] = null;
        }
        return canKillKing(testBoard, getTeamTurn());
    }

    public ChessBoard copyBoard() {
        ChessBoard newBoard = new ChessBoard();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                if (getBoard().getPiece(position) != null) {
                    newBoard.addPiece(position, new ChessPiece(getBoard().getPiece(position).getTeamColor(), (getBoard().getPiece(position).getPieceType())));
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
                    for (ChessMove potentialMove: potentialMoves) {
                        System.out.println("potential move: " + potentialMove);
                        ChessPiece killedPiece = checkingBoard.getPiece(potentialMove.getEndPosition());
                        if (killedPiece != null && killedPiece.getPieceType() == ChessPiece.PieceType.KING && killedPiece.getTeamColor() == teamColor) {
                            System.out.println("this is gonna kill king");
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}