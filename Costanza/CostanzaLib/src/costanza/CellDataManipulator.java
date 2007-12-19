package costanza;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;
import java.io.StreamTokenizer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/** Class holds methods for manipulating cell data*/
public class CellDataManipulator extends Data {

    /** Creates a new instance of CellDataManipulator */
    public CellDataManipulator() {
    }

    /**Reads data of given DataId to the file
     *@param id DataId
     *@param fileName String
     *@param opt Options
     */
    public void readData(DataId id, String fileName, Options opt) throws Exception {
	Collection coll = getCellData(id);
	if (coll != null) {
	    clearData(id);
	}


	LineNumberReader lnr = new LineNumberReader(new FileReader(fileName));
	lnr.setLineNumber(1);
	StreamTokenizer stok = new StreamTokenizer(lnr);
	stok.parseNumbers();
	stok.eolIsSignificant(true);
	stok.nextToken();
	if (checkToken(stok, StreamTokenizer.TT_NUMBER)) {
	    return;
	}
	int nCells = (int) stok.nval;
	stok.nextToken();
	if (checkToken(stok, StreamTokenizer.TT_NUMBER)) {
	    return;
	}
	int nDim = (int) stok.nval;

	stok.nextToken();
	if (checkToken(stok, StreamTokenizer.TT_EOL)) {
	    return;
	}

	int counter = 0;
	for (int i = 0; i < nCells && stok.ttype != StreamTokenizer.TT_EOF; ++i) {
	    lnr.setLineNumber(i + 2);
	    stok.nextToken();
	    if (checkToken(stok, StreamTokenizer.TT_NUMBER)) {
		return;
	    }
	    int x = (int) stok.nval;
	    stok.nextToken();
	    if (checkToken(stok, StreamTokenizer.TT_NUMBER)) {
		return;
	    }
	    int y = (int) stok.nval;
	    stok.nextToken();
	    if (checkToken(stok, StreamTokenizer.TT_NUMBER)) {
		return;
	    }
	    int z = (int) stok.nval;

	    stok.nextToken();
	    if (checkToken(stok, StreamTokenizer.TT_EOL)) {
		return;
	    }

	    CellCenter cent = new CellCenter( x, y, z);
	    attachCellData( cent, i);
	}
    }

    private boolean checkToken(StreamTokenizer stok, int t) {
	if (stok.ttype != t) {
	    System.out.println("Not valid data format in the input file . No data read.");
	    return false;
	}
	return true;
    }

//    public CellData_t getObject(DataId dId, int cId) throws Exception {
//	Object o = null;
//	return getCell(cId).get(dId);
//    }

    /**Writes data of given DataId to the file
     *@param id DataId
     *@param fileName String
     *@param opt Options
     */
    public void writeData(DataId id, String fileName, Options opt) throws Exception {
	Collection coll = getCellData(id);
	if (coll == null) {
	    System.out.println("Data id: " + id + " not found in Data.");
	    return;
	}

	FileWriter writer = new FileWriter(fileName);

	Iterator iter = coll.iterator();
	//String delim = ":";
	String sp = " ";
	//writer.write("DataId" + delim + id.name() +"\n");
	String dim = "3";
	switch (id) {
	    case CENTERS: {
		writer.write(coll.size() + sp + dim + "\n");
		while (iter.hasNext()) {
		    CellCenter elem = (CellCenter) iter.next();
		    //writer.write(elem.getId());
		    //writer.write(delim + " ");
		    writer.write(elem.getX() + sp + elem.getY() + sp + elem.getZ() + "\n");

		}
		break;
	    }
	    case BOAS: {
		writer.write(coll.size() + sp + dim + "\n");
		while (iter.hasNext()) {
		    BOA elem = (BOA) iter.next();
		    writer.write(elem.getCellId() + "\n");
		    Iterator<Pixel> pixIter = elem.iterator();
		    while (pixIter.hasNext()) {
			Pixel pix = pixIter.next();
			writer.write(pix.getX() + sp + pix.getY() + pix + pix.getZ() + "\n");
		    }
		}
		break;
	    }
	}

    }

    /**Merges all data for given cells. Final data are put in the first cell. 
     * @param c1 Cell
     * @param c2 Cell
     * @throws java.lang.Exception
     */
    public void mergeAllData(Cell c1, Cell c2) throws Exception {
       
        Set<DataId> ids = getDataKeys( DataGroup.CELL );
	Iterator iter = ids.iterator();

	while (iter.hasNext()) {
	    DataId id = (DataId) iter.next();
            merge(id, c1, c2);
	}
    }
    
    /**Merges all data for given cells. Final data are put in the first cell. 
     * @param c1Id int
     * @param c2Id int
     * @throws java.lang.Exception
     */
    public void mergeAllData(int c1Id, int c2Id) throws Exception {

        Cell c1 = getCell( c1Id );
        Cell c2 = getCell( c2Id );
        
        mergeAllData( c1, c2 );
    }

    /**Merges data of given type for given cells. Final data are put in the first cell
     * @param id DataId
     * @param c1 Cell
     * @param c2 Cell
     * @return int merged cell id
     * @throws java.lang.Exception
     */
    private int merge(DataId id, Cell c1, Cell c2 ) throws Exception {

//        if(c1 == null || c2 == null){
//            System.out.println("one of the cells is null");
//            return -1;
//        }
            
	switch (id) {
	    case CENTERS: {
		CellCenter e1 = (CellCenter)c1.get(id), e2 = (CellCenter)c2.get(id);

                if (e1 == null || e2 == null) {
		    System.out.println("Cell data: " + id + " not found in cell " + c1.getCellId() + ", or cell " + c2.getCellId() );
		    return -1;
		}
                
		int x = (e2.getX() + e1.getX()) / 2;
		int y = (e2.getY() + e1.getY()) / 2;
		int z = (e2.getZ() + e1.getZ()) / 2;
		//CellCenter midC = new CellCenter(x, y, z);
                
		Pixel pix = (Pixel)c1.get(id);
                pix.setXYZ(x, y, z);
		break;
	    }
	    case INTENSITIES: {
		CellIntensity e1 = (CellIntensity)c1.get(id), e2 = (CellIntensity)c2.get(id);
                
                if (e1 == null || e2 == null) {
		    System.out.println("Cell data: " + id + " not found in cell " + c1.getCellId() + ", or cell " + c2.getCellId() );
		    return -1;
		}

		BOA b1 = (BOA)c1.get(DataId.BOAS), b2 = (BOA)c2.get(DataId.BOAS);
                if (b1 == null || b2 == null) {
		    throw new Exception("There is no BOA associated with this Intensity");
		}

		int mergedSize = b1.size() + b2.size();
		for (int i=0; i < e1.size(); ++i) {
		    float I = e1.getIntensity(i)*b1.size() + e2.getIntensity(i)*b2.size();
                    e1.setIntensity(i, I/mergedSize);
		}
		break;
	    }
	    case BOAS: {

                BOA e1 = (BOA)c1.get(id), e2 = (BOA)c2.get(id);

		if (e1 == null || e2 == null) {
		    System.out.println("Cell data: " + id + " not found in cell " + c1.getCellId() + ", or cell " + c2.getCellId() );
		    return -1;
		}

		e1.addPixels(e2);

		break;
	    }
	}
        removeCell(c2);
        return c1.getCellId();
    }

}

