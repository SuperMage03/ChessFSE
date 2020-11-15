import javax.swing.*;

public class Queen extends Piece {
    //A child class of Piece, overrides the Piece values with the specific child piece based info
    //Such as the value of the piece, image, encoded value in boardGrid, and maxMoves
    public Queen(int colour) {
        this.colour = colour;
        code = 5;
        maxMoves = 8;
        moveSet = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, 1}, {1, -1}, {-1, -1}};
        if (colour == 0) {
            value = 90;
			img = new ImageIcon("Sprites/W_Queen.png").getImage();
        }
        else {
            value = -90;
			img = new ImageIcon("Sprites/B_Queen.png").getImage();
        }
    }
}
