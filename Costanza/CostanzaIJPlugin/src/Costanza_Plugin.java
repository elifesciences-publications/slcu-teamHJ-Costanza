
import costanza.Case;
import costanza.Driver;
import costanza.Factory;
import costanza.Job;
import costanza.Options;
import costanza.Processor;
import costanza.Queue;
import costanza.Stack;
import ij.IJ;
import ij.ImagePlus;

public class Costanza_Plugin implements ij.plugin.PlugIn {

	private Factory<Processor> factory;
	private MainFrame frame;
	private boolean pluginIsRunning;

	public void run(String arg) {
		initFactory();
		frame = new MainFrame(this);
		frame.setVisible(true);
		pluginIsRunning = true;
		while (pluginIsRunning == true) {
		// Wait for plugin to finish...
		}
	}

	void badUserInput() {
		Utility.printError("Bad user input!");
	}

	void stop() {
		frame.setVisible(false);
		frame.dispose();
		pluginIsRunning = false;
	}

	private void initFactory() {
		factory = new Factory<Processor>();
		factory.register("invert", costanza.Inverter.class);
		factory.register("meanfilter", costanza.MeanFilter.class);
		factory.register("null", costanza.NullProcessor.class);
	}

	public void start(MainPanel panel) {
		try {
			ImagePlus imagePlus;
			try {
				imagePlus = IJ.getImage();
			} catch (Exception exception) {
				// Do nothing as we assume ImageJ is displaying a message about this exception.
				return;
			}

			Stack stack = Utility.createStackFromImagePlus(imagePlus);
			Case IJCase = new Case(stack);

			Queue jobs = new Queue();
			
			// Add inverter.
			jobs.addJob(panel.getInvertJob());
				
			Driver driver = new Driver(jobs, IJCase, factory);
			driver.run();

			Stack result = IJCase.getStack();

			ImagePlus ip = Utility.createImagePlusFromStack(result);
			ip.show();

			displayData(IJCase);

		} catch (Exception exception) {
			IJ.showMessage("Costanza Plugin", "Caught exception: " + exception.getMessage());
		}
	}

	private void displayData(Case IJCase) {
//		Data data = IJCase.getData();

		ij.IJ.setColumnHeadings("Cell id\tx\ty\tsize\tintensity");

		ij.IJ.write("1\t1.0\t2.3\t45\t0.12");
		ij.IJ.write("2\t1.4\t1.3\t23\t0.7");
	}
}


