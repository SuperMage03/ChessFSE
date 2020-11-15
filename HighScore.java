import java.util.*;
import java.io.*;

public class HighScore {
    //Array List of all of the names and scores in the file,
    //both ArrayList are related list
    private final ArrayList<String> names = new ArrayList<String>();
    private final ArrayList<Integer> scores = new ArrayList<Integer>();
    //When initialize it opens the file and adds all of the content to the names and scores lists
    public HighScore() {
        try {
            Scanner inFile = new Scanner(new BufferedReader(new FileReader("Scores.txt")));
            while(inFile.hasNext()) {
                String n = inFile.next(); names.add(n);
                int s = inFile.nextInt(); scores.add(s);
            }
        }
        catch(FileNotFoundException ex) {
            System.out.println("Add a file called Score.txt at the root");
        }
    }

    //Adds the score and name to the names and scores list sorted from greatest to least
    public void addScore(String name) {
        //Index of the name in names
        int index = names.indexOf(name);
        //If the name is not in names, then add the name with win of 1
        if (index == -1) {
            names.add(name);
            scores.add(1);
        }

        //If there is a win
        else {
            //The new win record
            int newScore = scores.get(index) + 1;
            //Removes the old index
            names.remove(index);
            scores.remove(index);
            //If the person is the only one that had won once then just add the name and new record
            if (names.size() == 0) {
                names.add(name);
                scores.add(newScore);
            }
            else {
                System.out.println(newScore);
                //Find the index of when the other player is less or equal to him with wins against AI and insert it there
                for (int i = 0; i < names.size(); i++) {
                    if (scores.get(i) <= newScore) {
                        names.add(i, name);
                        scores.add(i, newScore);
                        break;
                    }
                }
            }
        }

    }

    //Writes the current ArrayList of scores along with the name to Score.txt file
    public void saveScore() {
        try {
            //Output I use a PrintWriter
            PrintWriter outFile = new PrintWriter(new BufferedWriter(new FileWriter("Scores.txt")));

            for (int i = 0; i < names.size(); i++) {
                outFile.println(names.get(i) + " " + scores.get(i));
            }
            outFile.close();
        }
        catch(IOException ex) {
            System.out.println("Writing Failed!");
            System.out.println(ex);
        }
    }

    //Getters for scores and names
    public ArrayList<Integer> getScores() {return scores;}
    public ArrayList<String> getNames() {return names;}
}