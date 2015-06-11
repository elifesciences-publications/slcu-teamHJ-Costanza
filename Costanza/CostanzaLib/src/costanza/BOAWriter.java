package costanza;

//import com.sun.media.jai.codec.FileSeekableStream;
//import com.sun.media.jai.codec.ImageDecoder;
////import com.sun.media.imageio.plugins.tiff.TIFFDirectory;
//import com.sun.media.jai.codec.TIFFDirectory;
//import com.sun.media.jai.codec.TIFFField;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import javax.imageio.ImageIO;
//import javax.imageio.ImageReader;
//import javax.imageio.metadata.IIOMetadata;
//import javax.imageio.stream.FileImageInputStream;
//import javax.imageio.stream.ImageInputStream;

import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.TIFFEncodeParam;
import com.sun.media.jai.codec.TIFFField;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferUShort;
import java.awt.image.WritableRaster;
import java.io.BufferedWriter;
import java.util.Iterator;
//import java.util.Vector;
import java.util.List;
import java.io.File;

import java.io.FileOutputStream;
import java.io.FileWriter;

import java.io.OutputStream;
import java.util.ArrayList;
//import java.util.Arrays;
import java.util.LinkedList;


public class BOAWriter {

    private final Case cs;

    public BOAWriter(Case c) {
        cs = c;
    }

    public ArrayList< List<Pixel>> getBOAs() throws Exception {
        //        System.out.println("counting boas: BOAWriter");
        int size = cs.sizeOfCells();
        ArrayList< List<Pixel>> boas = new ArrayList< List<Pixel>>(size);
//        Vector< List<Pixel> > boas = new Vector< List<Pixel> >(size);
        for (int i = 0; i < size; ++i) {
            boas.add(new LinkedList<Pixel>());
        }

        PixelFlag pf = (PixelFlag) cs.getStackData(DataId.PIXEL_FLAG);
        if (pf == null) {
            throw new Exception("No PixelFlag to get BOAs from.");
        }
        int xDim = pf.getXSize();
        int yDim = pf.getYSize();
        int zDim = pf.getZSize();
//        System.out.println("xDim = " + xDim + " yDim = " + yDim + " zDim = " + zDim );

        for (int iz = 0; iz < zDim; ++iz) {
            for (int iy = 0; iy < yDim; ++iy) {
                for (int ix = 0; ix < xDim; ++ix) {
                    int flag = pf.getFlag(ix, iy, iz);
                    if (flag != PixelFlag.BACKGROUND_FLAG) {
                        if (flag < 0) {
                            throw new Exception("Negative flag in PixelFlag (" + flag + ").");
                        }
                        if (flag >= size) {
                            throw new Exception("Flag excedes size of boas (" + flag + ">=" + size + ").");
                        }
                        boas.get(flag).add(new Pixel(ix, iy, iz));
                    }
                }
            }
        }
        return boas;
    }

    public void writeText(File f) throws Exception {

        ArrayList< List<Pixel>> boas = getBOAs();
        int size = boas.size();
        System.out.println("boas size = " + size);

        FileWriter fstream = new FileWriter(f);
        BufferedWriter out = new BufferedWriter(fstream);
        out.write(boas.size() + " boas\n");

        for (int i = 0; i < size; ++i) {
            List<Pixel> boa = boas.get(i);
            int boaSize = boa.size();
            out.write(i + ": " + boaSize + " pixels\n");
            Iterator<Pixel> iterator = boa.iterator();
            while (iterator.hasNext()) {
                out.write("\t" + iterator.next().toString() + "\n");
            }
        }
        out.close();
        fstream.close();
//        System.out.println("finished writing");
    }

    public void writeTif(File f) throws Exception {
//        System.out.println("writeTif(File f)");
        ArrayList<BufferedImage> images = prepareBOAsImage();
//        System.out.println("creating multipage tiff "+ images.size());
//        System.out.println(images.get(0).toString());
        TIFFEncodeParam params = new TIFFEncodeParam();
         //prepare additional fields
        TIFFField[] fields = new TIFFField[4];
        // PhotometricInterpretation
//        TIFFField fieldPhotoInter = new TIFFField(262, TIFFField.TIFF_SHORT, 1, (Object) new char[]{0});
//        fields[0] = fieldPhotoInter;
        // ResolutionUnit
        TIFFField fieldResUnit = new TIFFField(296, TIFFField.TIFF_SHORT, 1, (Object) new char[]{1});
        fields[0] = fieldResUnit;
        float[] scale = cs.getStack().getScale();
        // XResolution
        TIFFField fieldXRes = new TIFFField(282, TIFFField.TIFF_FLOAT, 1, (Object) new float[]{scale[0]});
        fields[1] = fieldXRes;
        // YResolution
        TIFFField fieldYRes = new TIFFField(283, TIFFField.TIFF_FLOAT, 1, (Object) new float[]{scale[1]});
        fields[2] = fieldYRes;
        TIFFField descriptor = new TIFFField(270, TIFFField.TIFF_ASCII, 1, (Object) new String[]{"simages="+images.size()+"slices"+images.size()+"unit="+cs.getStack().getUnit()});
        fields[3] = descriptor;
        // BitsPerSample
//        TIFFField fieldBitSample = new TIFFField(258, TIFFField.TIFF_SHORT, 1, (Object) new char[]{1});
//        fields[4] = fieldBitSample;
        // FillOrder
//        TIFFField fieldFillOrder = new TIFFField(266, TIFFField.TIFF_SHORT, 1, (Object) new char[]{1});
//        fields[5] = fieldFillOrder;
        // RowsPerStrip
//        TIFFField fieldRowsStrip = new TIFFField(278, TIFFField.TIFF_LONG, 1, (Object) new long[]{2200});
//        fields[6] = fieldRowsStrip;

        params.setExtraFields(fields);
//        System.out.println("preparing encoder");
        OutputStream out = new FileOutputStream(f);
        ImageEncoder encoder = ImageCodec.createImageEncoder("tiff", out, params);
        Iterator it = images.iterator();
        it.next(); //start additional images from second one
        params.setExtraImages(it);
        encoder.encode(images.get(0));
        out.close();
        System.out.println("Boas written to tif.");
    }

