package com.bc.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateFormatLong {

    private static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
    private static SimpleDateFormat sdftz = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss ");

    private DateFormatLong(){}

    public static final String format(Date date){
        if (date == null){
            return "";
        }
        return sdf.format(date);
    }

    public static final String formatWithTz(Date date){
        if (date == null){
            return "";
        }
        return sdftz.format(date);
    }
    
    public static final String formatWithTzPst(Date date){
        if (date == null){
            return "";
        }
        TimeZone tz = TimeZone.getTimeZone("PST");
        tz.setRawOffset(tz.getRawOffset()-tz.getDSTSavings());
        sdftz.setTimeZone(tz);
        return sdftz.format(date) + "PST";
    }
    
    public static final String now(){
        return sdf.format(Calendar.getInstance().getTime());
    }

    public static final Date parse(String str) throws ParseException {
        if (str == null){
            return Calendar.getInstance().getTime();
        }
        return sdf.parse(str);
    }
}
