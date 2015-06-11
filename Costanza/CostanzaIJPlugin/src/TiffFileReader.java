
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import java.io.File;

public class TiffFileReader extends JFileChooser {

    public TiffFileReader() {
        addChoosableFileFilter(new TiffFileReader.TifFileFilter());
        File wrkDir = new File(WRK_DIR);
        setCurrentDirectory(wrkDir);
    }
    public static final String WRK_DIR = "./plugins/Costanza";
//    private final Costanza_Plugin plugin;

    private class TifFileFilter extends FileFilter {

        public static final String ext = "tif";
        public static final String descr = "Tif images" + " *." + ext;

        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }

            String extension = FileFilterUtils.getExtension(f);
            if (extension != null) {
                if (extension.equals(FileFilterUtils.tiff)
                        || extension.equals(FileFilterUtils.tif)
//                        || extension.equals(Utils.gif)
//                        || extension.equals(Utils.jpeg)
//                        || extension.equals(Utils.jpg)
//                        || extension.equals(Utils.png)
                        ){
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        }
        

        @Override
        public String getDescription() {
            return descr;
        }

        public String getExtension(File f) {
            String e = null;
            String s = f.getName();
            int i = s.lastIndexOf('.');

            if (i > 0 && i < s.length() - 1) {
                e = s.substring(i + 1).toLowerCase();
            }
            return e;
        }
    }

//    public void readBOAsFromTiff(File f) {
//
//        System.out.println("reading boas from tif: TifFileReader");
////        if (new TifFileFilter().getExtension(f) == null) {
////            f = new File(f.getPath() + "." + TifFileFilter.ext);
////        }
////        plugin.writeBoas(f);
//        plugin.readBOAsFromTiff(f);
//    }
}
