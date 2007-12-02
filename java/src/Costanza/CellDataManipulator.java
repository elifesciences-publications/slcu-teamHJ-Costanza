
package Costanza;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

public class CellDataManipulator {
    
    private Data data;
    
    /** Creates a new instance of CellDataManipulator */
    public CellDataManipulator( Data d ) {
        this.data = d;
    }
    
    public void merge(DataId id, int c1Id, int c2Id, int newCellId){
        Collection cells = data.getData(id);
        if( cells == null ){
            System.out.println("Data id: " + id + " not found in Data.");
            return;
        }
        
        Iterator iter = cells.iterator();
        
        switch(id){
            case cellCenters:{
                
                CellCenter e1 = null, e2 = null;
                int breaker = 0;
                while(iter.hasNext() && breaker < 2){
                    CellCenter elem = (CellCenter)iter.next();
                    if(elem.getId() == c1Id){
                        e1 = elem;
                        ++breaker;
                    } else if(elem.getId() == c2Id){
                        e2 = elem;
                        ++breaker;
                    }
                }
                if( e1 == null || e2 == null ){
                    System.out.println("Cell id: " + e1.getId() + " or " + e2.getId() + " not found in the Data.");
                    return;
                }
                
                int x = (e2.getX() + e1.getX())/2;
                int y = (e2.getY() + e1.getY())/2;
                int z = (e2.getZ() + e1.getZ())/2;
                
                
                CellCenter midC = new CellCenter(newCellId, x, y, z);
                
                data.removeData(DataId.cellCenters, e1);
                data.removeData(DataId.cellCenters, e2);
                
                data.attachData(DataId.cellCenters, midC);
                
            }
            case cellBasinsOfAtraction:{
                BOA e1 = null, e2 = null;
                int breaker = 0;
                while(iter.hasNext() && breaker < 2){
                    BOA elem = (BOA)iter.next();
                    if(elem.getId() == c1Id){
                        e1 = elem;
                        ++breaker;
                    } else if(elem.getId() == c2Id){
                        e2 = elem;
                        ++breaker;
                    }
                }
                if( e1 == null || e2 == null ){
                    System.out.println("Cell id: " + e1.getId() + " or " + e2.getId() + " not found in the Data.");
                    return;
                }
                
                BOA mergedB = new BOA( newCellId, e1.getPixels() );
                mergedB.addPixels(e2.getPixels());
                
                data.removeData(DataId.cellBasinsOfAtraction, e1);
                data.removeData(DataId.cellBasinsOfAtraction, e2);
                
                data.attachData(DataId.cellBasinsOfAtraction, mergedB);
            }
        }
        
    }
}

