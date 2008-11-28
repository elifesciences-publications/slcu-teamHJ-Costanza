
import costanza.Case;
import costanza.CellCenter;
import costanza.CellIntensity;
import costanza.DataId;
import costanza.Driver;
import costanza.Factory;
import costanza.Job;
import costanza.Options;
import costanza.Processor;
import costanza.Queue;
import costanza.Stack;
import java.awt.EventQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

public class Costanza_Plugin implements ij.plugin.PlugIn {

    public static int REQUEST_BOA_COLORIZER = 1;
    public static int REQUEST_BOA_INTENSITY_COLORIZER = 2;
    public static int REQUEST_CELL_MARKER = 4;
    public static int REQUEST_WORKING_STACK = 8;

    private void printOutOfMemoryError() {
        stop(PluginStatus.OUT_OF_MEMORY);
    }

    public enum PluginStatus {

        RUN_APPLICATION,
        EXIT_APPLICATION,
        OUT_OF_MEMORY
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
    private ExecutorService executor;
    /**
     * This is the starting point of execution after the user starts the 
     * analyze.
     * 
     * @param jobs The queue of jobs to the processed. 
     * @param secondaryStackOption True if a second stack is being used for
     * intensity measurements.
     */
    public int countDisplayRequests(int request) {
        int c; // c accumulates the total bits set in v
        for (c = 0; request != 0; c++) {
            request &= request - 1; // clear the least significant bit set
        }
        return c;
    }

    public void start(Queue jobs, boolean secondaryStackOption) {

        System.out.print("initializing... ");
        System.out.print(Thread.currentThread().getName());
        System.out.println( "; " + String.valueOf(EventQueue.isDispatchThread()));
        

        frame.setMenuAndButtonsEnabled(false);
        frame.setEnabledProcessingOptions(false);

        this.jobs = jobs;
        this.secondaryStackOption = secondaryStackOption;
        try {
            try {
                imagePlus = ij.IJ.getImage();
            } catch (Exception exception) {
                // Do nothing as we assume ImageJ is displaying a message about this exception.
                frame.setMenuAndButtonsEnabled(true);
                frame.setEnabledProcessingOptions(true);
                return;
            }
//                        frame.setProgress(5);
            frame.askForScale(imagePlus.getCalibration());
            scaleOptionPanelContinueButtonPressed();
//                         frame.setProgress(100);
        } catch (Exception exception) {
            printExceptionMessage(exception);
            status = Costanza_Plugin.PluginStatus.EXIT_APPLICATION;
            return;
        }
    }

    public MainFrame getFrame(){
        return frame;
    }
    
    public boolean getSecondatrStackOption(){
        return secondaryStackOption;
    }
    
    public void setSecondatrStackOption( boolean b){
        secondaryStackOption = b;
    }
    
    /**
     * This is the second point of execution. The user is asked to enter the
     * scale of the image and this function is called after the user presses 
     * Continue.
     */
    void scaleOptionPanelContinueButtonPressed() {
        try {
            
            stack = Utility.createStackFromImagePlus(imagePlus);
            IJCase = new Case(stack);
            GUIDriver driver = new GUIDriver(new GUIQueue(jobs, frame.getProgressTextField(), this), IJCase, factory);
//            Driver driver = new Driver(jobs, IJCase, factory);
//            driver.run();
//            System.out.println(Thread.currentThread().getName());
            String s = null;
            if(executor == null)
                executor = Executors.newFixedThreadPool(1);
//            guiQueue.addPropertyChangeListener( new PropertyChangeListener() {
//                 public  void propertyChange(PropertyChangeEvent evt) {
//              if ("progress".equals(evt.getPropertyName())) {
////                  progressBar.setValue((Integer)evt.getNewValue());
//                  frame.getProgressTextField().setText(((Integer)evt.getNewValue()).toString());
//              }
//          }
//      });
//            FutureTask<String> future = new FutureTask<String>(driver, s);
//            executor.execute(future);
            executor.execute(driver);
//            AWTEvent laste = null;
//            while (!future.isDone()) {
////                try {
////                    Thread.sleep(500);
//
//                AWTEvent e = EventQueue.getCurrentEvent();
//                if (e != laste) {
//                    System.out.println(EventQueue.getCurrentEvent().paramString());
//                    laste = e;
//                }
////                    System.out.println(EventQueue.getCurrentEvent().paramString());
////                } catch (Exception ie) {
////                    System.out.println("Will check after 1/2 sec.");
////                }
//            }
//            executor.shutdown();
//            if (secondaryStackOption == true) {
//                frame.askForSecondaryStack();
//            } else {
//                showFinalResults();
//                frame.setMenuAndButtonsEnabled(true);
//            }
        } catch (Exception exception) {
            printExceptionMessage(exception);
            status = Costanza_Plugin.PluginStatus.EXIT_APPLICATION;
            return;
        } catch (Error error) {
            if (OutOfMemoryError.class.isInstance(error)) {
                printOutOfMemoryError();
            }
            throw error;
        }
    }

