package costanza;

import java.io.DataOutputStream;
import java.util.Vector;
import java.util.List;
import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedList;

public class BOAWriter {

    private Case cs;
    
    public BOAWriter(Case c){
        cs = c;
    }
    
    public void writeText(File f) throws Exception {

        System.out.println("saving boas: BOAWriter");
        int size = cs.sizeOfCells();
        Vector< List<Pixel> > boas = new Vector< List<Pixel> >(size);
        for(int i = 0; i < size; ++i){
            boas.add(new LinkedList<Pixel>());
        }
        
        int xDim = cs.getStack().getWidth();
        int yDim = cs.getStack().getHeight();
        int zDim = cs.getStack().getDepth();
        System.out.println("xDim = " + xDim + " yDim = " + yDim + " zDim = " + zDim );
        PixelFlag pf = (PixelFlag) cs.getStackData(DataId.PIXEL_FLAG);

        for (int iz = 0; iz < zDim; ++iz) {
            for (int iy = 0; iy < yDim; ++iy) {
                for (int ix = 0; ix < xDim; ++ix) {
                    int flag = pf.getFlag(ix, iy, iz);
                    if (flag != PixelFlag.BACKGROUND_FLAG) {
                        if (flag < 0) {
                            throw new Exception("Negative flag in PixelFlag.");
                        }
                        if (flag >= size) {
                            throw new Exception("Flag excedes size of boas.");
                        }
                        boas.get(flag).add(new Pixel(ix, iy, iz));
                    }
                }
            }
        }
        System.out.println("boas size = " + size);
        FileOutputStream fo = new FileOutputStream(f);
        DataOutputStream out = new DataOutputStream(fo);
        out.writeChars( size +" boas\n");
        for(int i = 0; i < size; ++i){
            List<Pixel> boa = boas.get(i);
            int boaSize = boa.size();
            out.writeChars( i +": " + boaSize + " pixels\n");
            for(int j = 0; j < boaSize; ++j){
                out.writeChars( "\t" + boa.get(j).toString() + "\n");   
            }
        }
        System.out.println("finished writing");
    }
}
