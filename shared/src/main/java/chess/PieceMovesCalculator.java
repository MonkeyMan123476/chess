package chess;

import java.util.Collection;
import java.util.List;

public class PieceMovesCalculator {

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            BishopMovesCalculator Bishop = new BishopMovesCalculator();
            return Bishop.pieceMoves(board, myPosition, piece);
        } else if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            throw new RuntimeException("Not implemented");
        } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            throw new RuntimeException("Not implemented");
        } else if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            throw new RuntimeException("Not implemented");
        } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            throw new RuntimeException("Not implemented");
        } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            throw new RuntimeException("Not implemented");
        }
        return List.of();
    }

}


class BishopMovesCalculator extends PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        return List.of(new ChessMove(myPosition, new ChessPosition(1, 8), null));
    }
}

class KingMovesCalculator extends PieceMovesCalculator {

}

class KnightMovesCalculator extends PieceMovesCalculator {

}

class PawnMovesCalculator extends PieceMovesCalculator {

}

class QueenMovesCalculator extends PieceMovesCalculator {

}

class RookMovesCalculator extends PieceMovesCalculator {

}