package costanza;

import java.lang.Exception;
import java.util.HashMap;

public class Options {
    /** Container with options. */
    private HashMap<String, Object> options;

    /** Empty constructor. */
    public Options() {
        options = new HashMap<String, Object>();
    }

    /** Returns option value. */
    public Object getOptionValue(String key) throws Exception {
        if (!options.containsKey(key)) {
            throw new Exception("Option key not available.");
        }
        return options.get(key);
    }
    
    /** Add option. */
    public void addOption(String key, Object value) throws Exception {
        if (!options.containsKey(key)) {
            options.put(key, value);
        } else {
            throw new Exception("Option key already exists.");
        }
    }
}
