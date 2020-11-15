//Eston Li, Computer Science FSE: Chess
//In this chess game you can either play against AI or against other friends. This game includes all of the rule for chess like castling and en passant
//The game also include a score board based on how many times a player has beaten the AI, the game also includes smart moves (restricts a move that will result in a check)
//The AI is not perfect, it will only move 2 piece sometimes back and forth to try to checkmate and will do that forever so be ware of that
//Other noteworthy feature to mention is you can set colour of the chess board and screen size in the cfg.txt file
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.MouseInfo;

public class Main extends JFrame implements ActionListener{
    private static final int fps = 60; //Frame Rate
    Timer myTimer;
    GamePanel game;

    public Main() {
        super("CHESS");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myTimer = new Timer(1000/fps, this);
        game = new GamePanel(this);
        add(game);
        pack();
        setResizable(false);
        setVisible(true);
    }
	
	//This method is used to converting a co-ordinate value in a screen space of 800 by 800 to a proportional value in a screen space of sSize
	public static int dynamicSizing(int value, int sSize) {
		double ratio = (double)value / 800;
		return (int) (sSize * ratio);
	}

    public void start() {myTimer.start();}

    public void actionPerformed(ActionEvent evt){
        game.update();
        game.repaint();
    }

    public static void main(String[] arguments) {Main frame = new Main();}
}

class GamePanel extends JPanel implements KeyListener, MouseListener {
    private static String mode = "Start"; //Mode of the current screen
    private Main mainFrame;
    private Board board; //The main chess board players play on
    private final Button SP, MP, SB, HB; //Buttons for start and score menu
	private final FNT title, sp, mp, sb, scoreTitle, scores, home; //Fonts for start and score menu
    private final HighScore hsFile; //For editing and getting the high scores from the Scores file
	private final Config configFile; //For getting the configurations from the cfg file
	private final int sSize; //Screen Size
    public GamePanel(Main m){
        mainFrame = m;
		configFile = new Config(); //Initialize the configuration reader
		sSize = configFile.getScreenSize(); //Set the screen size
		//Setting up all of the Font size and position
		title = new FNT("Font",  "png", Main.dynamicSizing(150, sSize)); //Title
		sp = new FNT("Font",  "png", Main.dynamicSizing(45, sSize)); //Single Player Button Text
		mp = new FNT("Font",  "png", Main.dynamicSizing(45, sSize)); //Multiplayer Button Text
		sb = new FNT("Font",  "png", Main.dynamicSizing(45, sSize)); //Score Button Text
		//Fonts used at the Score Screen
		scoreTitle = new FNT("Font",  "png", Main.dynamicSizing(80, sSize));
		scores = new FNT("Font",  "png", Main.dynamicSizing(45, sSize));
		home = new FNT("Font",  "png", Main.dynamicSizing(50, sSize));
		//Setting up all of the button size and position
        SP = new Button(Main.dynamicSizing(235, sSize), Main.dynamicSizing(400, sSize), Main.dynamicSizing(330, sSize), Main.dynamicSizing(80, sSize));
        MP = new Button(Main.dynamicSizing(235, sSize), Main.dynamicSizing(520, sSize), Main.dynamicSizing(330, sSize), Main.dynamicSizing(80, sSize));
        SB = new Button(Main.dynamicSizing(235, sSize), Main.dynamicSizing(640, sSize), Main.dynamicSizing(330, sSize), Main.dynamicSizing(80, sSize));
        HB = new Button(Main.dynamicSizing(235, sSize), Main.dynamicSizing(640, sSize), Main.dynamicSizing(330, sSize), Main.dynamicSizing(80, sSize));
        hsFile = new HighScore(); //Initialize the High Score editor
        //Set Panel Size
        setPreferredSize( new Dimension(sSize, sSize));
        addKeyListener(this);
        addMouseListener(this);
    }

    public static void setMode(String newMode) {mode = newMode;} //Setter for mode

    @Override
    public void addNotify() {
        super.addNotify();
        setFocusable(true);
        requestFocus();
        mainFrame.start();
    }

    //Game Update Function
    public void update() {
        //If the game state is playing, then call the update function for the board
        if (mode.equals("Play")) {
            board.update();
        }
    }


