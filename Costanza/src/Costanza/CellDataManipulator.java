
package Costanza;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;
import java.io.StreamTokenizer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.lang.Exception;

public class CellDataManipulator {
    
    private Data data;
    
    /** Creates a new instance of CellDataManipulator */
    public CellDataManipulator( Data d ) {
        this.data = d;
    }
    
    /**Reads data of given DataId to the file
     *@param DataId id, String fileName, Options opt
     */
    public void readData(DataId id, String fileName, Options opt) throws Exception{
        Collection coll = data.getData(id);
        if( coll != null ){
            data.clearData(id);
        }
        
        
        LineNumberReader lnr = new LineNumberReader(new FileReader(fileName));
        lnr.setLineNumber(1);
        StreamTokenizer stok = new StreamTokenizer(lnr);
        stok.parseNumbers();
        stok.eolIsSignificant(true);
        stok.nextToken();
        if(checkToken(stok,StreamTokenizer.TT_NUMBER))
            return;
        int nCells = (int)stok.nval;
        stok.nextToken();
        if(checkToken(stok, StreamTokenizer.TT_NUMBER))
            return;
        int nDim = (int)stok.nval;
        
        stok.nextToken();
        if( checkToken(stok, StreamTokenizer.TT_EOL))
            return;
        
        int counter = 0;
        for(int i = 0; i < nCells && stok.ttype != StreamTokenizer.TT_EOF; ++i){
            lnr.setLineNumber(i+2);
            stok.nextToken();
            if(checkToken(stok, StreamTokenizer.TT_NUMBER))
                return;
            int x = (int)stok.nval;
            stok.nextToken();
            if(checkToken(stok, StreamTokenizer.TT_NUMBER))
                return;
            int y = (int)stok.nval;
            stok.nextToken();
            if(checkToken(stok, StreamTokenizer.TT_NUMBER))
                return;
            int z = (int)stok.nval;
            
            stok.nextToken();
            if( checkToken(stok, StreamTokenizer.TT_EOL))
                return;
            
            CellCenter cent = new CellCenter(i,x,y,z);
            data.attachData(DataId.cellCenters, cent);
        }
    }
    
    private boolean checkToken(StreamTokenizer stok, int t){
        if (stok.ttype != t){
            System.out.println("Not valid data format in the input file . No data read.");
            return false;
        }
        return true;
    }
    /**Writes data of given DataId to the file
     *@param DataId id, String fileName, Options opt
     */
    public void writeData(DataId id, String fileName, Options opt) throws Exception{
        Collection coll = data.getData(id);
        if( coll == null ){
            System.out.println("Data id: " + id + " not found in Data.");
            return;
        }
        
        FileWriter writer = new FileWriter(fileName);
        
        Iterator iter = coll.iterator();
        //String delim = ":";
        String sp = " ";
        //writer.write("DataId" + delim + id.name() +"\n");
        String dim = "3";
        switch(id){
            case cellCenters:{
                writer.write(coll.size() + sp + dim + "\n");
                while(iter.hasNext()){
                    CellCenter elem = (CellCenter)iter.next();
                    //writer.write(elem.getId());
                    //writer.write(delim + " ");
                    writer.write(elem.getX() + sp + elem.getY() + sp + elem.getZ() + "\n");
                    
                }
                break;
            }
            case cellBasinsOfAttraction:{
                writer.write(coll.size() + sp + dim + "\n");
                while(iter.hasNext()){
                    BOA elem = (BOA)iter.next();
                    writer.write(elem.getId() + "\n");
                    Iterator<Pixel> pixIter = elem.getPixels().iterator();
                    while(pixIter.hasNext()){
                        Pixel pix = pixIter.next();
                        writer.write(pix.getX() + sp + pix.getY() + pix + pix.getZ() + "\n");
                    }
                }
                break;
            }
        }
        
    }
    /**Merges all
     *@param int c1Id, int c2Id, int newCellId
     */
    public void mergeAllData(int c1Id, int c2Id, int newCellId){
        
        Set<DataId> ids = data.getDataKeys();
        Iterator iter = ids.iterator();
        
        while(iter.hasNext()){
            DataId id = (DataId)iter.next();
            if(id.name().startsWith("cell")){
                merge(id, c1Id, c2Id, newCellId);
            }
        }
    }
    
    /**Merges data of given type for cells identified with cell ids and assigns new cell id to merged cell.
     *@param DataId id, int c1Id, int c2Id, int newCellId
     */
    public void merge(DataId id, int c1Id, int c2Id, int newCellId){
        Collection cells = data.getData(id);
        if( cells == null ){
            System.out.println("Data id: " + id + " not found in Data.");
            return;
        }
        System.out.println("merge " + id.toString());
        Iterator iter = cells.iterator();
        
        switch(id){
            case cellCenters:{
                CellCenter e1 = null, e2 = null;
                int breaker = 0;
                while(iter.hasNext() && breaker < 2){
                    CellCenter elem = (CellCenter)iter.next();
                    
                    if(elem.getId() == c1Id){
                        //System.out.println("found id 1");
                        
                        e1 = elem;
                        ++breaker;
                    } else if(elem.getId() == c2Id){
                        //System.out.println("found id2");
                        
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
                
                break;
            }
            case cellBasinsOfAttraction:{
                
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
                
                data.removeData(DataId.cellBasinsOfAttraction, e1);
                data.removeData(DataId.cellBasinsOfAttraction, e2);
                
                data.attachData(DataId.cellBasinsOfAttraction, mergedB);
                break;
            }
        }
        
    }
    
    public void removeAll(int cId) throws Exception {
        Set<DataId> ids = data.getDataKeys();
        Iterator iter = ids.iterator();
        //System.out.println("removeAll");
                
        while(iter.hasNext()){
            DataId id = (DataId)iter.next();
            if(id.name().startsWith("cell")){
                //System.out.println("remove " + id.toString());
                remove(id, cId);
            }
        }
    }
    
    private void remove(DataId dId,int cId) throws Exception {
            if(dId.name().startsWith("cell")){
                //System.out.println(dId.name() + " " + dId.toString());
                data.removeData(dId, data.getObject(dId,cId));
            }
    }
   
}

