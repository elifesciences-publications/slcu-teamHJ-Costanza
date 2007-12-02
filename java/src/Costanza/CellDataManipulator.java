
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
                
                CellCenter c1 = null, c2 = null;
                int breaker = 0;
                while(iter.hasNext() || breaker < 2){
                    CellCenter center = (CellCenter)iter.next();
                    if(center.getId() == c1Id){
                        c1 = center;
                        ++breaker;
                    } else if(center.getId() == c2Id){
                        c2 = center;
                        ++breaker;
                    }
                }
                if( c1 == null || c2 == null ){
                    System.out.println("Cell id: " + c1.getId() + " or " + c2.getId() + " not found in cellCenters Data.");
                    return;
                }
                
                int x = (c2.getX() + c1.getX())/2;
                int y = (c2.getY() + c1.getY())/2;
                int z = (c2.getZ() + c1.getZ())/2;
                
                
                CellCenter midC = new CellCenter(data.newCellId(), x, y, z);
                
                data.removeData(DataId.cellCenters, c1);
                data.removeData(DataId.cellCenters, c2);
                
                data.attachData(DataId.cellCenters, midC);
                
            }
            case cellBasinsOfAtraction:{
                BOA b1 = null, b2 = null;
                int breaker = 0;
                while(iter.hasNext() || breaker < 2){
                    BOA basin = (BOA)iter.next();
                    if(basin.getId() == c1Id){
                        b1 = basin;
                        ++breaker;
                    } else if(basin.getId() == c2Id){
                        b2 = basin;
                        ++breaker;
                    }
                }
                if( b1 == null || b2 == null ){
                    System.out.println("Cell id: " + b1.getId() + " or " + b2.getId() + " not found in BOA Data.");
                    return;
                }
                
                BOA mergedB = new BOA( data.newCellId(), b1.getPixels() );
                mergedB.addPixels(b2.getPixels());
                
                data.removeData(DataId.cellBasinsOfAtraction, b1);
                data.removeData(DataId.cellBasinsOfAtraction, b2);
            }
        }
        
    }
}

