package costanza;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Options {
    /** Container with options. */
    private final HashMap<String, Object> options;

    /** Empty constructor. */
    public Options() {
        options = new HashMap<String, Object>();
    }

    public boolean hasOption(String key){
        return options.containsKey(key);
    }
    /** Returns option value. */
    public Object getOptionValue(String key) throws Exception {
        if (!options.containsKey(key)) {
            throw new Exception("Option key (" +  key +") not available.");
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
    
    /** Get set of map enties for these options. */
    public Set<Map.Entry<String, Object>> entrySet()
    {
        return options.entrySet();
    }
    
    @Override
    public String toString(){
        return options.toString();
    }
}
