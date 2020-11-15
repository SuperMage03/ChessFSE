import javax.swing.*;

public class Bishop extends Piece {
    //A child class of Piece, overrides the Piece values with the specific child piece based info
    //Such as the value of the piece, image, encoded value in boardGrid, and maxMoves
    public Bishop(int colour) {
        this.colour = colour;
        code = 3;
        maxMoves = 8;
        moveSet = new int[][]{{1, 1}, {-1, 1}, {1, -1}, {-1, -1}};
        if (colour == 0) {
            value = 30;
			img = new ImageIcon("Sprites/W_Bishop.png").getImage();
        }
        else {
            value = -30;
            img = new ImageIcon("Sprites/B_Bishop.png").getImage();
        }
    }
}