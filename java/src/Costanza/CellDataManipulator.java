
package Costanza;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CellDataManipulator {
    
    private Data data;
    
    /** Creates a new instance of CellDataManipulator */
    public CellDataManipulator( Data d ) {
        this.data = d;
    }
    
    public void merge(DataId id, String c1Id, String c2Id){ 
        Set centers = new HashSet<Object>( data.getData(DataId.cellCenters) );
        if( centers == null ){
            System.out.println("Data id: " + id + " not found in Data.");
            return;
        }
        
        switch(id){
            case cellCenters:
                    
            
        }
        
    }
}
