package com.bc.util;

import java.util.Random;

public class PasswordGenerator {
    
    private PasswordGenerator(){}

    private static final String[] available = new String[]{"a","b","c","d","e","f","g","h","i","j","k","m","n",
        "p","q","r","s","t","u","v","w","x","y","z",
        "A","B","C","D","E","F","G","H","J","K","L","M","N",
        "P","Q","R","S","T","U","V","W","X","Y","Z",
        "2","3","4","5","6","7","8","9",
        "#","$","%","&","*","?","@"};

    public static String generatePassword(){
        StringBuilder sb = new StringBuilder();
        Random rand = new Random(System.currentTimeMillis());
        for (int i = 0; i < 10; i++){
            sb.append(available[rand.nextInt(available.length)]);
        }
        return sb.toString();
    }
    
}
