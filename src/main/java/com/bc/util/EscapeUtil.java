package com.bc.util;

import org.apache.commons.lang.StringEscapeUtils;

public class EscapeUtil {

    private EscapeUtil(){}
    
    /**
     * Escapes the characters in a String using JavaScript String rules.
     * 
     * Escapes any values it finds into their JavaScript String form. Deals correctly with quotes and control-chars (tab, backslash, cr, ff, etc.)
     */
    public static String escapeJavaScript(String str){
        return StringEscapeUtils.escapeJavaScript(str);
    }
    
    /**
     * Replaces anything it can with html entities
     * 
     * "bread" & "butter" becomes &quot;bread&quot; &amp; &quot;butter&quot;
     */
    public static String escapeHtml(String str){
        return StringEscapeUtils.escapeHtml(str);
    }
    
}
