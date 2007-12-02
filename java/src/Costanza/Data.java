package Costanza;

import java.util.HashMap;
//import java.util.HashSet;
import java.lang.String;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Iterator;
import java.lang.Exception;


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
    
    /**Gets Set of keys of available data types
     *
     */
    public Set<DataId> getDataKeys(){
        return mMap.keySet();
    }
    
    public Object getObject(DataId dId,int cId) throws Exception {
        Object o = null;
        Vector v = mMap.get(dId);
        Iterator it = v.iterator();
        switch(dId) {
            case cellCenters:{
                while (it.hasNext()) {
                    CellCenter cc = (CellCenter) it.next();
                    //System.out.println(cc.getId() + " " + cId);
                    if (cc.getId() == cId) {
                        o = cc;
                        break;
                    }
                }
            }
            case cellBasinsOfAttraction: {
                while (it.hasNext()) {
                    BOA boa = (BOA) it.next();
                    if (boa.getId() == cId) {
                        o = boa;
                        break;
                    }
                }
            }
        }
        if (o==null) 
            throw new Exception("CellId " + cId + " not found" );
            
        return o;
    }
    
    /**Gives the size of the data of given type
     *@param DataId id
     */
    public int sizeOfData(DataId id){
        if(mMap.containsKey(id)) {
            return mMap.get(id).size();
        } else {
            return 0;
        }
    }
    
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
    public void attachDataCollection( DataId id, Collection<?> c ){
        if(mMap.containsKey(id)) {
            mMap.get(id).addAll(c);
            //System.out.println("id present, Collection size: " + c.size());
        } else {
            //System.out.println("no id, Collection size: " + c.size());
            mMap.put( id, new Vector<Object>(c) );
        }
    }
    
    /**Removes data object o from Data set with id DataId
     *@param DataId id, Object o
     */
    public void removeData( DataId id, Object o ){
        if(mMap.containsKey(id)) {
            mMap.get(id).remove(o);
        } 
    }
    
    /**Removes all data from Data set with id DataId
     *@param DataId id
     */
    public void clearData( DataId id ){
        if(mMap.containsKey(id)) {
            mMap.get(id).clear();
        }
    }
    /**Retrives data Collection associated with dataId
     *@return Collection
     */
    public Collection getData( DataId id ){
        return mMap.get( id );
    }
}
