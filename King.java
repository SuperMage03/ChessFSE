import javax.swing.*;
import java.awt.*;

public class King extends Piece {
    //A child class of Piece, overrides the Piece values with the specific child piece based info
    //Such as the value of the piece, image, encoded value in boardGrid, and maxMoves
    public King(int colour) {
        this.colour = colour;
        code = 6;
        maxMoves = 1;
        moveSet = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, 1}, {1, -1}, {-1, -1}};
        if (colour == 0) {
            value = 900;
            img = new ImageIcon("Sprites/W_King.png").getImage();
        }
        else {
            value = -900;
            img = new ImageIcon("Sprites/B_King.png").getImage();
        }
    }
	
	//A static method that finds the position of the given colour's King in a board
	//If not found, returns null
    public static Point findKing(int colour, int[][] board) {
        int objective = colour * 10 + 6;
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                if (board[row][column] == objective) {
                    return new Point(column, row);
                }
            }
        }
        return null;
    }

	//A static method that checks if there are piece between the rook and the corresponding king if castling is available
	//If the conditions met castling then just mark the position in possibleMove grid for castling as true
    public static void kingCastleMove(Point kingPos, int[][] boardGrid, boolean[][] possibleMove, boolean[][] castle) {
        if (!Board.check(boardGrid, kingPos)) {
            for (int index = 0; index < 2; index++) {
                boolean[] curCastle = castle[index];
                int y = index == 0 ? 7 : 0;
                if (kingPos.y != y) {continue;}
                if (curCastle[1]) {
                    if (curCastle[0]) {
                        boolean flag = true;
                        for (int i = 3; i >= 1; i--) {
                            if (boardGrid[y][i]%10 != 0) {
                                flag = false;
                                break;
                            }
                        }
                        if (flag) {possibleMove[y][2] = true;}
                    }
                    if (curCastle[2]) {
                        boolean flag = true;
                        for (int i = 5; i < 7; i++) {
                            if (boardGrid[y][i]%10 != 0) {
                                flag = false;
                                break;
                            }
                        }
                        if (flag) {possibleMove[y][6] = true;}
                    }
                }
            }
        }
    }
}
