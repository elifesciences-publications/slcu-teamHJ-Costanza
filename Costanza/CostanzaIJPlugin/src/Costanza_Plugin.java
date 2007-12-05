
import costanza.Case;
import costanza.Data;
import costanza.Driver;
import costanza.Factory;
import costanza.Job;
import costanza.Options;
import costanza.Queue;
import costanza.Stack;
import ij.IJ;
import ij.ImagePlus;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Costanza_Plugin implements ij.plugin.PlugIn {

    private Factory factory;
    private MainFrame frame;

    @Override
    public void run(String arg) {
	initFactory();
	frame = new MainFrame(this);
	frame.setVisible(true);
    }

    void badUserInput() {
	printError("Bad user input!");
    }

    private void initFactory() {
	factory = new Factory();
	factory.register("invert", costanza.Inverter.class);
	factory.register("meanfilter", costanza.MeanFilter.class);
    }

    public void start(float radius, int repeat, boolean invert) {
	try {
	    ImagePlus imagePlus;
	    try {
		imagePlus = IJ.getImage();
	    } catch (Exception exception) {
		printWarning("Unable to retrieve image(s) from ImageJ.");
		return;
	    }

	    Stack stack = Tools.createStackFromImagePlus(imagePlus);
	    Case IJCase = new Case(stack);

	    Queue jobs = new Queue();

	    for (int i = 0; i < repeat; ++i) {
		Options option = new Options();
		option.addOption("radius", new Float(radius));
		jobs.addJob(new Job("meanfilter", option));
	    }

	    if (invert == true) {
		jobs.addJob(new Job("invert", null));
	    }

	    Driver driver = new Driver(jobs, IJCase, factory);
	    driver.run();

	    Stack result = IJCase.getStack();

	    ImagePlus ip = Tools.createImagePlusFromStack(result);
	    ip.show();

	    displayData(IJCase);

	} catch (Exception exception) {
	    IJ.showMessage("Costanza Plugin", "Caught exception: " + exception.getMessage());
	}
    }

    private void printWarning(String string) {
	ij.IJ.showMessage("Costanza Plugin", "Warning: " + string + "\n");
    }

    private void printError(String string) {
	ij.IJ.showMessage("Costanza Plugin", "Error: " + string + "\n");
    }

    private void displayData(Case IJCase) {
	Data data = IJCase.getData();

	ij.IJ.setColumnHeadings("Cell id\tx\ty\tsize\tintensity");

	ij.IJ.write("1\t1.0\t2.3\t45\t0.12");
	ij.IJ.write("2\t1.4\t1.3\t23\t0.7");


    }
}


