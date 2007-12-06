
import costanza.Case;
import costanza.CellCenter;
import costanza.DataId;
import costanza.Driver;
import costanza.Factory;
import costanza.Processor;
import costanza.Queue;
import costanza.Stack;
import ij.IJ;
import ij.ImagePlus;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

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
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				Logger.getLogger(Costanza_Plugin.class.getName()).log(Level.SEVERE, null, ex);
			}
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
		factory.register("gradientdescent", costanza.GradientDescent.class);
		factory.register("peakremover", costanza.PeakRemover.class);
		factory.register("peakmerger", costanza.PeakMerger.class);
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
			panel.addJobs(jobs);

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
		ij.IJ.setColumnHeadings("Cell id\tx\ty\tz");
		
		Object[] cellCenters = IJCase.getCellData(DataId.cellCenters).toArray();
		
		for (int i = 0; i < cellCenters.length; ++i) {
			String line = new String();
			line += i + "\t";
			CellCenter cellCenter = (CellCenter) cellCenters[i];
			line += cellCenter.getX() + "\t" + cellCenter.getY() + "\t" + cellCenter.getZ();
			ij.IJ.write(line);
		}
	}
}


