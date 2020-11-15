import javax.swing.*;

public class Knight extends Piece{
    //A child class of Piece, overrides the Piece values with the specific child piece based info
    //Such as the value of the piece, image, encoded value in boardGrid, and maxMoves
    public Knight(int colour) {
        this.colour = colour;
        code = 2;
        maxMoves = 1;
        moveSet = new int[][]{{1, 2}, {2, 1}, {-1, 2}, {2, -1}, {-2, 1}, {1, -2}, {-2, -1}, {-1, -2}};
        if (colour == 0) {
            value = 30;
			img = new ImageIcon("Sprites/W_Knight.png").getImage();
        }
        else {
            value = -30;
            img = new ImageIcon("Sprites/B_Knight.png").getImage();
        }
    }
}
