
import java.util.Collection;
import costanza.Case;
import costanza.CellCenter;
import costanza.DataId;
import costanza.Image;
import costanza.Pixel;
import costanza.Stack;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.Vector;

/** Utility class for the Costanza Plugin. */
public class Utility {

    /** Create new Stack object from ij.ImagePlus object. */
    static public Stack createStackFromImagePlus(ij.ImagePlus imagePlus) throws Exception {
        ij.ImageStack imageStack = imagePlus.getStack();
        int slices = imageStack.getSize();

        Stack stack = new Stack();
        for (int n = 1; n <= slices; ++n) {
            ij.process.ImageProcessor sliceProcessor = imageStack.getProcessor(n);
            Image image = new Image(sliceProcessor.createImage());
            stack.addImage(image);
        }

        ij.measure.Calibration calibration = imagePlus.getCalibration();
        stack.setXScale((float) calibration.pixelWidth);
        stack.setYScale((float) calibration.pixelHeight);
        stack.setZScale((float) calibration.pixelDepth);

        return stack;
    }

    /** Creates new ij.ImagePlus object from Stack. */
    static public ij.ImagePlus createImagePlusFromStack(Stack stack, String name) throws Exception {
        int width = stack.getWidth();
        int height = stack.getHeight();

        ij.ImageStack imageStack = new ij.ImageStack(width, height);

        for (int i = 0; i < stack.getDepth(); ++i) {
            Image image = stack.getImage(i);
            imageStack.addSlice("", getImageProcessorFromImage(image));
        }

        ij.ImagePlus imagePlus = new ij.ImagePlus(name, imageStack);
        ij.measure.Calibration calibration = imagePlus.getCalibration();
        calibration.pixelWidth = stack.getXScale();
        calibration.pixelHeight = stack.getYScale();
        calibration.pixelDepth = stack.getZScale();

        return imagePlus;
    }

    /** Creates new ij.ImagePlus object from result stack. */
    static public ij.ImagePlus createImagePlusFromResultStack(Case IJCase, String name) throws Exception {
        Stack stack = IJCase.getStack();
        int width = stack.getWidth();
        int height = stack.getHeight();
        int depth = stack.getDepth();

        ij.ImageStack imageStack = new ij.ImageStack(width, height);

        java.awt.image.BufferedImage[] resultImage = IJCase.getResultImages();

        for (int i = 0; i < depth; ++i) {
            ij.ImagePlus ipTmp = new ij.ImagePlus(name, resultImage[i]);
            ij.ImageStack stackTmp = ipTmp.getImageStack();
            if (stackTmp.getSize() != 1) {
                throw new Exception("Unexpected error in getImagePlusFromResultStack()");
            }
            imageStack.addSlice("", stackTmp.getProcessor(1));
        }

        ij.ImagePlus imagePlus = new ij.ImagePlus(name, imageStack);
        ij.measure.Calibration calibration = imagePlus.getCalibration();
        calibration.pixelWidth = stack.getXScale();
        calibration.pixelHeight = stack.getYScale();
        calibration.pixelDepth = stack.getZScale();

        return imagePlus;
    }

    /** Get ij.ImageProcessor from Image. */
    static public ij.process.ImageProcessor getImageProcessorFromImage(Image image) throws Exception {
        ij.ImagePlus ip = new ij.ImagePlus("", image.getImage());
        ij.ImageStack stack = ip.getImageStack();
        if (stack.getSize() != 1) {
            throw new Exception("Unexpected error in Tools.getImageProcessorFromImage()");
        }
        return stack.getProcessor(1);
    }

    static public void printWarning(String string) {
        ij.IJ.showMessage("Costanza Plugin", "Warning: " + string + "\n");
    }

    static public void printError(String string) {
        ij.IJ.showMessage("Costanza Plugin", "Error: " + string + "\n");
    }

