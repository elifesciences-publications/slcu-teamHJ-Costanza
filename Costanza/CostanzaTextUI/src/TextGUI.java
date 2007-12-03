import costanza.Image;
import costanza.Stack;
import costanza.Case;
import costanza.Options;
import costanza.Inverter;
import costanza.MeanFilter;
import costanza.GradientDescent;
import costanza.IntensityFinder;
import costanza.PeakMerger;
import costanza.PeakRemover;

/**
 * The Text version of our GUI.
 * @author michael
 */
public class TextGUI {
    
    /** Creates a new instance of TextGUI */
    public TextGUI() throws Exception {

				int invertFlag=1, 
						meanFilterFlag=1, 
						gradientDescentFlag=1, 
						intensityFinderFlag=1, 
						peakMergerFlag=1, 
						peakRemoverFlag=0;

        System.out.println("Creating a Stack");
        Stack stack = new Stack();
        for(int i=0; i<10; ++i){
            Image image = new Image(10, 10);
            setTheImageRandom(image);
            stack.addImage(image);
        }
        //System.out.println("Creating an Image of size: " + image.getWidth() + " " + image.getHeight());
        //System.out.println("Text: Original Stack 1: " + stack.getDepth());
        System.out.println("Creating a Case");
        Case myCase = new Case(stack);
        //System.out.println("Filling images");
				//for(int i=0; i<myCase.getStack().getDepth(); ++i){
				//myCase.getStack().setIntensity(4,i+4,i,(i == 1) ? 1.0f : 0.0f);
				//}
				if (invertFlag!=0) {
						System.out.println("### Applying Invert ###");
						System.out.println("Creating options");
						Options options = new Options();
						System.out.println("Creating Processor");
						Inverter inverter = new Inverter();
						System.out.println("Running Processor");
						myCase = inverter.process(myCase, options);
						System.out.println("Done!\n");
				}
				if (meanFilterFlag!=0) {
						System.out.println("### Applying MeanFilter ###");
						System.out.println("Creating options");
						Options options = new Options();
						options.addOption("radius", new Float(3));
						System.out.println("Creating Processor");
						MeanFilter meanFilter = new MeanFilter();
						System.out.println("Running Processor");
						myCase = meanFilter.process(myCase, options);
						System.out.println("Done!\n");
				}
				if (gradientDescentFlag!=0) {
						System.out.println("### Applying GradientDescent ###");
						System.out.println("Creating options");
						Options options = new Options();
						System.out.println("Creating Processor");
						GradientDescent gradientDescent = new GradientDescent();
						System.out.println("Running Processor");
						myCase = gradientDescent.process(myCase, options);
						System.out.println("Done!\n");
				}
				if (intensityFinderFlag!=0) {
						System.out.println("### Applying IntensityFinder ###");
						System.out.println("Creating options");
						Options options = new Options();
						System.out.println("Creating Processor");
						IntensityFinder intensityFinder = new IntensityFinder();
						System.out.println("Running Processor");
						myCase = intensityFinder.process(myCase, options);
						System.out.println("Done!\n");
				}
				if (peakMergerFlag!=0) {
						System.out.println("### Applying PeakMerger ###");
						System.out.println("Creating options");
						Options options = new Options();
						options.addOption("radius", new Float(1));
						System.out.println("Creating Processor");
						PeakMerger peakMerger = new PeakMerger();
						System.out.println("Running Processor");
						myCase = peakMerger.process(myCase, options);
						System.out.println("Done!\n");
				}
				if (peakRemoverFlag!=0) {
						System.out.println("### Applying PeakRemover ###");
						System.out.println("Creating options");
						Options options = new Options();
						options.addOption("sizeThreshold", new Float(10));
						options.addOption("intensityThreshold", new Float(10));
						System.out.println("Creating Processor");
						PeakRemover peakRemover = new PeakRemover();
						System.out.println("Running Processor");
						myCase = peakRemover.process(myCase, options);
						System.out.println("Done!\n");
				}
				

        System.out.println("FINAL RESULT\n\n");				
        //for(int i=0; i<myCase.getStack().getDepth(); ++i){
				//  printImage(myCase.getStack().getImage(i));
        //}
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
     * Simple method for setting an initial image to a diagonal matrix.
     * @param image the image to set
     */
    private void setTheImageRandom(Image image){
        System.out.println("Image: " + image);
        for(int i=0; i<image.getWidth(); ++i){
            //System.out.println("Inside first loop");
            for(int j=0; j<image.getHeight(); ++j){
                //System.out.println("Inside second loop");
                image.setIntensity(i, j, (float)Math.random());
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
