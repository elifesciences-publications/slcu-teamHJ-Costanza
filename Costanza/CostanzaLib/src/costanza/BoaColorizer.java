package costanza;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

/**Implementation of a Processor that colors each BOA with a unique color.
 * @author michael
 * @see Processor
 */
public class BoaColorizer extends Processor {

    @Override
    public Case process(Case c, Options options) throws Exception {
        // Get the basin of attractors from data in case
        Collection boaCollection = c.getCellData(DataId.cellBasinsOfAttraction);
        if (boaCollection == null) { return c; }
        Iterator i = boaCollection.iterator();
        while (i.hasNext()) {
            BOA boa = (BOA) i.next();
        }
        System.out.println("Number of collected BOAs: " + boaCollection.size());

        return c;
    }
}
