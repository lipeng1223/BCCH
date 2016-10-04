package com.bc.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateFormat {

    private static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    private static SimpleDateFormat fname = new SimpleDateFormat("MM-dd-yy");

    private DateFormat(){}

    public static final String format(Date date){
        if (date == null){
            return "";
        }
        return sdf.format(date);
    }

    public static final String formatForFilename(Date date){
        if (date == null){
            return "";
        }
        return fname.format(date);
    }

    public static final String now(){
        return sdf.format(Calendar.getInstance().getTime());
    }

    public static final String firstOfMonth(){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        return sdf.format(c.getTime());
    }
    
    public static final String yesterday(){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH)-1);
        return sdf.format(c.getTime());
    }
    
    public static final Date parse(String str) throws ParseException {
        if (str == null){
            return Calendar.getInstance().getTime();
        }
        return sdf.parse(str);
    }
    
    public static final Date parsePst(String str) throws ParseException {
        if (str == null){
            return Calendar.getInstance().getTime();
        }
        TimeZone tz = TimeZone.getTimeZone("PST");
        tz.setRawOffset(tz.getRawOffset()-tz.getDSTSavings());
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        sdf.setTimeZone(tz);
        return sdf.parse(str);
    }
    
}
