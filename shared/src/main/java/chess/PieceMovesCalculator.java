package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PieceMovesCalculator {

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            BishopMovesCalculator Bishop = new BishopMovesCalculator();
            return Bishop.pieceMoves(board, myPosition, piece);
        } else if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            KingMovesCalculator King = new KingMovesCalculator();
            return King.pieceMoves(board, myPosition, piece);
        } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            throw new RuntimeException("Not implemented");
        } else if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            throw new RuntimeException("Not implemented");
        } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            QueenMovesCalculator Queen = new QueenMovesCalculator();
            return Queen.pieceMoves(board, myPosition, piece);
        } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            throw new RuntimeException("Not implemented");
        }
        return List.of();
    }

    public boolean checkDifferentColors(ChessPiece otherPiece, ChessPiece myPiece) {
        return otherPiece.getTeamColor() != myPiece.getTeamColor();
    }
}


class BishopMovesCalculator extends PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        List<ChessMove> moveList = new ArrayList<>();
        int[][] diagonalDirections = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        for (int[] dir: diagonalDirections) {
            for (int i = 1; i < 8 ; i++) { // dir[0] is rowDirection, dir[1] is colDirection
                int newX = myPosition.getRow() + (i * dir[0]);
                int newY = myPosition.getColumn() + (i * dir[1]);
                if (newX > 0 && newX <= 8 && newY > 0 && newY <= 8) {
                    ChessPosition newPosition = new ChessPosition(newX, newY);
                    if ((board.getPiece(newPosition) != null)) {
                        if (checkDifferentColors(board.getPiece(newPosition), piece)) {
                            moveList.add(new ChessMove(myPosition, newPosition, null));
                        }
                        break;
                    } else {
                        moveList.add(new ChessMove(myPosition, newPosition, null));
                    }
                } else {
                    break;
                }
            }
        }
        return moveList;
    }
}

class KingMovesCalculator extends PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        List<ChessMove> moveList = new ArrayList<>();
        int[][] moveDirections = {{-1, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}};
        for (int[] dir: moveDirections) { // dir[0] rowDirection, dir[1] colDirection
            int newX = myPosition.getRow() + dir[0];
            int newY = myPosition.getColumn() + dir[1];
            if (newX > 0 && newX <= 8 && newY > 0 && newY <= 8) {
                ChessPosition newPosition = new ChessPosition(newX, newY);
                if (board.getPiece(newPosition) != null) {
                    if (checkDifferentColors(board.getPiece(newPosition), piece)) {
                        moveList.add(new ChessMove(myPosition, newPosition, null));
                    }
                } else {
                    moveList.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }
        return moveList;
    }
}

class KnightMovesCalculator extends PieceMovesCalculator {

}

class PawnMovesCalculator extends PieceMovesCalculator {

}

class QueenMovesCalculator extends PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        List<ChessMove> moveList = new ArrayList<>();
        int[][] moveDirections = {{-1, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}};
        for (int[] dir: moveDirections) { // dir[0] rowDirection, dir[1] colDirection
            for (int i = )
                int newX = myPosition.getRow() + dir[0];
                int newY = myPosition.getColumn() + dir[1];
                if (newX > 0 && newX <= 8 && newY > 0 && newY <= 8) {
                    ChessPosition newPosition = new ChessPosition(newX, newY);
                    if (board.getPiece(newPosition) != null) {
                        if (checkDifferentColors(board.getPiece(newPosition), piece)) {
                            moveList.add(new ChessMove(myPosition, newPosition, null));
                        }
                        break;
                    } else {
                        moveList.add(new ChessMove(myPosition, newPosition, null));
                    }
                } else {
                    break;
                }
        }
        return moveList;
    }
}

class RookMovesCalculator extends PieceMovesCalculator {

}