    /** 
     * This is the optional third point of execution. The user is being asked to
     * active a second stack and this function is called after the user presses
     * Continue.
     */
    void secondaryStackOptionPanelContinueButtonPressed() throws Exception {
        try {
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

        } catch (Error error) {
            if (OutOfMemoryError.class.isInstance(error)) {
                printOutOfMemoryError();
            }
            throw error;
        }
//        frame.setMenuAndButtonsEnabled(true);
//        frame.setEnabledProcessingOptions(true);
    }

    void secondaryStackOptionPanelCanceleButtonPressed() throws Exception {
        try {
//            synchronized(this){
                secondaryStackOption = false;
//            }
            showFinalResults();
            secondaryStackOption = true;
        } catch (Error error) {
            if (OutOfMemoryError.class.isInstance(error)) {
                printOutOfMemoryError();
            }
            throw error;
        }
//        frame.setMenuAndButtonsEnabled(true);
//        frame.setEnabledProcessingOptions(true);
    }

    /**
     * Stop the plugin.
     */
    public void stop(PluginStatus status) {
        frame.setVisible(false);
        frame.dispose();
        this.status = status;
    }

    /** 
     * Display final results.
     */
    public void showFinalResults() throws Exception {
//        
//        processResultRequests(frame.getResultRequest());
//        frame.setEnabledDisplayOptions(false);
        executor.execute(new RunnProcessResults(secondaryStackOption));
//        printData(secondaryStackOption);

    }

    private class RunnProcessResults implements Runnable {

        private boolean secondaryStackOption;

        RunnProcessResults(boolean b) {
            secondaryStackOption = b;
        }

