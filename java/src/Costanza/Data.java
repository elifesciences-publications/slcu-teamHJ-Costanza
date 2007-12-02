package Costanza;

import java.util.HashMap;
//import java.util.HashSet;
import java.lang.String;
import java.util.Collection;
import java.util.Map;
import java.util.Vector;

/**Data is a container for different types of data used by Processor.
 */
public class Data {
    /**Maps dataId to a Set of Objects which represent data*/
    private Map< DataId, Vector< Object > > mMap;
    //private int maxCellId;
    
    public Data(){
        mMap = new HashMap< DataId, Vector< Object > >();
        //maxCellId = 0;
    }
    
    //public int newCellId(){
      //  return ++maxCellId;
    //}
    /**Attaches data Object to a dataId
     *@param String id, Object o
     */
    public void attachData( DataId id, Object o ){
        if(mMap.containsKey(id)) {
            mMap.get(id).add(o);
        } else {
            Vector<Object> s = new Vector<Object>();
            s.add( o );
            mMap.put( id, s );
        }
    }
    
    /**Attaches all the data Objects contained in the Collection to the dataId
     *@param String id, Collection c
     */
    public void attachData( DataId id, Collection<Object> c ){
        if(mMap.containsKey(id)) {
            mMap.get(id).addAll(c);
        } else {
            mMap.put( id, new Vector<Object>(c) );
        }
    }
    
    /**Remove data object o from Data set with DataId id
     *@param DataId id, Object o
     */
    public void removeData( DataId id, Object o ){
        if(mMap.containsKey(id)) {
            mMap.get(id).remove(o);
        }
    }
    /**Retrives data Collection associated with dataId
     *@return Collection
     */
    public Collection getData( DataId id ){
        return mMap.get( id );
    }
}
