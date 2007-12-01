package Costanza.ui;

import Costanza.Image;
import Costanza.Stack;
import Costanza.Inverter;
import Costanza.Case;

/*
 * TextGUI.java
 *
 * Created on December 1, 2007, 12:50 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 * The Text version of our GUI.
 * @author michael
 */
public class TextGUI {
    
    /** Creates a new instance of TextGUI */
    public TextGUI() {
        Image image = new Image(10, 10);
        for(int i=0; i<image.getWidth(); ++i){
            for(int j=0; j<image.getHeight(); ++j){
                image.setIntensity(i, j, (i == j) ? 1 : 0);
                System.out.print("");
            }
            System.out.println();
        }
        Stack stack = new Stack();
        stack.addImage(image);
        Case myCase = new Case(stack);
        Inverter inverter = new Inverter();
        myCase = inverter.process(myCase);
    }
    
    public static void main(String[] argv){
        new TextGUI();
    }
}
