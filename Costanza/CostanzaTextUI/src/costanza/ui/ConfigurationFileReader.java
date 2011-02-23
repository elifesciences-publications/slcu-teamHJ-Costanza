package costanza.ui;

import costanza.Job;
import costanza.Options;
import costanza.Queue;
import java.io.File;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigurationFileReader{

    public static final String WORKING_DIR_GUI = "workingDirectory";
    public static final String EXTENDED_NEIGHBORHOOD_GUI = "useExtendedNeighborhood";
    public static final String PLATAEU_MARKER_GUI = "usePlateauMarker";
    public static final String INTENSITY_LEVELS_GUI = "intensityLevelsNumber";
    public static final String MARKER_RADIUS_GUI = "markerRadius";
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

    private int request;

    public int getResultRequest(){
        return request;
    }

    public ConfigurationFileReader() {
        
//        File wrkDir = new File(WRK_DIR);
//        File file = new File(f);
        
        java.util.Properties defaults = new java.util.Properties();
//        defaults.setProperty(WORKING_DIR_GUI, wrkDir.getPath());
        defaults.setProperty(EXTENDED_NEIGHBORHOOD_GUI, "false");
        defaults.setProperty(PLATAEU_MARKER_GUI, "false");
        defaults.setProperty(INTENSITY_LEVELS_GUI, "256");
        defaults.setProperty(MARKER_RADIUS_GUI, "2");
        defaults.setProperty(SECONDARY_STACK_GUI, "false");
        defaults.setProperty(CELL_CENTERS_GUI, "true");
        defaults.setProperty(BOAS_GUI, "false");
        defaults.setProperty(INTENSITY_BOAS_GUI, "false");
        defaults.setProperty(WORKING_STACK_GUI, "false");

        defaults.setProperty(IMAGEJCALIBRATION_GUI, "true");
        defaults.setProperty(SCALES_GUI, "1 1 1");

        props = new java.util.Properties(defaults);

        props.setProperty(BACKGROUND_GUI, "50");
        props.setProperty(MEAN_FILTER_GUI, "2 2");

        props.setProperty(PEAK_REMOVER_GUI, "10 10");
        props.setProperty(PEAK_MERGER_GUI, "10");

//        if(file.exists()){
//            System.out.println("Loading configuration");
//        loadProperties(file);
//        }
//        else{
//            System.out.println("No configuration file");
//        }
    }

    public String getProperty(String p) {
        return props.getProperty(p);
    }

    public Queue loadProperties(File f) {
        Queue q = new Queue();
        try {
            props.clear();
            FileInputStream input = new FileInputStream(f);
            props.load(input);
            input.close();
//            props.list(System.out);
//            setCurrentDirectory(new File(props.getProperty(WORKING_DIR_GUI)));

            Vector<String> vec = getOrderedProcessors();
            addPreprocessingJobs(q,vec);
            
            Options o = new Options();
            o.addOption("useExtendedNeighborhood", Boolean.parseBoolean(props.getProperty(EXTENDED_NEIGHBORHOOD_GUI)));
            o.addOption("usePlateau", Boolean.parseBoolean(props.getProperty(PLATAEU_MARKER_GUI)));
            o.addOption("intensityLevelsNumber", Integer.parseInt(props.getProperty(INTENSITY_LEVELS_GUI)));
            q.addJob(new Job("gradientdescent", o));
            addPostprocessingJobs(q,vec);
            q.addJob(new Job("intensityfinder"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return q;
    }

    public Vector<String> getOrderedProcessors() {
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
                vec.set(i, procStr);
//                if (procStr.equals(INVERTER_GUI)) {
//                    vec.set(i, InvertOption.NAME);
//                } else if (procStr.equals(MEAN_FILTER_GUI)) {
//                    vec.set(i, MeanFilterOption.NAME);
//                } else if (procStr.equals(MEDIAN_FILTER_GUI)) {
//                    vec.set(i, MedianFilterOption.NAME);
//                } else if (procStr.equals(BACKGROUND_GUI)) {
//                    vec.set(i, BackGroundFinderIntensityOption.NAME);
//                } else if (procStr.equals(PEAK_REMOVER_GUI)) {
//                    vec.set(i, PeakRemoverOption.NAME);
//                } else if (procStr.equals(PEAK_MERGER_GUI)) {
//                    vec.set(i, PeakMergerOption.NAME);
//                } else {
//                    throw new Exception("Unidentified processor option: " + procStr + "(" + i + ")");
//                }
            }
        }
        while (vec.lastElement() == null) {
            vec.setSize(vec.size() - 1);
        }
        return vec;
//        System.out.println(vec);
//        ProcessorOptionPanel pre = frame.getPreProcessorPanel();
//        ProcessorOptionPanel post = frame.getPostProcessorPanel();
//        
//        Iterator<String> iter = vec.iterator();
//        while(iter.hasNext()){
//            String n = iter.next();
//            if(pre.hasOptionInMenu(n)){
//                pre.addOptionPanel(n);
//            }
//            else if(post.hasOptionInMenu(n)){
//                post.addOptionPanel(n);
//            }
//        }
    }

    public void addPreprocessingJobs(Queue q, Vector<String> vec) throws Exception {
        for (Integer c = 0; c < vec.size(); ++c) {
            String val = vec.get(c);
            Options o = new Options();
//        System.out.println(val);
            if (val.equals(INVERTER_GUI)) {
                q.addJob(new Job("invert"));
                val = props.getProperty(INVERTER_GUI + c.toString());
//            System.out.println(val);
            } else if (val.equals(MEAN_FILTER_GUI)) {
                val = props.getProperty(MEAN_FILTER_GUI + c.toString());
//            System.out.println(val);
                StringTokenizer st = new StringTokenizer(val);
                o.addOption("radius", Float.parseFloat(st.nextToken()));
                o.addOption("meanFilterRepeat", Integer.parseInt(st.nextToken()));
                q.addJob(new Job("meanfilter", o));
            } else if (val.equals(MEDIAN_FILTER_GUI)) {
                val = props.getProperty(MEDIAN_FILTER_GUI + c.toString());
//            System.out.println(val);
                StringTokenizer st = new StringTokenizer(val);
                o.addOption("medianFilterRadius", Float.parseFloat(st.nextToken()));
                o.addOption("medianFilterRepeat", Integer.parseInt(st.nextToken()));
                q.addJob(new Job("medianfilter", o));
            } else if (val.equals(BACKGROUND_GUI)) {
                val = props.getProperty(BACKGROUND_GUI + c.toString());
//            System.out.println(val);
                StringTokenizer st = new StringTokenizer(val);
                o.addOption("threshold", Float.parseFloat(st.nextToken()));
                q.addJob(new Job("backgroundextractor", o));
            } 
        }
    }
    
    public void addPostprocessingJobs(Queue q, Vector<String> vec) throws Exception {
        for (Integer c = 0; c < vec.size(); ++c) {
            String val = vec.get(c);
            Options o = new Options();
//        System.out.println(val);
            if (val.equals(PEAK_REMOVER_GUI)) {
                val = props.getProperty(PEAK_REMOVER_GUI + c.toString());
//            System.out.println(val);
                StringTokenizer st = new StringTokenizer(val);
                o.addOption("sizeThreshold", Float.parseFloat(st.nextToken()));
                o.addOption("intensityThreshold", Float.parseFloat(st.nextToken()));
                q.addJob(new Job("peakremover", o));
            } else if (val.equals(PEAK_MERGER_GUI)) {
                val = props.getProperty(PEAK_MERGER_GUI + c.toString());
//            System.out.println(val);
                StringTokenizer st = new StringTokenizer(val);
                o.addOption("radius", Float.parseFloat(st.nextToken()));
                q.addJob(new Job("peakmerger", o));
            } 
        }
    }
    
public List<Job> getOutputJobs() throws Exception {
    
            List<Job> jobs = new LinkedList<Job>();
            if (Boolean.parseBoolean(props.getProperty(CELL_CENTERS_GUI))){
                Options options = new Options();
                options.addOption("markNeighbors", Integer.parseInt(props.getProperty(MARKER_RADIUS_GUI)));
                jobs.add(new Job("cellcentermarker", options));
            }
            if (Boolean.parseBoolean(props.getProperty(BOAS_GUI))){
                jobs.add(new Job("boacolorize"));
            }
            if (Boolean.parseBoolean(props.getProperty(INTENSITY_BOAS_GUI))){
                jobs.add(new Job("boacolorizeintensity"));
            }
            return jobs;
}

    private java.util.Properties props;
}
