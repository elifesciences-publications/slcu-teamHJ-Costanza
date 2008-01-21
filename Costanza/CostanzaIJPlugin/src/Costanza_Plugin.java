
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
import java.util.logging.Level;
import java.util.logging.Logger;

public class Costanza_Plugin implements ij.plugin.PlugIn {

	public static int REQUEST_BOA_COLORIZER = 1;
	public static int REQUEST_BOA_INTENSITY_COLORIZER = 2;
	public static int REQUEST_CELL_MARKER = 4;
	public static int REQUEST_WORKING_STACK = 8;

	private enum PluginStatus {

		RUN_APPLICATION,
		EXIT_APPLICATION
	}
	private Case IJCase;
	private Stack stack;
	private Factory<Processor> factory;
	private MainFrame frame;
	private PluginStatus status;
	private Stack secondaryStack;
	private ij.ImagePlus imagePlus;
	private Queue jobs;
	private boolean secondaryStackOption;

	/**
	 * This is the starting point of execution after the user starts the 
	 * analyze.
	 * 
	 * @param jobs The queue of jobs to the processed. 
	 * @param secondaryStackOption True if a second stack is being used for
	 * intensity measurements.
	 */
	public void start(Queue jobs, boolean secondaryStackOption) {
		this.jobs = jobs;
		this.secondaryStackOption = secondaryStackOption;
		try {
			try {
				imagePlus = ij.IJ.getImage();
			} catch (Exception exception) {
				// Do nothing as we assume ImageJ is displaying a message about this exception.
				return;
			}
			frame.askForScale(imagePlus.getCalibration());
		} catch (Exception exception) {
			printExceptionMessage(exception);
			status = Costanza_Plugin.PluginStatus.EXIT_APPLICATION;
			return;
		}
	}

	/**
	 * This is the second point of execution. The used is asked to enter the
	 * scale of the image and this function is called after the user presses 
	 * Continue.
	 */
	void scaleOptionPanelContinueButtonPressed() {
		try {
			stack = Utility.createStackFromImagePlus(imagePlus);
			IJCase = new Case(stack);

			Driver driver = new Driver(jobs, IJCase, factory);
			driver.run();

			if (secondaryStackOption == true) {
				frame.askForSecondaryStack();
			} else {
				showFinalResults();
			}
		} catch (Exception exception) {
			printExceptionMessage(exception);
			status = Costanza_Plugin.PluginStatus.EXIT_APPLICATION;
			return;
		}
	}

	/** 
	 * This is the option third point of execution. The user is being asked to
	 * active a second stack and this function is called after the user presses
	 * Continue.
	 */
	void secondaryStackOptionPanelContinueButtonPressed() throws Exception {
		ij.ImagePlus secondaryStackImagePlus = ij.IJ.getImage();
		secondaryStack = Utility.createStackFromImagePlus(secondaryStackImagePlus);

		Options options = new Options();
		options.addOption("OverrideStack", secondaryStack);
		Job job = new Job("intensityfinder", options);
		Queue secondaryStackJobs = new Queue();
		secondaryStackJobs.addJob(job);

		Driver driver = new Driver(secondaryStackJobs, IJCase, factory);
		driver.run();

		showFinalResults();
	}

	/**
	 * Stop the plugin.
	 */
	public void stop() {
		frame.setVisible(false);
		frame.dispose();
		status = PluginStatus.EXIT_APPLICATION;
	}

	/** 
	 * Display final results.
	 */
	private void showFinalResults() throws Exception {
		processResultRequests(frame.getResultRequest());
		printData();
	}

	/**
	 * Starting point of plugin.
	 * @param arg ???
	 * @todo Find a meaning of arg.
	 */
	@Override
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

	/**
	 * Initiate the factory.
	 */
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

	/**
	 * Use ImageJ's built in function to present an exception related error 
	 * message.
	 * 
	 * @param exception Exception just caught by the application.
	 * 
	 * @todo Change showMessage to a better error message function?
	 */
	static public void printExceptionMessage(Exception exception) {
		exception.printStackTrace();
		ij.IJ.showMessage("Costanza Plugin", "Caught exception: " + exception.getMessage());
	}

	/**
	 * Display results according to the user's request.
	 * 
	 * @param request Request from the user.
	 * @throws java.lang.Exception
	 */
	private void processResultRequests(int request) throws Exception {
		if ((request & REQUEST_BOA_COLORIZER) == REQUEST_BOA_COLORIZER) {
			Job job = new Job("boacolorize", null);
			displayResult("Costanza - Basins of attractions (BOA)", job);
		}
		if ((request & REQUEST_BOA_INTENSITY_COLORIZER) == REQUEST_BOA_INTENSITY_COLORIZER) {
			Options options = new Options();
			if (secondaryStackOption == true) {
				options.addOption("OverrideStack", secondaryStack);
			}
			Job job = new Job("boaintensitycolorize", options);
			displayResult("Costanza - Basins of attractions (BOA)", job);
		}
		if ((request & REQUEST_CELL_MARKER) == REQUEST_CELL_MARKER) {
			Job job = new Job("cellmarker", null);
			displayResult("Costanza - Cell centers", job);
		}
		if ((request & REQUEST_WORKING_STACK) == REQUEST_WORKING_STACK) {
			Stack stack = IJCase.getStack();
			ij.ImagePlus workingStackImagePlus = Utility.createImagePlusFromStack(stack, "Costanza - Working stack");
			workingStackImagePlus.show();
		}
	}

	/**
	 * Display a result.
	 * 
	 * @param name Name of result.
	 * @param job Job to be used to retrieve result.
	 * @throws java.lang.Exception
	 */
	private void displayResult(String name, Job job) throws Exception {
		Queue displayJobs = new Queue();
		displayJobs.addJob(job);
		Driver driver = new Driver(displayJobs, IJCase, factory);
		driver.run();
		ij.ImagePlus displayImagePlus = Utility.createImagePlusFromResultStack(IJCase, name);
		displayImagePlus.show();
	}

	/**
	 * Print data from the case.
	 * 
	 * @throws java.lang.Exception
	 */
	private void printData() throws Exception {
		float xScale = IJCase.getStack().getXScale();
		float yScale = IJCase.getStack().getYScale();
		float zScale = IJCase.getStack().getZScale();
		float volumeScale = xScale * yScale * zScale;

		String tab = "\t";
		String newline = "\n";
		ij.IJ.setColumnHeadings("Cell id\tx\ty\tz\tBoa volume\tMean cell intensity");

		int id;
		if (secondaryStackOption == true) {
			id = secondaryStack.getId();
		} else {
			id = stack.getId();
		}

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

