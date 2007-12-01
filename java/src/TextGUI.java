import Costanza.Image;
import Costanza.Stack;
import Costanza.Inverter;
import Costanza.Case;
import Costanza.Options;
import Costanza.MeanFilter;

/*
 * TextGUI.java
 *
 * Created on December 1, 2007, 12:50 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 * The Text version of our GUI.
 * @author michael
 */
public class TextGUI {
    
    /** Creates a new instance of TextGUI */
    public TextGUI() throws Exception {
        Image image = new Image(10, 10);
        System.out.println("Creating an Image of size: " + image.getWidth() + " " + image.getHeight());
        setTheImage(image);
        printImage(image);
        System.out.println("Creating a Stack");
        Stack stack = new Stack();
        System.out.println("Adding an Image to the Stack");
        stack.addImage(image);
        System.out.println("Creating a Case");
        Case myCase = new Case(stack);
        Options options = new Options();
        options.addOption("radius", new Float(1));
        Inverter inverter = new Inverter();
        myCase = inverter.process(myCase, options);
        printImage(myCase.getStack().getImage(0));
        MeanFilter meanFilter = new MeanFilter();
        myCase = meanFilter.process(myCase, options);
        printImage(myCase.getStack().getImage(0));
    }
    
    /**
     * Simple method for setting an initial image to a diagonal matrix.
     * @param image the image to set
     */
    private void setTheImage(Image image){
        System.out.println("Image: " + image);
        for(int i=0; i<image.getWidth(); ++i){
            //System.out.println("Inside first loop");
            for(int j=0; j<image.getHeight(); ++j){
                //System.out.println("Inside second loop");
                image.setIntensity(i, j, (i == j) ? 1 : 0);
            }
        }
    }
    /**
     * Print an Image to the terminal.
     * @param image the image to print
     */
    private void printImage(Image image){
        System.out.println("Image: " + image);
        for(int i=0; i<image.getWidth(); ++i){
            for(int j=0; j<image.getHeight(); ++j){
                System.out.print(image.getIntensity(i,j) + " ");
            }
            System.out.println();
        }
    }
    
    public static void main(String[] argv){
        try{
            new TextGUI();
        }catch(Exception e){
            System.out.print("Error: ");
            System.out.println(e.getMessage());
        }
    }
}
