package costanza;

/** Processor that is doing absolutely nothing.
 * This processor is used in cases where you want to return null instead of a
 * Processor Class. Basically an ugly hack. Modifications to Queue and/or
 * Factory should be done to make this Class obselete.
 * @see Processor
 */
public class NullProcessor extends Processor {

    /**Does absolutely nothing.
     * @param c the Case to do nothing on.
     * @param options the Options to not use. Safely set to null.
     * @return the untouched Case.
     * @throws java.lang.Exception
     */
    @Override
    public Case process(Case c, Options options) throws Exception {
	return c;
    }
}
