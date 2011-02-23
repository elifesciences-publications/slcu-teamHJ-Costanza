/*
 * Flters file names to get the list of image stack file names
 */

package costanza.ui;
import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StackImageFilter implements FilenameFilter{

    public StackImageFilter(File d, String name){
        dir = d;
        basename = name;
        p = Pattern.compile(basename + "(\\d+)\\.(.[^\\.]+)");
//        p = Pattern.compile(basename + ".+");
    }

    public String getExtension(File f){
        Matcher m = p.matcher(f.getName());
        m.matches();
        return m.group(2);
    }
    
    public int getNumber(File f){
        Matcher m = p.matcher(f.getName());
        m.matches();
        return Integer.parseInt(m.group(1));
    }
    
    public boolean accept(File d, String name) {
//        System.out.println("basename = " + basename);
//        System.out.println("pathname = " + dir.getPath() + "|" + name);
//        System.out.println("patern = " + p.pattern());
        Matcher m = p.matcher(name);
//        System.out.println("matches = " + m.matches());
        return dir.equals(d) && m.matches();
//        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private String basename;
    File dir;
    private Pattern p;
}
