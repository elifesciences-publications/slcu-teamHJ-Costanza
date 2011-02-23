
public class Binner {

    private int[] count;
    private double binWidth;
    
    public Binner(int nbins, double bw){

        binWidth = bw;
        count  = new int[nbins];
        for(int i = 0; i < nbins; ++i){
            count[i] = 0;
        }
    }
    
    public Binner( double max, double bw){
        this((int)(max/bw) +1, bw);
    }
    
    public int getCount (int i) throws Exception {
        return count[i];
    }
    
    public int getSize () {
        return count.length;
    }
    
    public double getBinWidth () {
        return binWidth;
    }
    
    public void increaseCount (int i) throws Exception {
        ++count[i];
    }
    
    public void zeroCount (int i) throws Exception {
        count[i] = 0;
    }
    
    public void putIn( double x ) throws Exception {
        int i = getBinIndex(x);
        increaseCount(i);
    }
    
    public int getBinIndex(double x){
     return (int)(x/binWidth);  
    }
    
    public Pair<Double, Double> getBinLimits( int bi){
        double low = bi*binWidth;
        return new Pair<Double, Double>(low, low+binWidth);
    }
}

