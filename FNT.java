import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

//A class of font that is made from collection of images
public class FNT {
	//drawMap stores the scaLed Images specific to the size of the font corresponds to the ascii value of the image's letter
    private final HashMap<Integer, Image> drawMap = new HashMap<Integer, Image>();
	//referenceWidth stores the scaled images' width corresponds to the ascii value of the image's letter
    private final HashMap<Integer, Integer> referenceWidth = new HashMap<Integer, Integer>();
	//Constructor takes in the directory of the Font folder and the extension of the images and the desire font size
    public FNT(String dir, String ext, int size) {
        for (int i = 33; i <= 126; i++) {
			//Get the image
            Image curImg = new ImageIcon(String.format("%s/%d.%s", dir, i, ext)).getImage();
            try {
				//Finds the width and height of the original image with BufferedIamge
                BufferedImage img = ImageIO.read(new File(String.format("%s/%d.%s", dir, i, ext)));
				//Original to new size's ratio
                double ratio = (double) size / img.getHeight();
				//Finds the new width with the ratio
                int w = (int) (img.getWidth() * ratio);
				//Scale the image to the new size
                drawMap.put(i, curImg.getScaledInstance(w, size, Image.SCALE_SMOOTH));
                referenceWidth.put(i, w);
            }
            catch (IOException Exp) {
                System.out.println("No File Found!");
            }
        }
    }

	//Draws the font with the desire string and the position of where to show the strings
    public void drawStr(String str, int x, int y, Graphics g) {
        int counterX = x; //Counter for the x postion of where to blit next letter
        for (char c : str.toCharArray()) {
            Image charImg = drawMap.get((int) c);
            g.drawImage(charImg, counterX, y, null);
			//If the character is space then just skip the same amount of pixel as a letter A in x position
			//If not just add the width stored in referenceWidth already
            counterX = c == ' ' ? counterX + referenceWidth.get((int) 'A') : counterX + referenceWidth.get((int) c);
        }
    }
}
