/**
 * Base class for a Processor.
 * @see Inverter
 */
public abstract class Processor {

	public Case myCase;
	public Processor myProcessor;
	public Options myOptions;

	/**
	 * Get the options used by this Processor.
	 * @return the Options object this Processor uses.
	 */
	public Options getOptions() {
		return myOptions;
	}

	/** 
	 * Abstract method for running the implemented process.
	 * @param case the Case the the Processor will work on.
	 * @return the processed Case.
	 */
	public abstract Case process(Case case){
		return case;
	}
}
