package costanza;

import java.io.File;
//import java.io.FileOutputStream;
//import java.awt.image.RenderedImage;
//import java.awt.image.renderable.ParameterBlock;
//import com.sun.image.codec.jpeg.JPEGCodec;
//import com.sun.image.codec.jpeg.JPEGImageEncoder;
import javax.media.jai.NullOpImage;
import javax.media.jai.OpImage;
//import javax.media.jai.JAI;
//import javax.media.jai.RenderedOp;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam;
import com.sun.media.jai.codec.ImageDecoder;
//import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.ImageCodec;

import java.awt.image.BufferedImage;
//import java.awt.image.ColorModel;
import java.awt.image.DataBufferUShort;
//import java.awt.image.WritableRaster;
import java.util.ArrayList;
//import java.util.Hashtable;
//import javax.imageio.ImageIO;
//import javax.media.jai.ImageLayout;
import javax.media.jai.PlanarImage;

public class BOAReader {

    private final Case cs;

    public BOAReader(Case c) {
        cs = c;
    }

    public void readTif(File f) throws Exception {
//        System.out.println("reading boas: BOAReader");
        try {
            SeekableStream s = new FileSeekableStream(f);
            TIFFDecodeParam param = null;
            ImageDecoder decoder = ImageCodec.createImageDecoder("tiff", s, param);
            BufferedImage[] bufferedImages = new BufferedImage[decoder.getNumPages()];
            int zDim = decoder.getNumPages();
//            System.out.println(decoder.getNumPages() + " images");
            for (int i = 0; i < zDim; i++) {
                PlanarImage planar_img = new NullOpImage(decoder.decodeAsRenderedImage(i), null, null, OpImage.OP_IO_BOUND);
                bufferedImages[i] = planar_img.getAsBufferedImage();
                if(bufferedImages[i].getType() != BufferedImage.TYPE_USHORT_GRAY)
                    throw new Exception("Can read BOAs only from image type: TYPE_USHORT_GRAY.");
            }
//            System.out.println(bufferedImages[0].toString());
            int xDim = bufferedImages[0].getWidth();
            int yDim = bufferedImages[0].getHeight();

            //clear all data
            cs.clearData(DataId.CENTERS);
            cs.clearData(DataId.INTENSITIES);
            cs.clearData(DataId.NEIGHBORS);
            cs.clearData(DataId.PIXEL_FLAG);
            ArrayList<Integer> cellSizes = new ArrayList<Integer>();
//            System.out.println("xDim = " + xDim + " yDim = " + yDim + " zDim = " + zDim );
            PixelFlag pf = (PixelFlag) cs.getStackData(DataId.PIXEL_FLAG);
            if (pf == null) {
                pf = new PixelFlag(xDim, yDim, zDim, PixelFlag.BACKGROUND_FLAG);
            } else {
                pf.init(xDim, yDim, zDim, PixelFlag.BACKGROUND_FLAG);
            }
            for (int iz = 0; iz < zDim; ++iz) {
                BufferedImage image = bufferedImages[iz];
                DataBufferUShort buffer = (DataBufferUShort) image.getRaster().getDataBuffer();
                short[] arrayUShort = buffer.getData();
        
                for (int iy = 0; iy < yDim; ++iy) {
                    for (int ix = 0; ix < xDim; ++ix) {

                        int index = arrayUShort[ix + iy * xDim] & 0xffff;
                        if (index != 1) {
                            if (index < 0) {
                                throw new Exception("Negative flag in PixelFlag (" + index + ") at pixel("+ ix +", "+ iy +", "+ iz +")");
                            }
                            pf.setFlag(ix, iy, iz, index);
//                            if(cellSizes.isEmpty() || cellSizes.get(index)==null)
//                            {
//                                cellSizes.ensureCapacity(index);
//                                cellSizes.add(index, 1);
//                            }
//                            else
//                                cellSizes.set(index, cellSizes.get(index)+1);
                        }
                    }
                }
            }
            cs.setResultImages(bufferedImages);
//            for(int i = 0; i < cellSizes.size(); i++){
////                cs.attachCellData(param, cellSize);
//                System.out.println("Cell["+ i + "] size = " + cellSizes.get(i));
//            }
            System.out.println("Boas from tif read.");
        } catch (java.io.IOException ioe) {
            System.out.println(ioe);
        }
    }
}
