package costanza;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;
import java.io.StreamTokenizer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.lang.Exception;

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

	    CellCenter cent = new CellCenter(i, x, y, z);
	    attachData(DataId.cellCenters, cent);
	}
    }

    private boolean checkToken(StreamTokenizer stok, int t) {
	if (stok.ttype != t) {
	    System.out.println("Not valid data format in the input file . No data read.");
	    return false;
	}
	return true;
    }

    public Object getObject(DataId dId, int cId) throws Exception {
	Object o = null;
	Collection<?> v = getCellData(dId);
	Iterator it = v.iterator();
	//System.out.println("getObject " + dId.toString() + " " + cId);
	switch (dId) {
	    case cellCenters: {
		while (it.hasNext()) {

		    CellCenter cc = (CellCenter) it.next();
		    //System.out.println(cc.getId() + " " + cId);
		    if (cc.getId() == cId) {
			o = cc;
			break;
		    }
		}
		break;
	    }
	    case cellBasinsOfAttraction: {
		while (it.hasNext()) {
		    BOA boa = (BOA) it.next();
		    if (boa.getId() == cId) {
			o = boa;
			break;
		    }
		}
		break;
	    }
	    case cellIntensity: {
		while (it.hasNext()) {
		    CellIntensity ci = (CellIntensity) it.next();
		    if (ci.getId() == cId) {
			o = ci;
			break;
		    }
		}
		break;
	    }
	}
	if (o == null) {
	    throw new Exception("CellId " + cId + " not found");
	}

	return o;
    }

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
	    case cellCenters: {
		writer.write(coll.size() + sp + dim + "\n");
		while (iter.hasNext()) {
		    CellCenter elem = (CellCenter) iter.next();
		    //writer.write(elem.getId());
		    //writer.write(delim + " ");
		    writer.write(elem.getX() + sp + elem.getY() + sp + elem.getZ() + "\n");

		}
		break;
	    }
	    case cellBasinsOfAttraction: {
		writer.write(coll.size() + sp + dim + "\n");
		while (iter.hasNext()) {
		    BOA elem = (BOA) iter.next();
		    writer.write(elem.getId() + "\n");
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

    /**Merges all
     *@param c1Id int
     *@param c2Id int
     *@param newCellId int
     */
    public void mergeAllData(int c1Id, int c2Id, int newCellId) throws Exception {

	Set<DataId> ids = getDataKeys();
	Iterator iter = ids.iterator();

	while (iter.hasNext()) {
	    DataId id = (DataId) iter.next();
	    if (id.name().startsWith("cell")) {
		merge(id, c1Id, c2Id, newCellId);
	    }
	}
    }

    /**Merges data of given type for cells identified with cell ids and assigns new cell id to merged cell.
     *@param id DataId id
     *@param c1Id int
     *@param c2Id int
     *@param newCellId int
     */
    public void merge(DataId id, int c1Id, int c2Id, int newCellId) throws Exception {
	Collection cells = getCellData(id);
	if (cells == null) {
	    System.out.println("Data id: " + id + " not found in Data.");
	    return;
	}
	Iterator iter = cells.iterator();
	switch (id) {
	    case cellCenters: {
		CellCenter e1 = null, e2 = null;
		int breaker = 0;

		while (iter.hasNext() && breaker < 2) {
		    CellCenter elem = (CellCenter) iter.next();
		    if (elem.getId() == c1Id) {
			e1 = elem;
			++breaker;
		    } else if (elem.getId() == c2Id) {
			e2 = elem;
			++breaker;
		    }
		}
		if (e1 == null || e2 == null) {
		    System.out.println("Cell id: " + e1.getId() + " or " + e2.getId() + " not found in the Data.");
		    return;
		}
		int x = (e2.getX() + e1.getX()) / 2;
		int y = (e2.getY() + e1.getY()) / 2;
		int z = (e2.getZ() + e1.getZ()) / 2;
		CellCenter midC = new CellCenter(newCellId, x, y, z);
		removeData(DataId.cellCenters, e1);
		removeData(DataId.cellCenters, e2);
		attachData(DataId.cellCenters, midC);
		break;
	    }
	    case cellIntensity: {
		CellIntensity e1 = null, e2 = null;
		int breaker = 0;
		while (iter.hasNext() && breaker < 2) {
		    CellIntensity elem = (CellIntensity) iter.next();
		    if (elem.getId() == c1Id) {
			e1 = elem;
			++breaker;
		    } else if (elem.getId() == c2Id) {
			e2 = elem;
			++breaker;
		    }
		}
		if (e1 == null || e2 == null) {
		    System.out.println("Cell id: " + e1.getId() + " or " + e2.getId() + " not found in the Data.");
		    return;
		}
		CellIntensity mergedI = new CellIntensity(newCellId);
		Object[] boa = getCellData(DataId.cellBasinsOfAttraction).toArray();
		if (boa.length == 0)
		    throw new Exception("There is now boa associated with this Intensity");
		BOA b1=null, b2=null;
		for (int i = 0; i < boa.length; ++i) {
		   BOA b = (BOA) boa[i];
		   if (b.getId()==e1.getId())
		       b1=b;
		   if (b.getId()==e2.getId())
		       b2=b;
		}
		int mergedSize = b1.size() + b2.size();
		for (int i=0; i < e1.size(); ++i) {
		    float I = e1.getIntensity(i)*b1.size() + e2.getIntensity(i)*b2.size();
		    mergedI.addIntensity(I/mergedSize);
		}
		removeData(DataId.cellIntensity, e1);
		removeData(DataId.cellIntensity, e2);

		attachData(DataId.cellIntensity, mergedI);
		break;
	    }
	    case cellBasinsOfAttraction: {

		BOA e1 = null, e2 = null;
		int breaker = 0;
		while (iter.hasNext() && breaker < 2) {
		    BOA elem = (BOA) iter.next();
		    if (elem.getId() == c1Id) {
			e1 = elem;
			++breaker;
		    } else if (elem.getId() == c2Id) {
			e2 = elem;
			++breaker;
		    }
		}
		if (e1 == null || e2 == null) {
		    System.out.println("Cell id: " + e1.getId() + " or " + e2.getId() + " not found in the Data.");
		    return;
		}

		BOA mergedB = new BOA(newCellId, e1);
		mergedB.addPixels(e2);

		removeData(DataId.cellBasinsOfAttraction, e1);
		removeData(DataId.cellBasinsOfAttraction, e2);

		attachData(DataId.cellBasinsOfAttraction, mergedB);
		break;
	    }
	}
    }

    public void removeAll(int cId) throws Exception {
	Set<DataId> ids = getDataKeys();
	Iterator iter = ids.iterator();
	while (iter.hasNext()) {
	    DataId id = (DataId) iter.next();
	    if (id.name().startsWith("cell")) {
		remove(id, cId);
	    }
	}
    }

    private void remove(DataId dId, int cId) throws Exception {
	if (dId.name().startsWith("cell")) {
	    //System.out.println("remove " + dId.name() + " with ID " + cId );
	    removeData(dId, getObject(dId, cId));
	}
    }
}

