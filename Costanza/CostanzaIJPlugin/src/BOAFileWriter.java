
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;

public class BOAFileWriter extends JFileChooser {

    public BOAFileWriter(Costanza_Plugin p) {
        addChoosableFileFilter(new BOAFileFilter());
        plugin = p;
        
        File wrkDir = new File(WRK_DIR);
        
        setCurrentDirectory(wrkDir);
    }
    
    
    public static final String WRK_DIR = "./plugins/Costanza";
//    private MainFrame frame;
    private Costanza_Plugin plugin;


    private class BOAFileFilter extends FileFilter {

        public static final String ext = "boa";
        public static final String descr = "BOA text files" + " *." + ext;

        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }

            String extension = getExtension(f);
            if ((extension != null) && extension.equals(ext)) {
                return true;
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
    
    public void saveBOAs(File f) {

        System.out.println("saving boas: BOAFileWriter");
            if(new BOAFileFilter().getExtension(f) == null){
               f = new File(f.getPath() + "." + BOAFileFilter.ext);
            }
            plugin.writeBoas(f);
    }
}
