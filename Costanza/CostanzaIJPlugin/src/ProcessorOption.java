
import costanza.Queue;

public interface ProcessorOption {

	public java.awt.Panel getPanel();

	public String getProcessorName();

	public void addJobs(Queue jobs) throws Exception;
}
