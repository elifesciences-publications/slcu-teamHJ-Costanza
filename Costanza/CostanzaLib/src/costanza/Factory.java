package costanza;
import java.util.HashMap;

/** A general Object factory, here used for Processor  objects. */
public class Factory <T>{
    /** Map holding id strings and their classes. */
    private HashMap<String, Class<? extends T>> objectClasses;
    
    /** Constructor for class Factory. */
    public Factory() {
        objectClasses = new HashMap<String, Class<? extends T>>();
    }
    
    /** Register class inherited from Processor with id string. */
    public void register(String processorId, Class<? extends T> processorClass) {
        objectClasses.put(processorId, processorClass);
    }
    
    /** Create object using processorId. */
    public T create(String processorId) throws Exception {
        if (!objectClasses.containsKey(processorId)) {
            throw new Exception("Class '" + processorId + "' not found in factory.");
        }
        Class<? extends T> objectClass;
        objectClass = objectClasses.get(processorId);
        T object = objectClass.newInstance();
        return object;
    }
}