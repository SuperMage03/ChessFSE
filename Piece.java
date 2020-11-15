import java.awt.*;

public class Piece {
    //Super Class for all of the piece type
    protected int colour, code, value; //Colour is the colour this piece belongs, code is the last digit encoded on the boardGrid, value is how much the piece worth for AI
    protected Image img; //Image for this piece
    protected int[][] moveSet, killSet; //moveSet is the movement options for this piece, killSet is the kill movement for this piece (only needed for pawn)
    protected int maxMoves; //Maximum number of step this piece can move per turn

    //Getters
    public Image getImg() {return img;}
    public int getValue() {return value;}


	public void setImgSize(int sSize) {
		img = img.getScaledInstance(sSize, sSize, Image.SCALE_SMOOTH);
	}


    //Calculates all of the possible position this piece can go to and stores it in possibleMove grid array, then returns number of moves for this piece
    //This method overloading is for pieces that are not pawn nor king
    public int calcPieceMove(Point selected, int[][] boardGrid, boolean[][] possibleMove) {
        int counter = 0; //Counter for the amount moves possible for this piece
        for (int[] curMoves : moveSet) { //Loop through all of the moves this piece can go
            for (int i = 1; i <= maxMoves; i++) { //Move the piece to on direction step by step
                //Calculates the new position that this piece can move based on what step we are on
                int newX = selected.x + curMoves[0] * i;
                int newY = selected.y + curMoves[1] * i;
                //If it's out of bound
                if (!Board.inBoard(newX, newY)) {break;}
                //If it's not a blank spot
                else if (boardGrid[newY][newX]%10 != 0) {
                    //If the new position is an enemy, record it possible to travel to
                    if (boardGrid[newY][newX]/10 != colour) {
                        possibleMove[newY][newX] = true;
                        counter++;
                    }
                    break;
                }
                //If it's a blank, record it possible to travel to
                else {
                    possibleMove[newY][newX] = true;
                    counter++;
                }
            }
        }
        return counter;
    }

    //Same thing for the first calcPieceMove but specifically for pawns
    public int calcPieceMove(Point selected, int[][] boardGrid, boolean[][] possibleMove, Point[] enpassant) {
        int moveSteps, counter = 0; //moveSteps is the amount of steps it can take this turn
        if (colour == 0) { //If the colour is white
            if (selected.y == 6) {moveSteps = 2;} //If the pawn is still at starting position then it can use the starting 2 step leap
            else {moveSteps = 1;} //If not it can only move 1 step
        }
        else { //Same thing as white but for black
            if (selected.y == 1) {moveSteps = 2;}
            else {moveSteps = 1;}
        }
        //FOR MOVEMENT
        for (int i = 1; i <= moveSteps; i++) {
            //Calculate the position taken this step
            int newX = selected.x;
            int newY = selected.y + moveSet[0][1] * i;
            //If it's out of bound,  record it possible to travel to
            if (!Board.inBoard(newX, newY)) {break;}
            //If it's not a blank spot
            else if (boardGrid[newY][newX]%10 != 0) {
                break;
            }
            //If it's a blank,  record it possible to travel to
            else {
                possibleMove[newY][newX] = true;
                counter++;
            }
        }
        //FOR KILL, movement is different than simply moving
        for (int[] kill : killSet) {
            //Calculate the current diagonal step's new position
            int newX = selected.x + kill[0];
            int newY = selected.y + kill[1];
            if (Board.inBoard(newX, newY) && boardGrid[newY][newX]%10 != 0 && boardGrid[newY][newX]/10 != colour) {
                possibleMove[newY][newX] = true;
                counter++;
            }
            //If last enemy move was a 2 step leap then we can check if we can use en passant for this piece to capture the other piece
            if (enpassant[0] != null && Board.inBoard(newX, newY)) {
                if (enpassant[0].x == newX && enpassant[0].y == newY) {
                    possibleMove[newY][newX] = true;
                    counter++;
                }
            }
        }
        return counter;
    }

    //Same as the first calcPieceMove but specifically for king
    public int calcPieceMove(int[][] boardGrid, boolean[][] possibleMove, boolean[][] castle) {
        int counter = 0;
        int[][] curMoves = moveSet;
        int[][] boardCopy = new int[8][8]; //Make a copy of a boardGrid for checking if new position going to is a check
        Point kingPos = King.findKing(colour, boardGrid); //Finds the position of the king
        //Loop through all of the directions
        for (int[] movement : curMoves) {
            //Gets the new position that the king can possibly move to
            int newX = kingPos.x + movement[0];
            int newY = kingPos.y + movement[1];
            //If the new position is reachable and the new position is not occupied by the same colour
            if (Board.inBoard(newX, newY) && (boardGrid[newY][newX]/10 != colour || boardGrid[newY][newX]%10 == 0)) {
                boardCopy = Board.copyBoardGrid(boardGrid); //Deep copies boardGrid to boardCopy
                //Let the king move to this position in the copy of boardGrid and check if there is a check, if there isn't then it's good to move
                boardCopy[newY][newX] = boardGrid[kingPos.y][kingPos.x];
                boardCopy[kingPos.y][kingPos.x] = 0;
                if (!Board.check(boardCopy, new Point(newX, newY))) {
                    possibleMove[newY][newX] = true;
                    counter++;
                }
            }
        }
        //Calls kingCastleMove from King class for update on the availability of castling since the player might move the king
        King.kingCastleMove(kingPos, boardGrid, possibleMove, castle);
        return counter;
    }

}
