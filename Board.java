import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;

public class Board {
    //WHITE: Blank = 0, Pawn = 1, Knight = 2, Bishop = 3, Rook = 4, Queen = 5, King = 6
    //BLACK: Blank = 0, Pawn = 11, Knight = 12, Bishop = 13, Rook = 14, Queen = 15, King = 16
    private final int[][] boardGrid; //The current board grid (representation in integers)
    private boolean[][] possibleMove; //A grid that stores all of the possible moves of a piece
    private int turn = 0; //The current colour for this turn
    private Point selected = null; //Selected piece
    //enpassant stores the position of a pawn that used the starting leap, first index is where the pawn was and second index is where the pawn to
    private Point[] enpassant = {null, null};
    private int enpassantEnd = -1; //Which turn en passant move expires
    //Each row of the 2d list is corresponds the elegiblitiy and very left of the boolean array checks
    //If the left rook has moved yet, the middle one checks if the king has moved yet, and the right one checks if the right rook has moved yet
    private final boolean[][] castle = {{true, true, true}, {true, true, true}};
    private final boolean AIMode; //Boolean for if this board is AI enabled or not
    private String name; //Stores the player name
    private int loser = -1; //Stores the loser
    //What a new board looks like
    private static final int[][] newBoard = {{14,12,13,15,16,13,12,14},
                                             {11,11,11,11,11,11,11,11},
                                             { 0, 0, 0, 0, 0, 0, 0, 0},
                                             { 0, 0, 0, 0, 0, 0, 0, 0},
                                             { 0, 0, 0, 0, 0, 0, 0, 0},
                                             { 0, 0, 0, 0, 0, 0, 0, 0},
                                             { 1, 1, 1, 1, 1, 1, 1, 1},
                                             { 4, 2, 3, 5, 6, 3, 2, 4}};

    private static final HashMap<Integer, int[][]> moves = new HashMap<Integer, int[][]>(); //Possible Movement on the x, y for each piece
    private static final HashMap<Integer, Integer> maxMoves = new HashMap<Integer, Integer>(); //The maximum steps you can take for each piece

