package com.bc.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

import org.apache.log4j.Logger;

/**
 * Timing utility method
 * 
 * Just create it with an identification string then call start to start the timing, stop to end it with logging output
 * 
 * @author tmegela
 *
 */
public class Timing {

    private Logger logger = Logger.getLogger(Timing.class);
    
    private long start;
    private long end;
    private String identifier;
    private NumberFormat nf;
    
    public Timing(String identifier){
        this.identifier = identifier;
        nf = new DecimalFormat("0.00");
    }
    
    public void start(){
        start = System.currentTimeMillis();
        logger.info(identifier+" - timing start: "+Calendar.getInstance().getTime());
    }
    
    public void stop(){
        end = System.currentTimeMillis();
        logger.info(identifier+" - timing stop: "+Calendar.getInstance().getTime()+"  seconds: "+nf.format((end-start)/1000D)+" millis: "+(end-start));
    }
}
