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
    Object getOptionValue(String key) {
        if (!options.containsKey(key)) {
            java.lang.System.exit(1);
        }
        return options.get(key);
    }
    
    /** Add option. */
    void addOption(String key, Object value) {
        if (!options.containsKey(key)) {
            options.put(key, value);
        } else {
            java.lang.System.exit(1);
        }
    }
}
