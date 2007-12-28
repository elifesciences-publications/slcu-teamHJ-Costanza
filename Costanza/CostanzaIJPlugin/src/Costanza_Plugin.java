
import costanza.Case;
import costanza.CellCenter;
import costanza.CellIntensity;
import costanza.BOA;
import costanza.DataId;
import costanza.Driver;
import costanza.Factory;
import costanza.Processor;
import costanza.Queue;
import costanza.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Costanza_Plugin implements ij.plugin.PlugIn {

	void scaleDialogCancel(ScaleDialog dialog) {
		status = PluginStatus.CANCEL_DIALOG;
		dialog.setVisible(false);
	}

	MainFrame getMainFrame() {
		return frame;
	}

	void scaleDialogContinue(ScaleDialog dialog) {
		status = PluginStatus.CONTINUE_DIALOG;
		dialog.setVisible(false);
	}

	private enum PluginStatus {

		RUN_APPLICATION,
		EXIT_APPLICATION,
		CANCEL_DIALOG,
		CONTINUE_DIALOG
	}
	private Factory<Processor> factory;
	private MainFrame frame;
	private PluginStatus status;

	public void run(String arg) {
		try {
			initFactory();
			frame = new MainFrame(this);
			frame.setVisible(true);
			status = PluginStatus.RUN_APPLICATION;
			while (status != PluginStatus.EXIT_APPLICATION) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ex) {
					Logger.getLogger(Costanza_Plugin.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		} catch (Exception ex) {
			Logger.getLogger(Costanza_Plugin.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	void badUserInput() {
		Utility.printError("Bad user input!");
	}

	void stop() {
		frame.setVisible(false);
		frame.dispose();
		status = PluginStatus.EXIT_APPLICATION;
	}

	private void initFactory() {
		factory = new Factory<Processor>();
		factory.register("invert", costanza.Inverter.class);
		factory.register("meanfilter", costanza.MeanFilter.class);
		factory.register("null", costanza.NullProcessor.class);
		factory.register("gradientdescent", costanza.GradientDescent.class);
		factory.register("peakremover", costanza.PeakRemover.class);
		factory.register("peakmerger", costanza.PeakMerger.class);
		factory.register("backgroundextractor", costanza.BackgroundFinderIntensity.class);
		factory.register("intensityfinder", costanza.IntensityFinder.class);
		factory.register("boacolorize", costanza.BoaColorizer.class);
	}

	public void start(MainPanel panel) {
		try {
			ij.ImagePlus imagePlus;
			try {
				imagePlus = ij.IJ.getImage();
			} catch (Exception exception) {
				// Do nothing as we assume ImageJ is displaying a message about this exception.
				return;
			}

			ScaleDialog scaleDialog = new ScaleDialog(this, true, imagePlus.getCalibration());
			scaleDialog.setVisible(true);

			switch (status) {
				case CANCEL_DIALOG:
					return;
				case CONTINUE_DIALOG:
					status = PluginStatus.RUN_APPLICATION;
					break;
				default:
					throw new Exception("Unexpected error in start()");
			}

			Stack stack = Utility.createStackFromImagePlus(imagePlus);
			Case IJCase = new Case(stack);

			Queue jobs = new Queue();
			panel.addJobs(jobs);

			Driver driver = new Driver(jobs, IJCase, factory);
			driver.run();

			Stack result = IJCase.getStack();

			ij.ImagePlus ip = Utility.createImagePlusFromStack(result);
			ip.show();

			displayData(IJCase);
		} catch (Exception exception) {
			ij.IJ.showMessage("Costanza Plugin", "Caught exception: " + exception.getMessage());
			exception.printStackTrace();
			status = Costanza_Plugin.PluginStatus.EXIT_APPLICATION;
			return;
		}
	}

	private void displayData(Case IJCase) {
		float xScale = IJCase.getStack().getXScale();
		float yScale = IJCase.getStack().getYScale();
		float zScale = IJCase.getStack().getZScale();
		float volumeScale = xScale*yScale*zScale;

		String tab = "\t";
		String newline = "\n";
		ij.IJ.setColumnHeadings("Cell id\tx\ty\tz\tBoa volume\tMean cell intensity");
		

		java.util.Set<Integer> cellIds = IJCase.getCellIds();
		java.util.Iterator<Integer> iterator = cellIds.iterator();
		while (iterator.hasNext()) {
			Integer i = iterator.next();
			String line = "";
			CellCenter cellCenter = (CellCenter) IJCase.getCellData(DataId.CENTERS, i);
			CellIntensity cellIntensity = (CellIntensity) IJCase.getCellData(DataId.INTENSITIES, i);
			BOA cellBoa = (BOA) IJCase.getCellData(DataId.BOAS, i);

			float x = cellCenter.getX()*xScale;
			float y = cellCenter.getY()*yScale;
			float z = cellCenter.getZ()*zScale;
			float volume = cellBoa.size()*volumeScale;
			line += i + tab + x + tab + y + tab + z + tab + volume + tab + cellIntensity.getIntensity(0);
			ij.IJ.write(line);
		}
	}
}

