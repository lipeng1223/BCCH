/*
 * BreaClientLauncher.java
 *
 * Created on September 10, 2005, 11:38 AM
 *
 */

package com.bc.breakclient;

/**
 *
 * @author megela
 */
public class BreakClientLauncher {
    
    /** Creates a new instance of BreaClientLauncher */
    public BreakClientLauncher() {
        System.setProperty("swing.aatext", "true");
        BreakClientLogin bcl = new BreakClientLogin();
        bcl.setVisible(true);
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.setProperty("sun.java2d.noddraw", "true");
        new BreakClientLauncher();
    }
    
}
