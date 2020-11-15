import java.util.*;
import java.io.*;

/*
!!!!!!!!!!!!!!Layout of cfg.txt!!!!!!!!!!!!!!!!!!!!!!
Screen Size: ___
Board Colour 1: ___ ___ ___
Board Colour 2: ___ ___ ___
*/


//A class for getting configurations in the cfg.txt file
public class Config {
    //Colours of the grid
    private final int[] colour1 = new int[3];
    private final int[] colour2 = new int[3];
	private int sSize; //Screen Size
    //When initialize it opens the file and adds all of the content to their corresponding configuration variable
    public Config() {
        try {
            Scanner inFile = new Scanner(new BufferedReader(new FileReader("cfg.txt")));
			sSize = Integer.parseInt(inFile.nextLine().substring(13));
			sSize -= sSize % 8; //This is done to avoid extra pixel on the screen that can lead to crashing when clicked on
			
			String[] temp;
			//For colour1
			temp = inFile.nextLine().substring(16).split(" ");
			for (int i = 0; i < 3; i++) {
				colour1[i] = Integer.parseInt(temp[i]);
			}
			//For colour2
			temp = inFile.nextLine().substring(16).split(" ");
			for (int i = 0; i < 3; i++) {
				colour2[i] = Integer.parseInt(temp[i]);
			}
        }
        catch(FileNotFoundException ex) {
            System.out.println("Add a file called cfg.txt at the root and make sure it has the layout setup properly!");
        }
    }



    //Getters for each configuration variables
	public int getScreenSize() {return sSize;}
    public int[] getColour1() {return colour1;}
    public int[] getColour2() {return colour2;}
}