    //!!!!----------------MOUSE-----------------!!!!
    //A method that gets the mouse position by getting the mouse position relative to the entire screen
    //Then subtract where the frame starts at
    public Point GetMousePos() {
        Point mouse = MouseInfo.getPointerInfo().getLocation();
        Point offset = getLocationOnScreen();
        return new Point(mouse.x - offset.x, mouse.y - offset.y);
    }

    //If Mouse Clicked
    @Override
    public void	mouseClicked(MouseEvent e) {
        //If is in the starting menu
        if (mode.equals("Start")) {
            //If clicked on single player button then make a board with AIMode enabled and ask for a name and change the mode to play
            if (SP.onMouse(GetMousePos())) {
                board = new Board(true, JOptionPane.showInputDialog("Player Name: "), configFile);
                mode = "Play";
            }
            //If clicked on multiplayer button then make a board with AIMode disabled and set name to blank and change the mode to play
            else if (MP.onMouse(GetMousePos())) {
                board = new Board(false, "", configFile);
                mode = "Play";
            }
            //If clicked on player scores, then change the mode to Score Screen
            else if (SB.onMouse(GetMousePos())) {
                mode = "Score";
            }
        }
        //If playing, then call the click update
        else if (mode.equals("Play")) {
            board.clickUpdate(GetMousePos(), hsFile);
        }
        //If is on score screen and
        else if (mode.equals("Score") && SB.onMouse(GetMousePos())) {
            mode = "Start";
        }

    }
    @Override
    public void	mouseEntered(MouseEvent e) {}
    @Override
    public void	mouseExited(MouseEvent e) {}
    @Override
    public void	mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}

    //!!!!----------------KEYBOARD-----------------!!!!
    public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}

    //Draw Panel
    @Override
    public void paint(Graphics g){
        //Fills the screen black first for full refresh of screen
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, sSize, sSize);
        if (mode.equals("Play")) {
            board.draw(g); //Draws the content of board
        }
        else if (mode.equals("Start")) {
            drawStart(g); //Calls drawStart
        }
        else if (mode.equals("Score")) {
            drawScores(g); //Calls drawStart
        }
    }


    private void drawStart(Graphics g) {
        Board.drawBG(g, sSize, configFile.getColour1(), configFile.getColour2()); //Draws the grid of the board
        title.drawStr("CHESS", Main.dynamicSizing(199, sSize), Main.dynamicSizing(60, sSize), g); //Draws the Title Text
        SP.draw(g, GetMousePos()); //Draws the Single Player Button
        sp.drawStr("SINGLE PLAYER", Main.dynamicSizing(245, sSize), Main.dynamicSizing(420, sSize), g); //Draws the Single Player Button Text

        MP.draw(g, GetMousePos()); //Draws the Multiplayer Button
        mp.drawStr("MULTIPLAYER", Main.dynamicSizing(270, sSize), Main.dynamicSizing(540, sSize), g); //Draws the Multiplayer Button Text

        SB.draw(g, GetMousePos()); //Draws the Score Button
        sb.drawStr("PLAYER SCORES", Main.dynamicSizing(245, sSize), Main.dynamicSizing(660, sSize), g); //Draws the Score Button Text
    }


    private void drawScores(Graphics g) {
        Board.drawBG(g, sSize, configFile.getColour1(), configFile.getColour2()); //Draws the grid of the board
        scoreTitle.drawStr("WIN RECORDS", Main.dynamicSizing(160, sSize), Main.dynamicSizing(60, sSize), g); //Draws the Title text for the Score Screen
        //Draws up to 5 Players with scores from greatest to least score
        for (int i = 0; i < hsFile.getNames().size() && i < 5; i++) {
            //Draws the score
            scores.drawStr(String.format("%d. %s %d", i + 1, hsFile.getNames().get(i), hsFile.getScores().get(i)), Main.dynamicSizing(260, sSize), Main.dynamicSizing(180, sSize) + i * Main.dynamicSizing(80, sSize), g);
        }

        HB.draw(g, GetMousePos()); //Draws the Home Button
        home.drawStr("HOME SCREEN", Main.dynamicSizing(255, sSize), Main.dynamicSizing(655, sSize), g); //Draws the Home Button Text
    }
}