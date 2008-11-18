
import costanza.Options;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class ConfigurationFileManager extends JFileChooser {

    public static final String WORKING_DIR_GUI = "workingDirectory";
    public static final String EXTENDED_NEIGHBORHOOD_GUI = "useExtendedNeighborhood";
    public static final String PLATAEU_MARKER_GUI = "usePlateauMarker";
    public static final String INTENSITY_LEVELS_GUI = "intensityLevelsNumber";
    public static final String SECONDARY_STACK_GUI = "useSecondaryStack";
    public static final String CELL_CENTERS_GUI = "markCellCenters";
    public static final String BOAS_GUI = "displayBOAs";
    public static final String INTENSITY_BOAS_GUI = "displayIntensityBOAs";
    public static final String WORKING_STACK_GUI = "displayWorkingStack";
    public static final String BACKGROUND_GUI = "backgroundExtraction";
    public static final String MEAN_FILTER_GUI = "meanFilter";
    public static final String MEDIAN_FILTER_GUI = "medianFilter";
    public static final String INVERTER_GUI = "inverter";   
    public static final String PEAK_REMOVER_GUI = "peakRemover";
    public static final String PEAK_MERGER_GUI = "peakMerger";
    public static final String IMAGEJCALIBRATION_GUI = "useImageJCalibration";
    public static final String SCALES_GUI = "scales";
    
    public static final String WRK_DIR = "./plugins/Costanza";
    public static final String LAST_FILE = WRK_DIR + "/last." + ConfigurationFileFilter.ext;
    public static final String DEFAULT_FILE = WRK_DIR + "/default." + ConfigurationFileFilter.ext;

    public ConfigurationFileManager(MainFrame f) {
        addChoosableFileFilter(new ConfigurationFileFilter());
        frame = f;
        props = new java.util.Properties();
        File wrkDir = new File(WRK_DIR);
        File last = new File(LAST_FILE);
        File def = new File(DEFAULT_FILE);
        
        if(last.exists()){
//            System.out.println("Loading last configuration");
            loadProperties(last);
        }
        else if(def.exists()){
//            System.out.println("Loading default configuration");
            loadProperties(def);
        }
        else{
            setCurrentDirectory(wrkDir);
            props.setProperty(WORKING_DIR_GUI, wrkDir.getPath());
            props.setProperty(EXTENDED_NEIGHBORHOOD_GUI, "false");
            props.setProperty(PLATAEU_MARKER_GUI, "false");
            props.setProperty(INTENSITY_LEVELS_GUI, "256");
            props.setProperty(SECONDARY_STACK_GUI, "false");
            props.setProperty(CELL_CENTERS_GUI, "true");
            props.setProperty(BOAS_GUI, "false");
            props.setProperty(INTENSITY_BOAS_GUI, "false");
            props.setProperty(WORKING_STACK_GUI, "false");

            props.setProperty(BACKGROUND_GUI, "50");
            props.setProperty(MEAN_FILTER_GUI, "2 2");

            props.setProperty(PEAK_REMOVER_GUI, "10 10");
            props.setProperty(PEAK_MERGER_GUI, "10");

            props.setProperty(IMAGEJCALIBRATION_GUI, "true");
            props.setProperty(SCALES_GUI, "1 1 1");
        }
    }

    public void saveProperties(File f) {
        try {
            retriveGUIProperties();
            FileOutputStream input = new FileOutputStream(f);
            props.store(input, "Costanza configuration");
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    public void loadProperties(File f) {
        try {
            props.clear();
            FileInputStream input = new FileInputStream(f);
            props.load(input);
            input.close();
//            props.list(System.out);
            setCurrentDirectory(new File(props.getProperty(WORKING_DIR_GUI)));
            
            ProcessorOptionPanel pre = frame.getPreProcessorPanel();
            pre.removeAllOptions();
            ProcessorOptionPanel post = frame.getPostProcessorPanel();
            post.removeAllOptions();

            addOrderedProcessors();
            setGUIProperties();
            
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public void addOrderedProcessors() throws Exception {
        Vector<String> vec = new Vector<String>(props.size());
        vec.setSize(props.size());
        Enumeration e = props.propertyNames();
        Pattern p = Pattern.compile("(^.+)(\\d+$)");

        while (e.hasMoreElements()) {
            String key = ((String) e.nextElement()).trim();
            Matcher m = p.matcher(key);
            if (m.matches()) {
                String procStr = m.group(1);
                Integer i = Integer.parseInt(m.group(2));
                if (procStr.equals(INVERTER_GUI)) {
                    vec.set(i, "Invert image");
                } else if (procStr.equals(MEAN_FILTER_GUI)) {
                    vec.set(i, "Mean filter");
                } else if (procStr.equals(MEDIAN_FILTER_GUI)) {
                    vec.set(i, "Median filter");
                } else if (procStr.equals(BACKGROUND_GUI)) {
                    vec.set(i, "Background extraction");
                } else if (procStr.equals(PEAK_REMOVER_GUI)) {
                    vec.set(i, "Peak remover");
                } else if (procStr.equals(PEAK_MERGER_GUI)) {
                    vec.set(i, "Peak merger");
                } else {
                    throw new Exception("Unidentified processor option: " + procStr + "(" + i + ")");
                }
            }
        }
        while(vec.lastElement() == null){
            vec.setSize(vec.size()-1);
        }
//        System.out.println(vec);
        ProcessorOptionPanel pre = frame.getPreProcessorPanel();
        ProcessorOptionPanel post = frame.getPostProcessorPanel();
        
        Iterator<String> iter = vec.iterator();
        while(iter.hasNext()){
            String n = iter.next();
            if(pre.hasOptionInMenu(n)){
                pre.addOptionPanel(n);
            }
            else if(post.hasOptionInMenu(n)){
                post.addOptionPanel(n);
            }
        }
    }

    public void retriveGUIProperties() {
        props.clear();
        props.setProperty(WORKING_DIR_GUI, getCurrentDirectory().getPath());
        IOOptionPanel ioPanel = frame.getIOPanel();

        props.setProperty(EXTENDED_NEIGHBORHOOD_GUI, String.valueOf(ioPanel.getExtendedNeighborhoodOption()));
        props.setProperty(PLATAEU_MARKER_GUI, String.valueOf(ioPanel.getPlateauOption()));
        props.setProperty(INTENSITY_LEVELS_GUI, String.valueOf(ioPanel.getIntensityLevels()));
        props.setProperty(SECONDARY_STACK_GUI, String.valueOf(ioPanel.getSecondaryStackOption()));
        props.setProperty(CELL_CENTERS_GUI, String.valueOf((ioPanel.getResultRequest() & Costanza_Plugin.REQUEST_CELL_MARKER) != 0));
        props.setProperty(BOAS_GUI, String.valueOf((ioPanel.getResultRequest() & Costanza_Plugin.REQUEST_BOA_COLORIZER) != 0));
        props.setProperty(INTENSITY_BOAS_GUI, String.valueOf((ioPanel.getResultRequest() & Costanza_Plugin.REQUEST_BOA_INTENSITY_COLORIZER) != 0));
        props.setProperty(WORKING_STACK_GUI, String.valueOf((ioPanel.getResultRequest() & Costanza_Plugin.REQUEST_WORKING_STACK) != 0));

        ProcessorOptionPanel pre = frame.getPreProcessorPanel();
        java.util.Iterator<OptionPanel> opIter = pre.getOptionIterator();
        int c = 0;
        while (opIter.hasNext()) {
            try {
                OptionPanel optionPanel = opIter.next();

                ProcessorOption procOpt = optionPanel.getProcessorOption();
                setPropertyFromProcessorOption(procOpt, c);
                ++c;
            } catch (Exception e) {
                ij.IJ.showMessage("ConfigurationFileManager: " + e.getMessage());
            }
        }

        ProcessorOptionPanel post = frame.getPostProcessorPanel();
        opIter = post.getOptionIterator();
        while (opIter.hasNext()) {
            try {
                OptionPanel optionPanel = opIter.next();
                ProcessorOption procOpt = optionPanel.getProcessorOption();
                setPropertyFromProcessorOption(procOpt, c);
                ++c;
            } catch (Exception e) {
                ij.IJ.showMessage("ConfigurationFileManager: " + e.getMessage());
            }
        }

        ScaleOptionPanel scalePanel = frame.getScaleOptionPanel();
        props.setProperty(IMAGEJCALIBRATION_GUI, String.valueOf(scalePanel.getIJCalibrationOption()));
        props.setProperty(SCALES_GUI, String.valueOf(scalePanel.getScaleX()) + " " + String.valueOf(scalePanel.getScaleY()) + " " + String.valueOf(scalePanel.getScaleZ()));

    }

    public void setGUIProperties() {

        File wrkDir = new File(props.getProperty(WORKING_DIR_GUI));
        setCurrentDirectory(wrkDir);
        IOOptionPanel ioPanel = frame.getIOPanel();

        ioPanel.setExtendedNeighborhoodOption(Boolean.parseBoolean(props.getProperty(EXTENDED_NEIGHBORHOOD_GUI)));
        ioPanel.setPlateauOption(Boolean.parseBoolean(props.getProperty(PLATAEU_MARKER_GUI)));
        ioPanel.setIntensityLevels(Integer.parseInt(props.getProperty(INTENSITY_LEVELS_GUI)));
        ioPanel.setSecondaryStackOption(Boolean.parseBoolean(props.getProperty(SECONDARY_STACK_GUI)));
        ioPanel.setCellCenterRequest(Boolean.parseBoolean(props.getProperty(CELL_CENTERS_GUI)));
        ioPanel.setBOARequest(Boolean.parseBoolean(props.getProperty(BOAS_GUI)));
        ioPanel.setBOAIntensityRequest(Boolean.parseBoolean(props.getProperty(INTENSITY_BOAS_GUI)));
        ioPanel.setWorkingStackRequest(Boolean.parseBoolean(props.getProperty(WORKING_STACK_GUI)));


        ProcessorOptionPanel pre = frame.getPreProcessorPanel();
        java.util.Iterator<OptionPanel> opIter = pre.getOptionIterator();
        int c = 0;
        while (opIter.hasNext()) {
            try {
                OptionPanel optionPanel = opIter.next();
                ProcessorOption procOpt = optionPanel.getProcessorOption();
                setProcessorOption(procOpt, c);
                ++c;
            } catch (Exception e) {
                ij.IJ.showMessage("ConfigurationFileManager: " + e.getMessage());
            }
        }

        ProcessorOptionPanel post = frame.getPostProcessorPanel();
        opIter = post.getOptionIterator();
        while (opIter.hasNext()) {
            try {
                OptionPanel optionPanel = opIter.next();
                ProcessorOption procOpt = optionPanel.getProcessorOption();
                setProcessorOption(procOpt, c);
                ++c;
            } catch (Exception e) {
                ij.IJ.showMessage("ConfigurationFileManager: " + e.getMessage());
            }
        }

        ScaleOptionPanel scalePanel = frame.getScaleOptionPanel();

        StringTokenizer st = new StringTokenizer(props.getProperty(SCALES_GUI));
        float x = Float.parseFloat(st.nextToken());
        float y = Float.parseFloat(st.nextToken());
        float z = Float.parseFloat(st.nextToken());
        scalePanel.setScale(x, y, z);
        scalePanel.setIJCalibrationOption(Boolean.parseBoolean(props.getProperty(IMAGEJCALIBRATION_GUI)));
    }

    public void addRequestedProcessors() {

    }

    public void setPropertyFromProcessorOption(ProcessorOption pOpt, Integer c) throws Exception {
        String val = pOpt.getProcessorName();
        Options o = pOpt.getOptions();
        String n;
        if (val.equals("Invert image")) {
            n = INVERTER_GUI + c.toString();
            val = new String();
        } else if (val.equals("Mean Filter")) {
            n = MEAN_FILTER_GUI + c.toString();
            val = new String(o.getOptionValue("radius").toString() + " " + o.getOptionValue("meanFilterRepeat").toString());
        } else if (val.equals("Median Filter")) {
            n = MEDIAN_FILTER_GUI + c.toString();
            val = new String(o.getOptionValue("medianFilterRadius").toString() + " " + o.getOptionValue("medianFilterRepeat").toString());
        } else if (val.equals("Background extraction")) {
            n = BACKGROUND_GUI + c.toString();
            val = new String(o.getOptionValue("threshold").toString());
        } else if (val.equals("Peak remover")) {
            n = PEAK_REMOVER_GUI + c.toString();
            val = new String(o.getOptionValue("sizeThreshold").toString() + " " + o.getOptionValue("intensityThreshold").toString());
        } else if (val.equals("Peak merger")) {
            n = PEAK_MERGER_GUI + c.toString();
            val = new String(o.getOptionValue("radius").toString());
        } else {
            val = null;
            n = null;
            throw new Exception("Unidentified processor option: " + pOpt.getProcessorName() + "(" + c + ")");
        }
        props.setProperty(n, val);
    }

    public void setProcessorOption(ProcessorOption pOpt, Integer c) throws Exception {
        String val = pOpt.getProcessorName();
        Options o = new Options();
//        System.out.println(val);
        if (val.equals("Invert image")) {
            val = props.getProperty(INVERTER_GUI + c.toString());
//            System.out.println(val);
        } else if (val.equals("Mean Filter")) {
            val = props.getProperty(MEAN_FILTER_GUI + c.toString());
//            System.out.println(val);
            StringTokenizer st = new StringTokenizer(val);
            o.addOption("radius", Float.parseFloat(st.nextToken()));
            o.addOption("meanFilterRepeat", Integer.parseInt(st.nextToken()));
        } else if (val.equals("Median Filter")) {
            val = props.getProperty(MEDIAN_FILTER_GUI + c.toString());
//            System.out.println(val);
            StringTokenizer st = new StringTokenizer(val);
            o.addOption("medianFilterRadius", Float.parseFloat(st.nextToken()));
            o.addOption("medianFilterRepeat", Integer.parseInt(st.nextToken()));
        } else if (val.equals("Background extraction")) {
            val = props.getProperty(BACKGROUND_GUI + c.toString());
//            System.out.println(val);
            StringTokenizer st = new StringTokenizer(val);
            o.addOption("threshold", Float.parseFloat(st.nextToken()));
        } else if (val.equals("Peak remover")) {
            val = props.getProperty(PEAK_REMOVER_GUI + c.toString());
//            System.out.println(val);
            StringTokenizer st = new StringTokenizer(val);
            o.addOption("sizeThreshold", Float.parseFloat(st.nextToken()));
            o.addOption("intensityThreshold", Float.parseFloat(st.nextToken()));
        } else if (val.equals("Peak merger")) {
            val = props.getProperty(PEAK_MERGER_GUI + c.toString());
//            System.out.println(val);
            StringTokenizer st = new StringTokenizer(val);
            o.addOption("radius", Float.parseFloat(st.nextToken()));

        } else {
            throw new Exception("Unidentified processor option: " + pOpt.getProcessorName() + "(" + c + ")");
        }
        pOpt.setFromOptions(o);
    }
    
    private MainFrame frame;
    private java.util.Properties props;

    private class ConfigurationFileFilter extends FileFilter {

        public static final String ext = "ccfg";
        public static final String descr = "Costanza configuration files" + " *." + ext;

        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }

            String extension = getExtension(f);
            if ((extension != null) && extension.equals(ext)) {
                return true;
            }

            return false;
        }

        @Override
        public String getDescription() {
            return descr;
        }

        public String getExtension(File f) {
            String e = null;
            String s = f.getName();
            int i = s.lastIndexOf('.');

            if (i > 0 && i < s.length() - 1) {
                e = s.substring(i + 1).toLowerCase();
            }
            return e;
        }
    }
}
