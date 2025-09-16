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
            KnightMovesCalculator Knight = new KnightMovesCalculator();
            return Knight.pieceMoves(board, myPosition, piece);
        } else if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            PawnMovesCalculator Pawn = new PawnMovesCalculator();
            return Pawn.pieceMoves(board, myPosition, piece);
        } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            QueenMovesCalculator Queen = new QueenMovesCalculator();
            return Queen.pieceMoves(board, myPosition, piece);
        } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            RookMovesCalculator Rook = new RookMovesCalculator();
            return Rook.pieceMoves(board, myPosition, piece);
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
            for (int i = 1; i < 8 ; i++) { //
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
        for (int[] dir: moveDirections) {
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
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        List<ChessMove> moveList = new ArrayList<>();
        int[][] moveDirections = {{-1, 2}, {-1, -2}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {2, 1}, {2, -1}};
        for (int[] dir: moveDirections) {
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

class PawnMovesCalculator extends PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        List<ChessMove> moveList = new ArrayList<>();
        int[][] moveDirections = {{1, -1}, {1, 0}, {1, 1}, {2, 0}};
        ChessGame.TeamColor color = piece.getTeamColor();
        if (color == ChessGame.TeamColor.BLACK) {
            moveDirections[0][0] = -1;
            moveDirections[1][0] = -1;
            moveDirections[2][0] = -1;
            moveDirections[3][0] = -2;
        }
        for (int[] dir: moveDirections) {
            int newX = myPosition.getRow() + dir[0];
            int newY = myPosition.getColumn() + dir[1];
            if (newX > 0 && newX <= 8 && newY > 0 && newY <= 8) {
                ChessPosition newPosition = new ChessPosition(newX, newY);
                if (dir[1] != 0 && board.getPiece(newPosition) != null) {
                    if (checkDifferentColors(board.getPiece(newPosition), piece)) {
                        if (color == ChessGame.TeamColor.BLACK && newPosition.getRow() == 1) {
                            makePromotions(moveList, myPosition, newPosition);
                        } else if (newPosition.getRow() == 8){
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
                        if (color == ChessGame.TeamColor.BLACK && newPosition.getRow() == 1) {
                            makePromotions(moveList, myPosition, newPosition);
                        } else if (newPosition.getRow() == 8){
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
        System.out.println("promoting");
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
        int[][] moveDirections = {{-1, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}};
        for (int[] dir: moveDirections) {
            for (int i = 1; i < 8; i++) {
                int newX = myPosition.getRow() + (i *dir[0]);
                int newY = myPosition.getColumn() + (i * dir[1]);
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
        }
        return moveList;
    }
}

class RookMovesCalculator extends PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        List<ChessMove> moveList = new ArrayList<>();
        int[][] moveDirections = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
        for (int[] dir: moveDirections) {
            for (int i = 1; i < 8; i++) {
                int newX = myPosition.getRow() + (i *dir[0]);
                int newY = myPosition.getColumn() + (i * dir[1]);
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
        }
        return moveList;
    }
}