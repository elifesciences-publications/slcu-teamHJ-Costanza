package costanza;

public class Job {
    private String processorId;
    private Options options;

    /** Constructor for Job class. */
    public Job(String processorId) {
        this.processorId = processorId;
        this.options = new Options();
    }
    public Job(String processorId, Options options) {
        this.processorId = processorId;
        this.options = options;
    }
    
    public String getProcessorId() {
        return processorId;
    }

    public Options getOptions() {
        return options;
    }
}