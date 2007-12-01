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
        stack.setIntensity(3,0,0,1.0f);
        printImage(image);
        System.out.println("Creating a Case");
        Case myCase = new Case(stack);
        System.out.println("Creating an Inverter");
        Inverter inverter = new Inverter();
        //MeanFilter meanFilter = new MeanFilter();
        System.out.println("Creating an Options");
        Options options = new Options();
        System.out.println("Adding an Options");
        options.addOption("radius", new Float(2));
        System.out.println("Inverting the Case");
        myCase = inverter.process(myCase, options);
        System.out.println("Getting the Stack");
        Stack workerStack = myCase.getStack();
        Image workedImage = workerStack.getImage(0);
        printImage(workedImage);
    }
    
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
    
    private void printImage(Image image){
        System.out.println("Image: " + image);
        for(int i=0; i<image.getWidth(); ++i){
            //System.out.println("Inside first loop");
            for(int j=0; j<image.getHeight(); ++j){
                //System.out.println("Inside second loop");
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