	private final int sSize; //Screen Size
    private static final HashMap<Integer, Piece> pieces = new HashMap<Integer, Piece>(); //The encoded value and its corresponding Piece object
	private final FNT winnerStatus; //Font for the message when someone win
	private final Config configFile; //For reading add of the configurations in the cfg file
    //Sets up all the static reference HashMaps
    static {
		//Put the pieces in the HashMap with its corresponding code
		pieces.put(0, null);
        pieces.put(1, new Pawn(0));
        pieces.put(2, new Knight(0));
        pieces.put(3, new Bishop(0));
        pieces.put(4, new Rook(0));
        pieces.put(5, new Queen(0));
        pieces.put(6, new King(0));
        pieces.put(11, new Pawn(1));
        pieces.put(12, new Knight(1));
        pieces.put(13, new Bishop(1));
        pieces.put(14, new Rook(1));
        pieces.put(15, new Queen(1));
        pieces.put(16, new King(1));
        //Knight moves
        moves.put(2, new int[][]{{1, 2}, {2, 1}, {-1, 2}, {2, -1}, {-2, 1}, {1, -2}, {-2, -1}, {-1, -2}});
        maxMoves.put(2, 1);
        //Bishop moves
        moves.put(3, new int[][]{{1, 1}, {-1, 1}, {1, -1}, {-1, -1}});
        maxMoves.put(3, 8);
        //Rook moves
        moves.put(4, new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}});
        maxMoves.put(4, 8);
        //Queen moves
        moves.put(5, new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, 1}, {1, -1}, {-1, -1}});
        maxMoves.put(5, 8);
        //King moves
        moves.put(6, new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, 1}, {1, -1}, {-1, -1}});
        maxMoves.put(6, 1);
    }

    //Takes in whether the Board is with AI and the name of the player if is in AI mode
    public Board(boolean AIMode, String name, Config configFile) {
        this.AIMode = AIMode; //Store the AIMode
		this.sSize = configFile.getScreenSize(); //Get screen size
		this.configFile = configFile; //Assign the config file reader
		winnerStatus = new FNT("Font",  "png", Main.dynamicSizing(70, sSize)); //The font for the winning message
		//Resize all of the pieces
		pieces.get(1).setImgSize(Main.dynamicSizing(80, sSize));
		pieces.get(2).setImgSize(Main.dynamicSizing(80, sSize));
		pieces.get(3).setImgSize(Main.dynamicSizing(80, sSize));
		pieces.get(4).setImgSize(Main.dynamicSizing(80, sSize));
		pieces.get(5).setImgSize(Main.dynamicSizing(80, sSize));
		pieces.get(6).setImgSize(Main.dynamicSizing(80, sSize));
		pieces.get(11).setImgSize(Main.dynamicSizing(80, sSize));
		pieces.get(12).setImgSize(Main.dynamicSizing(80, sSize));
		pieces.get(13).setImgSize(Main.dynamicSizing(80, sSize));
		pieces.get(14).setImgSize(Main.dynamicSizing(80, sSize));
		pieces.get(15).setImgSize(Main.dynamicSizing(80, sSize));
		pieces.get(16).setImgSize(Main.dynamicSizing(80, sSize));
		
        if (AIMode) {
            //If no name was given, set it to guest
            this.name = name != null ? name : "Guest";
        }
        //Copies the newBoard layout and set possibleMove to false
        boardGrid = copyBoardGrid(newBoard);
        possibleMove = new boolean[8][8];
    }

    //!!!!!!!!!!!!!!!!!!!STATIC METHODS!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public static boolean inBoard(int x, int y) {return (x < 8 && x >= 0 && y < 8 && y >= 0);}

    //Returns the cell that the player clicked on
    public static Point clickedOn(Point mousePos, int sSize) {
        Point curCell =  new Point((int) (mousePos.getX()/(sSize/8)), (int) (mousePos.getY()/(sSize/8)));
        //Make sure curCell's X and y position is in bound between 0 - 8
        curCell.x = Math.min(curCell.x, 8); curCell.y = Math.min(curCell.y, 8);
        curCell.x = Math.max(curCell.x, 0); curCell.y = Math.max(curCell.y, 0);
        return  curCell;
    }

    //Deep copies a Board Grid
    public static int[][] copyBoardGrid(int[][] original) {
        int[][] result = new int[8][8];
        for (int i = 0; i < 8; i++) {
            result[i] = Arrays.copyOf(original[i], 8);
        }
        return result;
    }
	
	//Calculates the current value of the given board by adding all the values of all the pieces in the given board
    private static int calcValue(int[][] board) {
        int counter = 0;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col]%10 != 0) {
                    counter += pieces.get(board[row][col]).getValue();
                }
            }
        }
        return counter;
    }


    //Get which piece the player select and store it in selected
    public void getSelect(Point mousePos) {
        Point temp = clickedOn(mousePos, sSize);
        if (!possibleMove[temp.y][temp.x]) {
            if (boardGrid[temp.y][temp.x] != 0 && boardGrid[temp.y][temp.x]/10 == turn) {
                selected = temp; possibleMove = new boolean[8][8];
                calcMove(selected, boardGrid, possibleMove, castle, enpassant);
            }
            else {
                selected = null;
                possibleMove = new boolean[8][8];
            }
        }
    }

    //MAIN MOVE FUNCTION
    //Trys to move a piece from origin to destination on the given board and have possibleMove grid to know if the piece can move there
    public void move(Point origin, Point destination, int[][] board, boolean[][] possibleMove) {
        //Make sure origin is not null (A piece is selected)
        if (origin != null && possibleMove[destination.y][destination.x]) {
            //Make a copy of the board and use the movement
            //See if a check occurs if moving to that position
            //If not then move to there
            int[][] boardCopy = copyBoardGrid(board);
            boardCopy[destination.y][destination.x] = board[origin.y][origin.x];
            boardCopy[origin.y][origin.x] = 0;
            if (!check(boardCopy, King.findKing(turn, boardCopy))) {
                validMove(origin, destination, board);
            }
            else if (check(boardCopy, King.findKing(turn, boardCopy)) && King.findKing((turn+1)%2, boardCopy) == null) {
                validMove(origin, destination, board);
            }
        }
    }

    //Clears en passant if one turn has passed
    private void enpassantClear() {
        if (enpassantEnd == turn) {
            enpassant = new Point[]{null, null};
            enpassantEnd = -1;
        }
    }

    //Change the condition of castling
    private void changeCanCastle(Point origin) {
        if (boardGrid[origin.y][origin.x]%10 == 4) {
            if (boardGrid[origin.y][origin.x]/10 == 0 && origin.y == 7) {
                if (origin.x == 0) {castle[0][0] = false;}
                else if (origin.x == 7) {castle[0][2] = false;}
            }
            else if (boardGrid[origin.y][origin.x]/10 == 1 && origin.y == 0) {
                if (origin.x == 0) {castle[1][0] = false;}
                else if (origin.x == 7) {castle[1][2] = false;}
            }
        }
    }

    //Method that actually moves the piece
    private void validMove(Point origin, Point temp, int[][] board) {
        //If a pawn did a 2 step start move, then stores the origin position and the destination position to enpassant
        if (board[origin.y][origin.x]%10 == 1 && Math.abs(origin.y - temp.y) == 2) {
            enpassant[0] = new Point(origin.x, origin.y + (temp.y - origin.y)/2);
            enpassant[1] = new Point(origin.x, origin.y + (temp.y - origin.y));
            enpassantEnd = turn;
        }
        //Check if a side did en passant then kill the corresponding pawn
        if (enpassant[0] != null && temp.x == enpassant[0].x && temp.y == enpassant[0].y) {
            board[enpassant[1].y][enpassant[1].x] = 0;
        }
        //For moving rook if castling
        if (board[origin.y][origin.x]%10 == 6) {
            if (((origin.y == 0 && temp.y == 0) || (origin.y == 7 && temp.y == 7)) && Math.abs(temp.x - origin.x) > 1) {
                if (temp.x == 2) {
                    board[origin.y][3] = board[origin.y][0];
                    board[origin.y][0] = 0;
                }
                else {
                    board[origin.y][5] = board[origin.y][7];
                    board[origin.y][7] = 0;
                }
            }
            //Set castling to false if king moves
            castle[board[origin.y][origin.x]/10][1] = false;
        }
        //check and change the state of can castle or not
        changeCanCastle(origin);
        //Moves the piece from origin to temp
        boardGrid[temp.y][temp.x] = boardGrid[origin.y][origin.x];
        boardGrid[origin.y][origin.x] = 0;
        //Reset selected and possible move
        selected = null;
        possibleMove = new boolean[8][8];
        //Go to next turn
        turn++; turn %= 2;
        //Check and clears en passant
        enpassantClear();
    }

    //Check if there are any possible move for this check to stop it or King is killed already
    //If not it's a checkmate or king is already captured or no move possible, it returns
	//the colour that is being checkMated, if not a checkmate it returns -1
    private int checkCheckMate(int[][] board, int turn, boolean[][] castle, Point[] enpassant) {
        Point kingPos = King.findKing(turn, board);
        if (King.findKing(0, boardGrid) == null) {
            return 0;
        }
        else if (King.findKing(1, boardGrid) == null) {
            return 1;
        }
        Piece k = pieces.get(board[kingPos.y][kingPos.x]);
        if (check(board, kingPos) && k.calcPieceMove(board, new boolean[8][8], castle) == 0 && !checkPieceBlock(board, kingPos, turn)) {
            return turn;
        }
        if (totalPossibleMove(board, turn, castle, enpassant) == 0) {
            return turn;
        }

        return -1;
    }

    //Calculates total number of possible moves for the certain colour
    private int totalPossibleMove(int[][] board, int colour, boolean[][] castle, Point[] enpassant) {
        int counter = 0;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col]%10 != 0 && board[row][col]/10 == colour) {
                    counter += calcMove(new Point(col, row), board, new boolean[8][8], castle, enpassant);
                }
            }
        }
        return counter;
    }

	//Returns if a player can move a piece to block for the check, it does it by checking if a player can block
	//the sight line of where the check is coming from by seeing if any piece can go the any position that is on that sight line
    public boolean checkPieceBlock(int[][] board, Point kingPos, int turn) {
        int colour = board[kingPos.y][kingPos.x]/10;
        //Where check is coming from, the sight line
        boolean[][] checkMove = new boolean[8][8];
		//Flag just see if the piece that is checking is found or not
        boolean flag = false;
        //See if the pawn is checking the king, then adds the position to the sight line
        int[][] pawnMoveSet;
        if (colour == 0) {pawnMoveSet = new int[][]{{1, -1}, {-1, -1}};}
        else {pawnMoveSet = new int[][]{{1, 1}, {-1, 1}};}
        for (int[] movement : pawnMoveSet) {
            int newX = kingPos.x + movement[0];
            int newY = kingPos.y + movement[1];
            if (inBoard(newX, newY) && board[newY][newX]%10 == 1 && board[newY][newX]/10 != colour) {
                checkMove[newY][newX] = true;
                flag = true;
                break;
            }
        }

		//See if it's the other peices that is checking the king, then mark the sight line
        for (int pieceType = 2; pieceType <= 6; pieceType++) {
            for (int[] movement : moves.get(pieceType)) {
                if (flag) {break;}
                checkMove = new boolean[8][8];
                for (int i = 1; i <= maxMoves.get(pieceType); i++) {
                    int newX = kingPos.x + movement[0] * i;
                    int newY = kingPos.y + movement[1] * i;
                    if (inBoard(newX, newY)) {
                        checkMove[newY][newX] = true;
                        if (board[newY][newX]%10 != 0) {
                            if (board[newY][newX]/10 != colour) {
                                if (board[newY][newX]%10 == pieceType) {
                                    flag = true;
                                }
                            }
                            else {checkMove[newY][newX] = false;}
                            break;
                        }
                    }
                }
            }
        }

		//Search for all of the moves for each piece in the board for that colour. See if it can move to a position in the sight line
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col]%10 != 0 && board[row][col]/10 == colour && board[row][col]%10 != 6 && board[row][col]%10 != 1) {
                    int pieceType = board[row][col] % 10;
                    int[][] curMoves = moves.get(pieceType);
                    for (int[] moveSet : curMoves) {
                        for (int i = 1; i <= maxMoves.get(pieceType); i++) {
                            int newX = col + moveSet[0] * i;
                            int newY = row + moveSet[1] * i;
                            //If it's out of bound
                            if (!inBoard(newX, newY)) {break;}
                            //If it's not a blank spot
                            else if (board[newY][newX]%10 != 0) {
                                //If the new position is an enemy
                                if (board[newY][newX]/10 != turn) {
                                    if (checkMove[newY][newX]) {return true;}
                                }
                                break;
                            }
                            //If it's a blank
                            else {
                                if (checkMove[newY][newX]) {return true;}
                            }
                        }
                    }
                }
            }
        }
		//If not found, returns false
        return false;
    }

	//See if a player is in check by seeing if an enemy piece can move to the king for capture
    public static boolean check(int[][] board, Point kingPos) {
        int colour = board[kingPos.y][kingPos.x]/10; //The colour of the piece
        int[][] moveSet; //The capturing movements for each piece

        //Check if Pawn is checking
        if (colour == 0) {moveSet = new int[][]{{1, -1}, {-1, -1}};}
        else {moveSet = new int[][]{{1, 1}, {-1, 1}};}
        for (int[] movement : moveSet) {
            int newX = kingPos.x + movement[0];
            int newY = kingPos.y + movement[1];
            if (inBoard(newX, newY) && board[newY][newX]%10 == 1 && board[newY][newX]/10 != colour) {
                return true;
            }
        }

        //Check if everything else is checking the king
        for (int pieceType = 2; pieceType <= 6; pieceType++) {
            for (int[] movement : moves.get(pieceType)) {
                for (int i = 1; i <= maxMoves.get(pieceType); i++) {
                    int newX = kingPos.x + movement[0] * i;
                    int newY = kingPos.y + movement[1] * i;
                    if (inBoard(newX, newY)) {
                        if (board[newY][newX]%10 != 0) {
                            if (board[newY][newX]/10 != colour) {
                                if (board[newY][newX]%10 == pieceType) {
                                    return true;
                                }
                                else {break;}
                            }
                            else {break;}
                        }
                    }
                }
            }
        }
		//If non of the enemy piece can capture the king then there is no check
        return false;
    }

	//Calculates the possible positions a given piece on a given board can move to, returns the amount of possible moves for that piece
    private int calcMove(Point selected, int[][] boardGrid, boolean[][] possibleMove, boolean[][] castle, Point[] enpassant) {
        int counter;
        int pieceType = pieces.get(boardGrid[selected.y][selected.x]).code;
        if (pieceType == 1) {
            counter = pieces.get(boardGrid[selected.y][selected.x]).calcPieceMove(selected, boardGrid, possibleMove, enpassant);
        }
        else if (pieceType == 6) {
            counter = pieces.get(boardGrid[selected.y][selected.x]).calcPieceMove(boardGrid, possibleMove, castle);
        }
        else {
            counter = pieces.get(boardGrid[selected.y][selected.x]).calcPieceMove(selected, boardGrid, possibleMove);
        }
        return counter;
    }



    //!!!!!!!!!!!!!!Main Updates!!!!!!!!!!!!!!!!
	//The update for every frame
    public void update() {
		//Assign and update the loser value to loser
        loser = checkCheckMate(boardGrid, turn, castle, enpassant);
		//If is in AI Mode and is AI's turn and there is no winner yet, then AI moves
        if (AIMode && turn == 1 && loser == -1) {
            Point[] temp = AIGetMove(boardGrid, 4, false);
            if (temp[0] != null && temp[1] != null) {
                validMove(temp[0], temp[1], boardGrid);
            }
        }
		//Checks if a pawn is avalible for promotion
        promotion(boardGrid);
    }
	
	//Update for when a mouse click happens, takes in the HighScore editor and the cell position of which the mouse is clicked on
    public void clickUpdate(Point mousePos, HighScore hs) {
        getSelect(mousePos); //Get the selected piece
        move(selected, clickedOn(mousePos, sSize), boardGrid, possibleMove); //Try to the selected piece to where the mouse clicked
        //If there is a loser
		if (loser != -1) {
			//If is in AI Mode
            if (AIMode) {
				//If the AI lost then add and save the score
                if (loser == 1) {
                    hs.addScore(name);
                    hs.saveScore();
                }
				//Go to the score screen
                GamePanel.setMode("Score");
            }
			//If is just multiplayer mode
            else {
				//Go to the start screen
                GamePanel.setMode("Start");
            }
        }
    }
	
	//Checks if a piece is ready for promotion, if there is promotion
	// and it's a player's then opens a pop-up for a player to select a desired piece
	// if is an AI then it will always choose queen
    private void promotion(int[][] board) {
        Object[] options = {"Queen", "Knight", "Rook", "Bishop"};
        int[] related = {5, 2, 4, 3}; //Related for the piece code of the corresponding piece in options
		//Checks for promotion by seeing if there is a pawn in their opposite side
        for (int column = 0; column < 8; column++) {
			//Checks for Black
            if (board[7][column]%10 == 1 && board[7][column]/10 == 1) {
                int chosen = -1;
                if (!AIMode) { //If is not an AI
                    while (chosen == -1) { //If option has not been made
						//Open the pop up and player choose it
                        chosen = JOptionPane.showOptionDialog(null,
                                "Select a Piece for Promotion", "Select Piece",
                                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                                null, options, null);
                    }
                }
                else {chosen = 0;} //AI picks queen
                board[7][column] = 10 + related[chosen]; //Promotes

            }
			//Chcks for white, same things as black
            if (board[0][column]%10 == 1 && board[0][column]/10 == 0) {
                int chosen = -1;
                while (chosen == -1) {
                    chosen = JOptionPane.showOptionDialog(null,
                            "Select a Piece for Promotion", "Select Piece",
                            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                            null, options, null);
                }
                board[0][column] = related[chosen]; //Promotes
            }
        }
    }

	//AI will always assume the player chooses Queen for promotion for maximum value, in its calculation of what the player might do
    private void promotionAIAssume(int[][] board) {
        for (int column = 0; column < 8; column++) {
            if (board[7][column]%10 == 1 && board[7][column]/10 == 1) {
                board[7][column] = 15;
            }
            if (board[0][column]%10 == 1 && board[0][column]/10 == 0) {
                board[0][column] = 5;
            }
        }
    }

    //DRAWING ALL THE STUFF ON BORAD
    public void draw(Graphics g) {
        drawBG(g, sSize, configFile.getColour1(), configFile.getColour2());
        drawContent(g);
        drawMoves(g);
        drawWin(g);
    }

	//Draws the pieces
    private void drawContent(Graphics g) {
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                if (pieces.get(boardGrid[row][column]) != null) {
                    g.drawImage(pieces.get(boardGrid[row][column]).getImg(), Main.dynamicSizing(10, sSize) + (sSize/8) * column, Main.dynamicSizing(10, sSize) + (sSize/8) * row, null);
                }
            }
        }
    }

	//Draws the grid
    public static void drawBG(Graphics g, int sSize, int[] colour1, int[] colour2) {
        int colourSwitcher; //Helps to alternate starting colour for each row
        for (int row = 0; row < 8; row++) {
            //Alternation of starting colour is rows
            if (row % 2 != 0) {colourSwitcher = 0;}
            else {colourSwitcher = 1;}
            for (int column = 0; column < 8; column++) {
                //Alternation of colour in column
                if (colourSwitcher % 2 == 0) {g.setColor(new Color(colour1[0], colour1[1], colour1[2]));}
                else {g.setColor(new Color(colour2[0], colour2[1], colour2[2]));}
                g.fillRect((sSize/8) * column, (sSize/8) * row, sSize/8, sSize/8);
                colourSwitcher++;
            }
        }
    }

	//Draws the circles for moving a piece
    private void drawMoves(Graphics g) {
        g.setColor(Color.GRAY);
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                if (possibleMove[row][column]) {
                    g.fillOval(Main.dynamicSizing(35, sSize) + (sSize/8) * column, Main.dynamicSizing(35, sSize) + (sSize/8) * row, Main.dynamicSizing(30, sSize), Main.dynamicSizing(30, sSize));
                }
            }
        }
    }

    
	//Draws the message when someone win
    private void drawWin(Graphics g) {
        if (loser == 0) {
            winnerStatus.drawStr("BLACK WON", Main.dynamicSizing(240, sSize), Main.dynamicSizing(365, sSize), g);
        }
        else if (loser == 1) {
            winnerStatus.drawStr("WHITE WON", Main.dynamicSizing(240, sSize), Main.dynamicSizing(365, sSize), g);
        }
    }

	//Gets the best moves for the AI by loop through the grid and finding all its pieces and where each piece can move
	//Gets the best move for the best possible score
    public Point[] AIGetMove(int[][] board, int depth, boolean maxing) {
        int bestMoveVal = 9999; //Assumes the bestMove at the start is something big since AI will want the smallest value since its piece is negative
		Point[] ans = new Point[]{null, null}; //ans[0] is the origin of the best move and ans[1] is the destination of the best move
        //Loop through the board
		for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
				//If the current position is a piece
                if (board[row][col]%10 != 0 && board[row][col]/10 == 1) {
                    boolean[][] validMoves = new boolean[8][8]; //Checks where in the grid a piece can move
                    calcMove(new Point(col, row), board, validMoves, new boolean[3][2], new Point[]{null, null}); //Fill in valid Moves without caring about Castling nor en passant
                    Point origin = new Point(col, row); //Assigns the origin for the current search
					//Loop through the validMoves gird and see where the piece can move
                    for (int r = 0; r < 8; r++) {
                        for (int c = 0; c < 8; c++) {
                            if (validMoves[r][c]) {
                                Point destination = new Point(c, r); //c, r is the current destination coordinate
								//Make a new copy of the board and move the piece to the position in that position
                                int[][] newBoard = copyBoardGrid(board);
                                newBoard[r][c] = board[row][col];
                                newBoard[row][col] = 0;
                                promotionAIAssume(newBoard); //Update the board for any promotion
								//Get the best outcome value for this scenerio
                                int newMoveVal = AIMove(newBoard, depth-1, Integer.MIN_VALUE, Integer.MAX_VALUE, !maxing);
                                //If the current outcome is equal or better than the current best outcome then make it the 
								//new best outcome and assign ans with origin and destination
								if (newMoveVal <= bestMoveVal) {
                                    bestMoveVal = newMoveVal;
                                    ans[0] = origin;
                                    ans[1] = destination;
                                }
                            }
                        }
                    }
                }
            }
        }
		//Return the best move
        return ans;
    }

    //Minimax and Alpha Beta Pruning for calculating the best move
	//It does this by assuming the opponent plays the best move they can do and see what's the best move AI can do
	//in reaction of the opponent's best move. Player will always want the score to be as large as possible since their
	//piece's value is position, and AI wants the score to be as small as possible since its piece value is negative
	//alpha holds the current best move within best moves within the current branch of moves for the Player, and beta holds
	//the current best move within best moves within the current branch of moves for the AI, if the current beta is already smaller
    //than the alpha agent of the same branch, then we can just prune other branches within this branch
    public int AIMove(int[][] board, int depth, int alpha, int beta, boolean maxing) {
        //If depth is at 0 then return the current board value
        if (depth == 0) {
            return calcValue(board);
        }
        //If it's trying to maximize the score (Simulating the player's move)
        if (maxing) {
            int maxValue = -9999; //Makes the maxValue extremely small to start off
			//Finds all the moves the player can do and try to simulate the possible moves branch off of that move
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (board[row][col]%10 != 0 && board[row][col]/10 == 0) {
                        boolean[][] validMoves = new boolean[8][8]; //Finds the valid move for current piece
                        calcMove(new Point(col, row), board, validMoves, new boolean[3][2], new Point[]{null, null}); //Fills up the validMoves grid with the possible moves
                        for (int r = 0; r < 8; r++) {
                            for (int c = 0; c < 8; c++) {
                                if (validMoves[r][c]) {
									//Make a copy of this board and apply this move in the new board
                                    int[][] newBoard = copyBoardGrid(board);
                                    newBoard[r][c] = board[row][col];
                                    newBoard[row][col] = 0;
                                    promotionAIAssume(newBoard);
									//Find the best value within this move's submove, now is maxing is false because simulated next turn is AI's
                                    maxValue = Math.max(maxValue, AIMove(newBoard, depth-1, alpha, beta, false));
                                    //Update the alpha agent value
									alpha = Math.max(alpha, maxValue);
                                    //Prune this branch if the beta is already less than or equal to alpha
                                    if (beta <= alpha) {return maxValue;}
                                }
                            }
                        }
                    }
                }
            }
			//Returns the maxValue
            return maxValue;
        }
		//If is trying to find the minimum score (AI's turn)
        else {
			//Same thing as the maxing but now trying to minimize
            int minValue = 9999;
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (board[row][col]%10 != 0 && board[row][col]/10 == 1) {
                        boolean[][] validMoves = new boolean[8][8];
                        calcMove(new Point(col, row), board, validMoves, new boolean[3][2], new Point[]{null, null});
                        for (int r = 0; r < 8; r++) {
                            for (int c = 0; c < 8; c++) {
                                if (validMoves[r][c]) {
                                    int[][] newBoard = copyBoardGrid(board);
                                    newBoard[r][c] = board[row][col];
                                    newBoard[row][col] = 0;
                                    promotionAIAssume(newBoard);
                                    minValue = Math.min(minValue, AIMove(newBoard, depth-1, alpha, beta, true));
                                    beta = Math.min(beta, minValue);
                                    //Prune this branch if the beta is already less than or equal to alpha
                                    if (beta <= alpha) {return minValue;}
                                }
                            }
                        }
                    }
                }
            }
            return minValue;
        }
    }
}