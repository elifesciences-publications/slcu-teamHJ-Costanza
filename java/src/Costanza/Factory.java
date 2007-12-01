package Costanza;
import java.util.HashMap;

public class Factory {
    /** Map holding Processor id strings and their classes. */
    private HashMap<String, Class<? extends Processor>> processorClasses;
    
    /** Constructor for class Factory. */
    public Factory() {
        processorClasses = new HashMap<String, Class<? extends Processor>>();
    }
    
    /** Register class inherited from Processor with id string. */
    public void register(String processorId, Class<? extends Processor> processorClass) {
        processorClasses.put(processorId, processorClass);
    }
    
    public Processor createProcessor(String processorId) throws Exception {
        if (!processorClasses.containsKey(processorId)) {
            throw new Exception("Class not found in factory.");
        }
        Class<? extends Processor> processorClass;
        processorClass = processorClasses.get(processorId);
        Processor processor = processorClass.newInstance();
        return processor;
    }
}