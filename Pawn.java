import javax.swing.*;

public class Pawn extends Piece {
    //A child class of Piece, overrides the Piece values with the specific child piece based info
    //Such as the value of the piece, image, encoded value in boardGrid, and maxMoves
    public Pawn(int colour) {
        this.colour = colour;
        code = 1;
        maxMoves = 1;
        if (colour == 0) {
            value = 10;
			img = new ImageIcon("Sprites/W_Pawn.png").getImage();
            moveSet = new int[][]{{0, -1}};
            killSet = new int[][] {{1, -1}, {-1, -1}};
        }
        else {
            value = -10;
            img = new ImageIcon("Sprites/B_Pawn.png").getImage();
            moveSet = new int[][]{{0, 1}};
            killSet = new int[][] {{1, 1}, {-1, 1}};
        }
    }
}
