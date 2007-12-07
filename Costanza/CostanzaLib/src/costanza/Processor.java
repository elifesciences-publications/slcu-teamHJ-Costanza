package costanza;
/**
 * Base class for a Processor.
 * @see Inverter
 */
public abstract class Processor {

	public Case myCase;
	public Processor myProcessor;


	/** 
	 * Abstract method for running the implemented process.
	 * @param c the Case the the Processor will work on.
	 * @param options Options for the processor
	 * @return the processed Case.
	 */
	public abstract Case process(Case c, Options options) throws Exception;
}
