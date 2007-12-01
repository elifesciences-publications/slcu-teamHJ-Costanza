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
        printImage(image);
        System.out.println("Creating a Stack");
        Stack stack = new Stack();
        System.out.println("Adding an Image to the Stack");
        stack.addImage(image);
        System.out.println("Creating a Case");
        Case myCase = new Case(stack);
        System.out.println("Creating an Inverter");
        Inverter inverter = new Inverter();
        MeanFilter meanFilter = new MeanFilter();
        System.out.println("Inverting the Case");
        Options options = new Options();
        options.addOption("radius", 2);
        myCase = meanFilter.process(myCase, options);
        Stack workerStack = myCase.getStack();
        Image workedImage = workerStack.getImage(0);
        printImage(workedImage);
    }
    
    private void printImage(Image image){
        for(int i=0; i<image.getWidth(); ++i){
            //System.out.println("Inside first loop");
            for(int j=0; j<image.getHeight(); ++j){
                //System.out.println("Inside second loop");
                image.setIntensity(i, j, (i == j) ? 1 : 0);
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
