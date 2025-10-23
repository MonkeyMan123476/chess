package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PieceMovesCalculator {

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        switch (piece.getPieceType()) {
            case BISHOP:
                BishopMovesCalculator bishop = new BishopMovesCalculator();
                return bishop.pieceMoves(board, myPosition, piece);
            case KING:
                KingMovesCalculator king = new KingMovesCalculator();
                return king.pieceMoves(board, myPosition, piece);
            case KNIGHT:
                KnightMovesCalculator knight = new KnightMovesCalculator();
                return knight.pieceMoves(board, myPosition, piece);
            case PAWN:
                PawnMovesCalculator pawn = new PawnMovesCalculator();
                return pawn.pieceMoves(board, myPosition, piece);
            case QUEEN:
                QueenMovesCalculator queen = new QueenMovesCalculator();
                return queen.pieceMoves(board, myPosition, piece);
            case ROOK:
                RookMovesCalculator rook = new RookMovesCalculator();
                return rook.pieceMoves(board, myPosition, piece);
            default:
                return List.of();
        }
    }

    public boolean checkDifferentColors(ChessPiece otherPiece, ChessPiece myPiece) {
        return otherPiece.getTeamColor() != myPiece.getTeamColor();
    }

    public void ifOpenAddSingle(ChessBoard board, ChessPosition myPosition, ChessPiece piece, Collection<ChessMove> moves, int x, int y) {
        if (x > 0 && x <= 8 && y > 0 && y <= 8) {
            ChessPosition newPosition = new ChessPosition(x, y);
            if (board.getPiece(newPosition) != null) {
                if (checkDifferentColors(board.getPiece(newPosition), piece)) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
            } else {
                moves.add(new ChessMove(myPosition, newPosition, null));
            }
        }
    }

    public void ifOpenAddMultiple(ChessBoard board, ChessPosition myPosition, ChessPiece piece, Collection<ChessMove> moves, int[] direction) {
        for (int i = 1; i < 8 ; i++) { //
            int newX = myPosition.getRow() + (i * direction[0]);
            int newY = myPosition.getColumn() + (i * direction[1]);
            if (newX > 0 && newX <= 8 && newY > 0 && newY <= 8) {
                ChessPosition newPosition = new ChessPosition(newX, newY);
                if ((board.getPiece(newPosition) != null)) {
                    if (checkDifferentColors(board.getPiece(newPosition), piece)) {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                } else {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
            } else {
                break;
            }
        }
    }
}


class BishopMovesCalculator extends PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        List<ChessMove> moveList = new ArrayList<>();
        int[][] diagonalDirections = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        for (int[] dir: diagonalDirections) {
            ifOpenAddMultiple(board, myPosition, piece, moveList, dir);
        }
        return moveList;
    }
}

class KingMovesCalculator extends PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        List<ChessMove> moveList = new ArrayList<>();
        int[][] kingMoveDirections = {{-1, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}};
        for (int[] dir: kingMoveDirections) {
            int newX = myPosition.getRow() + dir[0];
            int newY = myPosition.getColumn() + dir[1];
            ifOpenAddSingle(board, myPosition, piece, moveList, newX, newY);
        }
        return moveList;
    }
}

class KnightMovesCalculator extends PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        List<ChessMove> moveList = new ArrayList<>();
        int[][] knightMoveDirections = {{-1, 2}, {-1, -2}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {2, 1}, {2, -1}};
        for (int[] dir: knightMoveDirections) {
            int newX = myPosition.getRow() + dir[0];
            int newY = myPosition.getColumn() + dir[1];
            ifOpenAddSingle(board, myPosition, piece, moveList, newX, newY);
        }
        return moveList;
    }
}

class PawnMovesCalculator extends PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        List<ChessMove> moveList = new ArrayList<>();
        int[][] pawnMoveDirections = {{1, -1}, {1, 0}, {1, 1}, {2, 0}};
        ChessGame.TeamColor color = piece.getTeamColor();
        if (color == ChessGame.TeamColor.BLACK) {
            pawnMoveDirections[0][0] = -1;
            pawnMoveDirections[1][0] = -1;
            pawnMoveDirections[2][0] = -1;
            pawnMoveDirections[3][0] = -2;
        }
        for (int[] dir: pawnMoveDirections) {
            int newX = myPosition.getRow() + dir[0];
            int newY = myPosition.getColumn() + dir[1];
            if (newX > 0 && newX <= 8 && newY > 0 && newY <= 8) {
                ChessPosition newPosition = new ChessPosition(newX, newY);
                if (dir[1] != 0 && board.getPiece(newPosition) != null) {
                    if (checkDifferentColors(board.getPiece(newPosition), piece)) {
                        if ((color == ChessGame.TeamColor.BLACK && newPosition.getRow() == 1) || newPosition.getRow() == 8) {
                            makePromotions(moveList, myPosition, newPosition);
                        } else {
                            moveList.add(new ChessMove(myPosition, newPosition, null));
                        }
                    }
                } else if (dir[1] == 0 && board.getPiece(newPosition) == null) {
                    if (myPosition.getRow() == 2 && dir[0] == 2 && board.getPiece(new ChessPosition(newX - 1, newY)) == null) {
                        moveList.add(new ChessMove(myPosition, newPosition, null));
                    } else if (myPosition.getRow() == 7 && dir[0] == -2 && board.getPiece(new ChessPosition(newX + 1, newY)) == null){
                        moveList.add(new ChessMove(myPosition, newPosition, null));
                    } else if ((dir[0] == 1) || (dir[0] == -1)) {
                        if ((color == ChessGame.TeamColor.BLACK && newPosition.getRow() == 1) || newPosition.getRow() == 8) {
                            makePromotions(moveList, myPosition, newPosition);
                        } else {
                            moveList.add(new ChessMove(myPosition, newPosition, null));
                        }
                    }
                }
            }
        }
        return moveList;
    }

    public void makePromotions(Collection<ChessMove> list, ChessPosition oldSpot, ChessPosition newSpot) {
        list.add(new ChessMove(oldSpot, newSpot, ChessPiece.PieceType.BISHOP));
        list.add(new ChessMove(oldSpot, newSpot, ChessPiece.PieceType.KNIGHT));
        list.add(new ChessMove(oldSpot, newSpot, ChessPiece.PieceType.QUEEN));
        list.add(new ChessMove(oldSpot, newSpot, ChessPiece.PieceType.ROOK));
    }
}

class QueenMovesCalculator extends PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        List<ChessMove> moveList = new ArrayList<>();
        int[][] queenMoveDirections = {{-1, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}};
        for (int[] dir: queenMoveDirections) {
            ifOpenAddMultiple(board, myPosition, piece, moveList, dir);
        }
        return moveList;
    }
}

class RookMovesCalculator extends PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        List<ChessMove> moveList = new ArrayList<>();
        int[][] rookMoveDirections = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
        for (int[] dir: rookMoveDirections) {
            ifOpenAddMultiple(board, myPosition, piece, moveList, dir);
        }
        return moveList;
    }
}