    public ArrayList<BufferedImage> prepareBOAsImage() throws Exception {
//        System.out.println("prepareBOAsImage()");
        ArrayList< List<Pixel>> boas = getBOAs();
        PixelFlag pf = (PixelFlag) cs.getStackData(DataId.PIXEL_FLAG);
        int xDim = pf.getXSize();
        int yDim = pf.getYSize();
        int zDim = pf.getZSize();
        if (pf == null) {
            throw new Exception("No PixelFlag to get BOAs from.");
        }
        ArrayList<BufferedImage> images = new ArrayList<BufferedImage>(zDim);
        //create template to fill return stack with
        BufferedImage template = new BufferedImage(xDim, yDim, BufferedImage.TYPE_USHORT_GRAY);
        //set background pixels to 1 (ALT-Mars format)
        DataBufferUShort buffer = (DataBufferUShort) template.getRaster().getDataBuffer();
        short[] arrayUShort = buffer.getData();
        for (int i = 0; i < arrayUShort.length; i++) {
            arrayUShort[i] = 1;
        }
        //create a stack with template clones
        ColorModel cm = template.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        for (int i = 0; i < zDim; i++) {
            WritableRaster raster = template.copyData(null);
            images.add(new BufferedImage(cm, raster, isAlphaPremultiplied, null));
        }
//        System.out.println("images size = " + images.size() + "\n");
//        System.out.println(images.get(0).toString());
        //set boa pixels to boa index value
        int max = Short.MAX_VALUE - Short.MIN_VALUE;
        if (boas.size() > max) {
            System.out.println("Warning! Size of boas larger than  maximal ushort. Grayscale image data might be corrupted");
        }
        for (int i = 0; i < boas.size(); i++) {
            List<Pixel> boa = boas.get(i);
            Iterator<Pixel> iterator = boa.iterator();
            while (iterator.hasNext()) {
                Pixel p = iterator.next();
                BufferedImage img = images.get(p.getZ());
                buffer = (DataBufferUShort) img.getRaster().getDataBuffer();
                arrayUShort = buffer.getData();
                arrayUShort[p.getX() + p.getY() * xDim] = (short) (i + 2);
            }
        }
//        cs.setResultImages(images);
        return images;
    }

//    public void readTiffImageProperties(String inputTifImagePath) {
//        Iterator readersIterator = ImageIO.getImageReadersByFormatName("tif");
//        ImageReader imageReader = (ImageReader) readersIterator.next();
//        ImageInputStream imageInputStream;
//        try {
//            imageInputStream = new FileImageInputStream(new File(inputTifImagePath));
//            imageReader.setInput(imageInputStream, false, true);
//
//            /* Take a input from a file */
//            FileSeekableStream fileSeekableStream;
//            fileSeekableStream = new FileSeekableStream(inputTifImagePath);
//
//            /* create ImageDecoder to count your pages from multi-page tiff */
//            ImageDecoder iDecoder = ImageCodec.createImageDecoder("tiff", fileSeekableStream, null);
//
//            /* count the number of pages inside the multi-page tiff */
//            int pageCount = iDecoder.getNumPages();
//
//            /* use first for loop to get pages one by one */
//            for (int page = 0; page < pageCount; page++) {
//                /* get image metadata for each page */
////                IIOMetadata imageMetadata = imageReader.getImageMetadata(page);
//
//
////                TIFFDirectory ifd = TIFFDirectory.createFromMetadata(imageMetadata);
//                TIFFDirectory ifd = new TIFFDirectory(fileSeekableStream, page);
//
//                /* Create a Array of TIFFField*/
////                TIFFField[] allTiffFields = ifd.getTIFFFields();
//                TIFFField[] allTiffFields = ifd.getFields();
//                /* use second for loop to get all field data */
//                for (int i = 0; i < allTiffFields.length; i++) {
//                    TIFFField tiffField = allTiffFields[i];
//
//                    /* name of property */
//                    String nameOfField = tiffField.getTag();
//                            .getTag().getName();
//
//                    /* Tag no. of the property (optional) */
//                    int numberOfField = tiffField.getTagNumber();
//
//                    /* Type of property (optional) */
//                    String typeOfField = TIFFField.getTypeName(tiffField.getType());
//
//                    /* Value of Property*/
//                    String valueOfField = tiffField.getValueAsString(0);
//
//                    /* print it down as per your way */
//                    System.out.println((i + 1) + ". " + nameOfField + ", " + numberOfField + ", " + typeOfField + ", " + valueOfField);
//                }
//                /* just for separate between two page (image) property */
//                System.out.println("======================================");
//            }
//        } catch (FileNotFoundException e1) {
//            e1.printStackTrace();
//        } catch (IOException e1) {
//            e1.printStackTrace();
//        }
//    }

}