    /** Creates new ij.ImagePlus object giving density of found cells */
    @SuppressWarnings("unchecked")
    static public ij.ImagePlus createCellDensityImagePlus(Case IJCase, String name) throws Exception {
        Stack stack = IJCase.getStack();
        int width = stack.getWidth();
        int height = stack.getHeight();
        int depth = stack.getDepth();

        System.out.println("Making density plot.");
        Image img = new Image(width, height);
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                img.setIntensity(x, y, 0);
            }
        }
        float RAD = 50;
        //   BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        Collection<CellCenter> cellCenters = (Collection<CellCenter>) IJCase.getCellData(DataId.CENTERS);
        if (cellCenters != null) {
            //System.out.println("cell centers :" + cellCenters.size());
            int r = (int) RAD;
            float RR = RAD * RAD;
            for (Iterator iter = cellCenters.iterator(); iter.hasNext();) {
                CellCenter cc = (CellCenter) iter.next();
                int x0 = cc.getX();
                int y0 = cc.getY();
                int xmin = Math.max(x0 - r, 0);
                int xmax = Math.min(x0 + r, width);
                for (int x = xmin; x < xmax; ++x) {
                    int dx = x - x0;
                    int dy = (int) Math.sqrt(RR - dx * dx);
                    int ymin = Math.max(y0 - dy, 0);
                    int ymax = Math.min(y0 + dy, height);
                    for (int y = ymin; y < ymax; ++y) {

                        int count = (int) img.getIntensity(x, y);
                        //System.out.println("x =" + (x-x0) + " y= " + (y-y0) + "; count = " + (count+1) );
                        img.setIntensity(x, y, ++count);
                    }
                }

            }
        }
        float maxInten = img.getMaxIntensity();
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                float inten = img.getIntensity(x, y) / maxInten;
                img.setIntensity(x, y, inten);
            }
        }
        return new ij.ImagePlus(name, img.getImage());
    }

    /** Creates new ij.ImagePlus object giving density of found cells */
    @SuppressWarnings("unchecked")
    static public ij.ImagePlus createCellDensityImagePlus2(Case IJCase, String name) throws Exception {
        Stack stack = IJCase.getStack();
        int width = stack.getWidth();
        int height = stack.getHeight();
        int depth = stack.getDepth();
        float RAD = 30;
        System.out.println("Making density plot2.");
        Image img = new Image(width, height);

        Collection<CellCenter> cellCenters = (Collection<CellCenter>) IJCase.getCellData(DataId.CENTERS);
        if (cellCenters != null) {
            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    img.setIntensity(x, y, 0);
                }
            }
            float RR = RAD * RAD;
            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {

                    for (Iterator iter = cellCenters.iterator(); iter.hasNext();) {
                        CellCenter cc = (CellCenter) iter.next();
                        int x0 = cc.getX();
                        int y0 = cc.getY();
                        int dx = x - x0;
                        int dy = y - y0;
                        float rr = dx * dx + dy * dy;
                        if (rr < RR) {
                            int count = (int) img.getIntensity(x, y);
                            //System.out.println("x =" + (x-x0) + " y= " + (y-y0) + "; count = " + (count+1) );
                            img.setIntensity(x, y, ++count);
                        }
                    }
                }
            }
        }

        float maxInten = img.getMaxIntensity();
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                float inten = img.getIntensity(x, y) / maxInten;
                img.setIntensity(x, y, inten);
            }
        }
        return new ij.ImagePlus(name, img.getImage());
    }

    @SuppressWarnings("unchecked")
    static public void writeCellDistanceHistogram(File f, Case IJCase, int cellId) {
        System.out.println("writing cell distance histogram");
        try {
            Vector<CellCenter> cellCenters = (Vector<CellCenter>) IJCase.getCellData(DataId.CENTERS);
            if (cellCenters != null) {
                Stack stack = IJCase.getStack();
                int width = stack.getWidth();
                int height = stack.getHeight();
                double max = Math.sqrt(width * width + height * height);
                Binner bins = new Binner(max, 30);


                CellCenter c0 = cellCenters.get(cellId);
                int x0 = c0.getX();
                int y0 = c0.getY();
                double d = 0;
                for (Iterator iter1 = cellCenters.iterator(); iter1.hasNext();) {
                    CellCenter c1 = (CellCenter) iter1.next();
                    if (c1.getCellId() != c0.getCellId()) {
                        int x1 = c1.getX();
                        int y1 = c1.getY();
                        int dx = x1 - x0;
                        int dy = y1 - y0;
                        d = Math.sqrt(dx * dx + dy * dy);
                        bins.putIn(d);
                    }
                }


                BufferedWriter out = new BufferedWriter(new FileWriter(f));

                String sp = " ";
//                double bw = 1.0/bins.getBinWidth();
                double bw = bins.getBinWidth();
                for (int i = 0; i < bins.getSize(); ++i) {
                    Pair<Double, Double> pair = bins.getBinLimits(i);
//                    double div = Math.PI * (2 * pair.a * bw + bw * bw);
//                    double div = 2*pair.a*bw + 1.0;
//                    out.write(i + sp + pair.a + sp + pair.b + sp + (bins.getCount(i) / div) + "\n");
                    out.write(i + sp + pair.a + sp + pair.b + sp + bins.getCount(i) + "\n");
                }
                out.close();
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
//            status = PluginStatus.EXIT_APPLICATION;
        }
    }

    @SuppressWarnings("unchecked")
    static public void writeCellDistanceHistogram(Case IJCase) {
        System.out.println("writing cell distance histogram");
        try {
            Vector<CellCenter> cellCenters = (Vector<CellCenter>) IJCase.getCellData(DataId.CENTERS);
            if (cellCenters != null) {

                Stack stack = IJCase.getStack();
                int width = stack.getWidth();
                int height = stack.getHeight();
                double max = Math.sqrt(width * width + height * height);


                for (Iterator iter0 = cellCenters.iterator(); iter0.hasNext();) {
                    Binner bins = new Binner(max, 30);
                    CellCenter c0 = (CellCenter) iter0.next();
                    int x0 = c0.getX();
                    int y0 = c0.getY();
                    double d = 0;
                    for (Iterator iter1 = cellCenters.iterator(); iter1.hasNext();) {
                        CellCenter c1 = (CellCenter) iter1.next();
                        if (c1.getCellId() != c0.getCellId()) {
                            int x1 = c1.getX();
                            int y1 = c1.getY();
                            int dx = x1 - x0;
                            int dy = y1 - y0;
                            d = Math.sqrt(dx * dx + dy * dy);
                            bins.putIn(d);
                        }
                    }

                    File f = new File("./plugins/Costanza/" + c0.getCellId() + ".hist");
                    BufferedWriter out = new BufferedWriter(new FileWriter(f));

                    String sp = " ";
                    //                double bw = 1.0/bins.getBinWidth();
                    double bw = bins.getBinWidth();
                    for (int i = 0; i < bins.getSize(); ++i) {
                        Pair<Double, Double> pair = bins.getBinLimits(i);
//                        double div = Math.PI * (2 * pair.a * bw + bw * bw);
                        //                    double div = 2*pair.a*bw + 1.0;
//                        out.write(i + sp + pair.a + sp + pair.b + sp + (bins.getCount(i) / div) + "\n");
                        out.write(i + sp + pair.a + sp + pair.b + sp + bins.getCount(i) + "\n");
                    }
                    out.close();
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
//            status = PluginStatus.EXIT_APPLICATION;
        }
    }

    @SuppressWarnings("unchecked")
    static public int findCellCenterFromPixel(Case IJCase, Pixel p, float r) {
        try {

            Vector<CellCenter> cellCenters = (Vector<CellCenter>) IJCase.getCellData(DataId.CENTERS);
            if (cellCenters != null) {
                double rr = r * r;
                for (Iterator iter = cellCenters.iterator(); iter.hasNext();) {
                    CellCenter c = (CellCenter) iter.next();
                    int x0 = c.getX();
                    int y0 = c.getY();
                    int z0 = c.getZ();
                    int dx = p.getX() - x0;
                    int dy = p.getY() - y0;
                    int dz = p.getZ() - z0;
                    double d = dx * dx + dy * dy + dz * dz;
                    if (d < rr) {
                        return c.getCellId();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
//            status = PluginStatus.EXIT_APPLICATION;
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    static public int findCellCenterFromPixel(Case IJCase, Pixel p) {
        int minId = -1;
        try {

            Vector<CellCenter> cellCenters = (Vector<CellCenter>) IJCase.getCellData(DataId.CENTERS);
            if (cellCenters != null) {
                Stack stack = IJCase.getStack();
                int width = stack.getWidth();
                int height = stack.getHeight();
                double min = width * width + height * height;

                for (Iterator iter = cellCenters.iterator(); iter.hasNext();) {
                    CellCenter c = (CellCenter) iter.next();
                    int x0 = c.getX();
                    int y0 = c.getY();
                    int z0 = c.getZ();
                    int dx = p.getX() - x0;
                    int dy = p.getY() - y0;
                    int dz = p.getZ() - z0;
                    double d = dx * dx + dy * dy + dz * dz;
                    if (d < min) {
                        min = d;
                        minId = c.getCellId();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
//            status = PluginStatus.EXIT_APPLICATION;
        }
        return minId;
    }

    @SuppressWarnings("unchecked")
    static public void writeCellDistanceHistogram(File f, Case IJCase) {
        System.out.println("writing cell distance histogram");
        try {
            Collection<CellCenter> cellCenters = (Collection<CellCenter>) IJCase.getCellData(DataId.CENTERS);
            if (cellCenters != null) {
                Stack stack = IJCase.getStack();
                int width = stack.getWidth();
                int height = stack.getHeight();
                double max = Math.sqrt(width * width + height * height);
                Binner bins = new Binner(max, 30);

                for (Iterator iter0 = cellCenters.iterator(); iter0.hasNext();) {
                    CellCenter c0 = (CellCenter) iter0.next();
                    int x0 = c0.getX();
                    int y0 = c0.getY();
                    double d = 0;
                    for (Iterator iter1 = cellCenters.iterator(); iter1.hasNext();) {
                        CellCenter c1 = (CellCenter) iter1.next();
                        if (c1.getCellId() > c0.getCellId()) {
                            int x1 = c1.getX();
                            int y1 = c1.getY();
                            int dx = x1 - x0;
                            int dy = y1 - y0;
                            d = Math.sqrt(dx * dx + dy * dy);
                            bins.putIn(d);
                        }
                    }
                }

                BufferedWriter out = new BufferedWriter(new FileWriter(f));

                String sp = " ";
//                double bw = 1.0/bins.getBinWidth();
                double bw = bins.getBinWidth();
                for (int i = 0; i < bins.getSize(); ++i) {
                    Pair<Double, Double> pair = bins.getBinLimits(i);
                    double div = Math.PI * (2 * pair.a * bw + bw * bw);
//                    double div = 2*pair.a*bw + 1.0;
                    out.write(i + sp + pair.a + sp + pair.b + sp + (bins.getCount(i) / div) + "\n");
                }
                out.close();
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
//            status = PluginStatus.EXIT_APPLICATION;
        }
    }

    static public double getClippedRingArea(Pixel p, double r, int w, int h) {
        int x0 = 0, x1 = w, x2 = w, x3 = 0;
        int y0 = 0, y1 = 0, y2 = h, y3 = h;
        int xc = p.getX();
        int yc = p.getY();

        double rr = r * r;
        int dx0 = xc - x0;
        dx0 *= dx0;
        int dy0 = yc - y0;
        dy0 *= dy0;
        int dx1 = xc - x1;
        dx1 *= dx1;
        int dy1 = yc - y1;
        dy1 *= dy1;
        int D0 = dx0 + dy0;
        int D1 = dx1 + dy0;
        int D2 = dx1 + dy1;
        int D3 = dx0 + dy1;

        return 0.0;
    }

    static public Pair<Integer, Integer> checkD(double rr, int D1, int D2, int d, int xc, int x0, int x1) {
        if (d < rr) {
            int r1 = -1, r2 = -1;
            int sq = (int) Math.sqrt(rr - d);
            if (D1 > rr) {
                r1 = xc - sq;
            }
            if (D2 > rr) {
                r2 = xc - sq;
            }
        }
        return new Pair<Integer, Integer>(-2, -2);
    }
}
