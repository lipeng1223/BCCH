package com.bc.util;

import org.apache.commons.validator.routines.ISBNValidator;

public class IsbnUtil {


    private static int[] isbn10Multi = new int[]{10,9,8,7,6,5,4,3,2};
    private static String[] check10 = new String[]{"0", "X", "9", "8", "7", "6", "5", "4", "3", "2", "1"};
    private static int[] isbn13Multi = new int[]{1,3,1,3,1,3,1,3,1,3,1,3};
    private static String[] check13 = new String[]{"0", "9", "8", "7", "6", "5", "4", "3", "2", "1"};
    
    // validates as isbn10 or isbn13
    public static boolean isValid(String isbn){
        return ISBNValidator.getInstance().isValid(isbn);
    }
    public static boolean isValid10(String isbn){
        return ISBNValidator.getInstance().isValidISBN10(isbn);
    }
    public static boolean isValid13(String isbn){
        return ISBNValidator.getInstance().isValidISBN13(isbn);
    }
    
    public static boolean validate10(String isbn){
        if (isbn == null || isbn.length() != 10) return false;
        try {
            int sum = 0;
            int[] digits = new int[isbn.length()];
            
            char[] isbnChars = isbn.toCharArray();
            for (int i = 0; i < isbnChars.length; i++){
                digits[i] = Character.getNumericValue(isbnChars[0]);
            }
            
            for(int i = 0; i < (isbn.length() - 1); i++) {
                sum += digits[i] * (i + 1);
            }
            
            if ((sum % 11) == digits[isbn.length() - 1]) return true;
            
        } catch (Exception e){}
        return false;
    }    
    
    /**
     * http://www.isbn.org/standards/home/isbn/international/html/usm7.htm
     */
    
    public static String getIsbn10(String isbn){
        if (isbn.length() == 10){
            return isbn;
        } else if (isbn.length() == 13 && isbn.startsWith("978") && isValid13(isbn)){
            int[] rip = new int[9];
            for (int i = 3; i < 12; i++){
                try {
                    rip[i-3] = Integer.parseInt(isbn.substring(i, i+1));
                } catch (Exception e){
                    // not a number
                    return isbn;
                }
            }
            int added = 0;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < rip.length; i++){
                added += isbn10Multi[i]*rip[i];
                sb.append(rip[i]);
            }
            int check = added % 11;
            sb.append(check10[check]);
            return sb.toString();
        }
        // not a 13 or 10, don't know what it is
        return isbn;
    }
    
    public static String getIsbn13(String isbn){
        if (isbn.length() == 13){
            return isbn;
        } else if (isbn.length() == 10 && isValid10(isbn)){
            // calc 13
            int[] rip = new int[12];
            rip[0] = 9;
            rip[1] = 7;
            rip[2] = 8;
            for (int i = 0; i < 9; i++){
                try {
                    rip[i+3] = Integer.parseInt(isbn.substring(i, i+1));
                } catch (Exception e){
                    // not a number
                    return isbn;
                }
            }
            int added = 0;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < rip.length; i++){
                added += isbn13Multi[i]*rip[i];
                sb.append(rip[i]);
            }
            int check = added % 10;
            sb.append(check13[check]);
            return sb.toString();
        }
        // not a 13 or 10, don't know what it is
        return isbn;
    }    
}
