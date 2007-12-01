/*
 * PeakMerger.java
 *
 * Created on December 1, 2007, 12:49 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Costanza;

/**
 *
 * @author pontus
 */
public class PeakMerger extends Processor{
    
    /** Creates a new instance of PeakMerger */
    public PeakMerger() {
    }
 /** 
  * Implements the PeakMerger algorithm.
  * @param a Case and an Option
  * @returns a modified Case object
  * @see Processor
  */   
 public Case process(Case c, Options o) throws Exception {
     //float radius = (float)o.getOptionValue("radius");   
     return c;
    }
}
