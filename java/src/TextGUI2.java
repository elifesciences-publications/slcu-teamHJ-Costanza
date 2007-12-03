import Costanza.Image;
import Costanza.Stack;
import Costanza.Inverter;
import Costanza.Case;
import Costanza.Options;
import Costanza.MeanFilter;
import Costanza.GradientDescent;
import Costanza.Driver;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;

/**
 * The Text version of our GUI.
 * @author michael
 */
public class TextGUI2 {
    
    /** Creates a new instance of TextGUI */
    public TextGUI2(String baseName) throws Exception {
        System.out.println("Creating a Stack");
        Stack stack = getImageStack(baseName);
        Case myCase = new Case(stack);
        Options options = new Options();
        options.addOption("radius", new Float(1.0f));
        System.out.println("Applying the Mean filter.");
        MeanFilter meanFilter = new MeanFilter();
        //meanFilter.process(myCase, options);
        Inverter inverter = new Inverter();
        inverter.process(myCase, options);
        System.out.println("Saving the images.");
        saveImageStack(baseName, myCase.getStack());
        
    }
    
    private Stack getImageStack(String baseName) throws Exception {
        Stack stack = new Stack();
        for (int i = 0; i < 4; ++i) {
            System.out.println("Opening image: "+baseName+i+".jpg");
            Image image = getImage(baseName+i+".jpg");
            stack.addImage(image);
        }
        return stack;
    }
    
    private Image getImage(String baseName) {
        BufferedImage awtImage = null;
        try {
            awtImage = ImageIO.read(new File(baseName));
        } catch (IOException e) {
            System.out.println("Couldn't read file " + baseName + ": " + e.getMessage());
        }
        int w = awtImage.getWidth(null);
        int h = awtImage.getHeight(null);
        Graphics g = awtImage.getGraphics();
        g.drawImage(awtImage, 0, 0, null);
        Image image = new Image(w, h);
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                //image.setIntensity(i,j,handlesinglepixel(awtImage.getRGB(i,j))/255.0f);
                image.setIntensity(i,j,awtImage.getRaster().getSampleFloat(i,j,0)/255.0f);
                //System.out.println("Setting intensity: "+image.getIntensity(i,j));
            }
        }
        return image;
    }
    
    private float[] handlepixels(BufferedImage img, int x, int y, int w, int h) {
        int[] pixels = new int[w * h];
        float[] floatPixels = new float[w * h];
        PixelGrabber pg = new PixelGrabber(img, x, y, w, h, pixels, 0, w);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
            System.err.println("interrupted waiting for pixels!");
            return null;
        }
        if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
            System.err.println("image fetch aborted or errored");
            return null;
        }
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                floatPixels[j * w + i] = handlesinglepixel(pixels[j * w + i]);
            }
        }
        return floatPixels;
    }
    
    public float handlesinglepixel(int pixel) {
        int alpha = (pixel >> 24) & 0xff;
        int red   = (pixel >> 16) & 0xff;
        int green = (pixel >>  8) & 0xff;
        int blue  = (pixel      ) & 0xff;
        
        return (float)(red);
    }
    
    private void saveImageStack(String baseName, Stack stack) {
        for (int i = 0; i < stack.getDepth(); i++) {
            Image image = stack.getImage(i);
            try {
                BufferedImage bi = toAwtImage(image); // retrieve image
                File outputfile = new File(baseName+i+"New.jpg");
                ImageIO.write(bi, "jpg", outputfile);
            } catch (IOException e){}
        }
    }
    
    private BufferedImage toAwtImage(Image image) {
        BufferedImage bi = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                float intensity = image.getIntensity(i,j)*255;
                bi.getRaster().setSample(i,j,0,intensity);
            }
        }
        return bi;
    }
    
    public static void main(String[] argv){
        try{
            new TextGUI2(argv[0]);
        }catch(Exception e){
            System.out.print("Error: ");
            System.out.println(e.getMessage());
        }
    }
}