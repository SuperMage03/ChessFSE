import javax.swing.*;

public class Rook extends Piece {
    //A child class of Piece, overrides the Piece values with the specific child piece based info
    //Such as the value of the piece, image, encoded value in boardGrid, and maxMoves
    public Rook(int colour) {
        this.colour = colour;
        code = 4;
        maxMoves = 8;
        moveSet = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        if (colour == 0) {
            value = 50;
            img = new ImageIcon("Sprites/W_Rook.png").getImage();
        }
        else {
            value = -50;
            img = new ImageIcon("Sprites/B_Rook.png").getImage();
        }
    }
}