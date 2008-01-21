
import costanza.Case;
import costanza.CellCenter;
import costanza.CellIntensity;
import costanza.BOA;
import costanza.DataId;
import costanza.Driver;
import costanza.Factory;
import costanza.Job;
import costanza.Options;
import costanza.Processor;
import costanza.Queue;
import costanza.Stack;
import ij.ImagePlus;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Costanza_Plugin implements ij.plugin.PlugIn {

	public static int REQUEST_BOA_COLORIZER = 1;
	public static int REQUEST_BOA_INTENSITY_COLORIZER = 2;
	public static int REQUEST_CELL_MARKER = 4;
	public static int REQUEST_WORKING_STACK = 8;

	private enum PluginStatus {

		RUN_APPLICATION,
		EXIT_APPLICATION,
		CANCEL_DIALOG,
		CONTINUE_DIALOG,
		WAITING_FOR_SECONDARY_STACK_CHOOSER
	}
	private Case IJCase;
	private Factory<Processor> factory;
	private MainFrame frame;
	private PluginStatus status;
	private Stack secondaryStack;

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

	void secondaryStackChooserButtonPressed() throws Exception {
		ImagePlus imagePlus = ij.IJ.getImage();
		secondaryStack = Utility.createStackFromImagePlus(imagePlus);
		
		Options options = new Options();
		options.addOption("OverrideStack", secondaryStack);
		Job job = new Job("intensityfinder", options);
		Queue jobs = new Queue();
		jobs.addJob(job);

		Driver driver = new Driver(jobs, IJCase, factory);
		driver.run();

		showFinalResults(frame, true, secondaryStack.getId());
	}

	private void showFinalResults(MainFrame frame, boolean secondaryStackOption, int id) throws Exception {
		processResultRequests(IJCase, secondaryStackOption, frame.getResultRequest());
		displayData(IJCase, id);
	}

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
		factory.register("boaintensitycolorize", costanza.BoaColorizerIntensity.class);
		factory.register("cellmarker", costanza.CellCenterMarker.class);
	}

	public void start(Queue jobs, boolean secondaryStackOption) {
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
			IJCase = new Case(stack);

			Driver driver = new Driver(jobs, IJCase, factory);
			driver.run();

			if (secondaryStackOption == true) {
				frame.askForSecondaryStack();
			} else {
				showFinalResults(frame, secondaryStackOption, stack.getId());
			}
		} catch (Exception exception) {
			displayExceptionMessage(exception);
			status = Costanza_Plugin.PluginStatus.EXIT_APPLICATION;
			return;
		}
	}

	static public void displayExceptionMessage(Exception exception) {
		exception.printStackTrace();
		ij.IJ.showMessage("Costanza Plugin", "Caught exception: " + exception.getMessage());
	}

	private void processResultRequests(Case IJCase, boolean secondaryStackOption, int request) throws Exception {
		if ((request & REQUEST_BOA_COLORIZER) == REQUEST_BOA_COLORIZER) {
			Job job = new Job("boacolorize", null);
			displayResult("Costanza - Basins of attractions (BOA)", job, IJCase, factory);
		}
		if ((request & REQUEST_BOA_INTENSITY_COLORIZER) == REQUEST_BOA_INTENSITY_COLORIZER) {
			Options options = new Options();
			if (secondaryStackOption == true) {
				options.addOption("OverrideStack", secondaryStack);
			}
			Job job = new Job("boaintensitycolorize", options);
			displayResult("Costanza - Basins of attractions (BOA)", job, IJCase, factory);
		}
		if ((request & REQUEST_CELL_MARKER) == REQUEST_CELL_MARKER) {
			Job job = new Job("cellmarker", null);
			displayResult("Costanza - Cell centers", job, IJCase, factory);
		}
		if ((request & REQUEST_WORKING_STACK) == REQUEST_WORKING_STACK) {
			Stack stack = IJCase.getStack();
			ImagePlus imagePlus = Utility.createImagePlusFromStack(stack, "Costanza - Working stack");
			imagePlus.show();
		}
	}

	private void displayResult(String name, Job job, Case IJCase, Factory<Processor> factory) throws Exception {
		Queue jobs = new Queue();
		jobs.addJob(job);
		Driver driver = new Driver(jobs, IJCase, factory);
		driver.run();
		ij.ImagePlus imagePlus = Utility.createImagePlusFromResultStack(IJCase, name);
		imagePlus.show();
	}

	private void displayData(Case IJCase, int id) throws Exception {
		float xScale = IJCase.getStack().getXScale();
		float yScale = IJCase.getStack().getYScale();
		float zScale = IJCase.getStack().getZScale();
		float volumeScale = xScale * yScale * zScale;

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

			float x = cellCenter.getX() * xScale;
			float y = cellCenter.getY() * yScale;
			float z = cellCenter.getZ() * zScale;
			float volume = cellBoa.size() * volumeScale;
			line += i + tab + x + tab + y + tab + z + tab + volume + tab + cellIntensity.getIntensity(id + "mean");
			ij.IJ.write(line);
		}
	}
}

