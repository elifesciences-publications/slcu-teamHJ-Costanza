package costanza;

/** Processor that is doing absolutely nothing.
 * 
 * This processor is used in cases where you want to return null instead of a
 * Processor Class. Basically an ugly hack. Modifications to Queue and/or
 * Factory should be done to make this Class obselete.
*/
public class NullProcessor extends Processor {

	@Override
	public Case process(Case c, Options options) throws Exception {
		return c;
	}
}
