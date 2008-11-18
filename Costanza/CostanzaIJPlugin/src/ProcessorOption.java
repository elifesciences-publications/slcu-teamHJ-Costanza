import costanza.Options;
import costanza.Queue;

public interface ProcessorOption {

	public java.awt.Panel getPanel();

	public String getProcessorName();

	public void addJobs(Queue jobs) throws Exception;
        
        public Options getOptions() throws Exception;
        
        public void setFromOptions(Options o) throws Exception;
}
