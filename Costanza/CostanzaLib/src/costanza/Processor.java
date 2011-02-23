package costanza;

/**
 * Base class for a Processor.
 * @see Inverter
 */
public abstract class Processor {

    /** The Case to work on. */
//    public Case myCase;
    
    /** The Processor to use. */
//    public Processor myProcessor;

    /** 
     * Abstract method for running the implemented process.
     * @param c the Case the the Processor will work on.
     * @param options Options for the processor
     * @return the processed Case.
     */
    public abstract Case process(Case c, Options options) throws Exception;
}
