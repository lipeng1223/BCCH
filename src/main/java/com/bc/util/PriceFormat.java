package com.bc.util;

import java.text.DecimalFormat;

public class PriceFormat {

    private static final DecimalFormat format = new DecimalFormat("$#,##0.00;-$#,##0.00");
    private static final DecimalFormat noSignFormat = new DecimalFormat("#,##0.00;-#,##0.00");

    public static final String format(Double d){
        if (d == null){
            return "";
        }
        return format.format(d);
    }

    public static final String format(Float f){
        if (f == null){
            return "";
        }
        return format.format(f);
    }

    public static final String format(Integer i){
        if (i == null){
            return "";
        }
        return noSignFormat.format(i);
    }
    
    
    public static final String noSignFormat(Double d){
        if (d == null){
            return "";
        }
        return noSignFormat.format(d);
    }

    public static final String noSignFormat(Float f){
        if (f == null){
            return "";
        }
        return noSignFormat.format(f);
    }

    public static final String noSignFormat(Integer i){
        if (i == null){
            return "";
        }
        return noSignFormat.format(i);
    }
    
}
