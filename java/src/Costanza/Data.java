package Costanza;

import java.util.HashMap;
import java.util.HashSet;
import java.lang.String;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**Data is a container for different types of data used by Processor.
 */
public class Data {
    /**Maps dataId to a Set of Objects which represent data*/
    private Map< String, Set< Object > > mMap;
    
    public Data(){
        mMap = new HashMap< String, Set< Object > >();
    }
    
    /**Attaches data Object to a dataId
     *@param String id, Object o
     */
    public void attachData( String id, Object o ){
        if(mMap.containsKey(id)) {
            mMap.get(id).add(o);
        } else {
            Set<Object> s = new HashSet<Object>();
            s.add( o );
            mMap.put( id, s );
        }
    }
    
    /**Attaches all the data Objects contained in the Collection to the dataId
     *@param String id, Collection c
     */
    public void attachData( String id, Collection<Object> c ){
        if(mMap.containsKey(id)) {
            mMap.get(id).addAll(c);
        } else {
            mMap.put( id, new HashSet<Object>(c) );
        }
    }
    
    /**Retrives data Collection associated with dataId
     *@return Collection
     */
    public Collection getData( String id ){
        return mMap.get( id );
    }
}