        public void run() {
            try {
                processResultRequests(frame.getResultRequest(), secondaryStackOption);
                printData(secondaryStackOption);
                EventQueue.invokeLater(new RunDisplayText("Finished"));
                EventQueue.invokeLater(new RunSetEnabledDisplay());
            } catch (Exception ex) {
                Logger.getLogger(Costanza_Plugin.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        
    }
private class RunDisplayText implements Runnable {

            private String message;

            public RunDisplayText(String s) {
                message = s;
            }

            public void run() {
                frame.setProgressTextField(message);
//                frame.setMenuAndButtonsEnabled(true);
//                frame.setEnabledDisplayOptions(true);
//                textField.setText(message);
            }
        }

    private class RunSetEnabledDisplay implements Runnable {

        public void run() {
            frame.setMenuAndButtonsEnabled(true);
            frame.setEnabledDisplayOptions(true);
        }
    }
    /**
     * Starting point of plugin.
     * @param arg ???
     * @todo Find a meaning of arg.
     */
    //@Override
    public void run(String arg) {
        final Costanza_Plugin self = this;
        try {
//            System.out.println(Thread.currentThread().getName());
            initFactory();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    try {
                        frame = new MainFrame(self);
                    } catch (Exception ex) {
                        Logger.getLogger(Costanza_Plugin.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    frame.setVisible(true);
                }
            });
            
//            frame = new MainFrame(this);
//            frame.setVisible(true);
            status = PluginStatus.RUN_APPLICATION;
            while (status == PluginStatus.RUN_APPLICATION) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Costanza_Plugin.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(Costanza_Plugin.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (status == PluginStatus.OUT_OF_MEMORY) {
            ij.IJ.showMessage("Out of memory", "Out of memory: Please consult the Costanza user manual for further information.");
        }
    }

    /**
     * Initiate the factory.
     */
    private void initFactory() {
        factory = new Factory<Processor>();
        factory.register("invert", costanza.Inverter.class);
        factory.register("meanfilter", costanza.MeanFilter.class);
        factory.register("medianfilter", costanza.MedianFilter.class);
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
    private void processResultRequests(int request, boolean secondaryStackOption) throws Exception {
        int c = countDisplayRequests(frame.getResultRequest());
        int counter = 0;
        if ((request & REQUEST_CELL_MARKER) == REQUEST_CELL_MARKER) {
            EventQueue.invokeLater(new RunDisplayText("Display task " + String.valueOf(++counter) + " out of "+ String.valueOf(c)));
            Options options = new Options();
            options.addOption("markNeighbors", frame.getIOPanel().getMarkerRadius() - 1);
            Job job = new Job("cellmarker", options);
            displayResult("Costanza - Cell centers", job);
        }
        if ((request & REQUEST_BOA_COLORIZER) == REQUEST_BOA_COLORIZER) {
            EventQueue.invokeLater(new RunDisplayText("Display task " + String.valueOf(++counter) + " out of "+ String.valueOf(c)));
            Job job = new Job("boacolorize", null);
            displayResult("Costanza - Basins of attractions (BOA)", job);
        }
        if ((request & REQUEST_BOA_INTENSITY_COLORIZER) == REQUEST_BOA_INTENSITY_COLORIZER) {
            EventQueue.invokeLater(new RunDisplayText("Display task " + String.valueOf(++counter) + " out of "+ String.valueOf(c)));
            Options options = new Options();
            if (secondaryStackOption == true) {
                options.addOption("OverrideStack", secondaryStack);
            }
            Job job = new Job("boaintensitycolorize", options);
            displayResult("Costanza - Basins of attractions (BOA)-intensity", job);
        }
        if ((request & REQUEST_WORKING_STACK) == REQUEST_WORKING_STACK) {
            EventQueue.invokeLater(new RunDisplayText("Display task " + String.valueOf(++counter) + " out of "+ String.valueOf(c)));
            Stack workingStack = IJCase.getStack();
            ij.ImagePlus workingStackImagePlus = Utility.createImagePlusFromStack(workingStack, "Costanza - Working stack");
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
    private void printData(boolean secondaryStackOption) throws Exception {
        float xScale = IJCase.getStack().getXScale();
        float yScale = IJCase.getStack().getYScale();
        float zScale = IJCase.getStack().getZScale();
        float volumeScale = xScale * yScale * zScale;

        String tab = "\t";
        String newline = "\n";
        ij.IJ.setColumnHeadings("Cell id\tx\ty\tz\tBoa volume\tMean cell intensity");

        int id;
//        synchronized (this) {
            if (secondaryStackOption == true) {
                id = secondaryStack.getId();
            } else {
                id = stack.getId();
            }
//        }
        java.util.Set<Integer> cellIds = IJCase.getCellIds();
        java.util.Iterator<Integer> iterator = cellIds.iterator();

        while (iterator.hasNext()) {
            Integer i = iterator.next();
            String line = "";
            CellCenter cellCenter = (CellCenter) IJCase.getCellData(DataId.CENTERS, i);
            CellIntensity cellIntensity = (CellIntensity) IJCase.getCellData(DataId.INTENSITIES, i);
            //BOA cellBoa = (BOA) IJCase.getCellData(DataId.BOAS, i);
            int cellSize = IJCase.getCell(i).size();

            float x = cellCenter.getX() * xScale;
            float y = cellCenter.getY() * yScale;
            float z = cellCenter.getZ() * zScale;
            float volume = cellSize * volumeScale;
            float intensity = cellIntensity.getIntensity(id + "mean") * (float) Case.COSTANZA_INTENSITY_LEVELS;
            line += i + tab + x + tab + y + tab + z + tab + volume + tab + intensity;
            ij.IJ.write(line);
        }
    }
}

