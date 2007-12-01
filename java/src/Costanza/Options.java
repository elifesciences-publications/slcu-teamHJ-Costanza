package Costanza;

import java.util.Map;

public class Options {
    /** Container with options. */
    private Map<String, Object> options;

    /** Empty constructor. */
    public Options() {
        // Do nothing.
    }

    /** Returns option value. */
    public Object getOptionValue(String key) {
        if (!options.containsKey(key)) {
            throw Exception("Option key not available.");
        }
        return options.get(key);
    }
    
    /** Add option. */
    public void addOption(String key, Object value) {
        if (!options.containsKey(key)) {
            options.put(key, value);
        } else {
            throw Exception("Option key already exists.");
        }
    }
}
