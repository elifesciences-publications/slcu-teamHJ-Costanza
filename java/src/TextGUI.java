import Costanza.Image;
import Costanza.Stack;
import Costanza.Inverter;
import Costanza.Case;
import Costanza.Options;
import Costanza.MeanFilter;
import Costanza.GradientDescent;

/**
 * The Text version of our GUI.
 * @author michael
 */
public class TextGUI {
    
    /** Creates a new instance of TextGUI */
    public TextGUI() throws Exception {
        System.out.println("Creating a Stack");
        Stack stack = new Stack();
        for(int i=0; i<20; ++i){
            stack.addImage(new Image(400, 400));
        }
        //System.out.println("Creating an Image of size: " + image.getWidth() + " " + image.getHeight());
        //System.out.println("Text: Original Stack 1: " + stack.getDepth());
        System.out.println("Creating a Case");
        Case myCase = new Case(stack);
        System.out.println("Filling images");
        for(int i=0; i<myCase.getStack().getDepth(); ++i){
            myCase.getStack().setIntensity(4,i+4,i,(i == 1) ? 1.0f : 0.0f);
        }
        System.out.println("Creating options");
        //System.out.println("Text: Original Stack 2: " + myCase.getStack().getDepth());
        Options options = new Options();
        options.addOption("radius", new Float(1));
        //Inverter inverter = new Inverter();
        //myCase = inverter.process(myCase, options);
        //System.out.println("Text: Original Stack 3: " + myCase.getStack().getDepth());
        //MeanFilter meanFilter = new MeanFilter();
        //myCase = meanFilter.process(myCase, options);
        GradientDescent gd = new GradientDescent();
        myCase = gd.process(myCase, options);
        //System.out.println("Text: Original Stack 5: " + myCase.getStack().getDepth());
        for(int i=0; i<myCase.getStack().getDepth(); ++i){
            //printImage(myCase.getStack().getImage(i));
        }
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
                image.setIntensity(i, j, (i == 5 && j == 5) ? 1 : 0);
            }
        }
    }
    /**
     * Print an Image to the terminal.
     * @param image the image to print
     */
    private void printImage(Image image){
        System.out.println("Image: " + image);
        for(int i=0; i<image.getHeight(); ++i){
            for(int j=0; j<image.getWidth(); ++j){
                System.out.print(image.getIntensity(j,i) + " ");
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
