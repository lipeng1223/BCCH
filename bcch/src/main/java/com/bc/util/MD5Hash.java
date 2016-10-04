package com.bc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Hash {
    
    private MD5Hash(){}

    private static final char kHexChars[] =
    { '0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f' };
    
    public static String encode(String text) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(text.getBytes());
        return bufferToHex(md.digest());
    }
    
    public static String getHash(File file) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        FileInputStream fis = new FileInputStream(file);
        int read;
        byte[] buffer = new byte[1024];
        while ((read = fis.read(buffer)) != -1){
            md.update(buffer, 0, read);
        }
        fis.close();
        return bufferToHex(md.digest());
    }
    
    /**
     *   @param  buffer  a buffer to convert to hex
     *   @return the hex string version of the input buffer
     */
    private static String bufferToHex(byte buffer[]) {
        return bufferToHex(buffer, 0, buffer.length);
    }
    
    /**
     *   @param  buffer      a buffer to convert to hex
     *   @param  startOffset the offset of the first byte in the buffer to process
     *   @param  length      the number of bytes in the buffer to process
     *   @return the hex string version of the input buffer
     */
    private static String bufferToHex(byte buffer[], int startOffset, int length) {
        StringBuffer hexString = new StringBuffer(2 * length);
        int endOffset = startOffset + length;
        for (int i = startOffset; i < endOffset; i++) {
            appendHexPair(buffer[i], hexString);
        }
        return hexString.toString();
    }
    
    /**
     *   @param  b a byte whose hex representation is to be obtained
     *   @param  hexString the string to append the hex digits to
     */
    private static void appendHexPair(byte b, StringBuffer hexString) {
        char highNibble = kHexChars[(b & 0xF0) >> 4];
        char lowNibble = kHexChars[b & 0x0F];
        hexString.append(highNibble);
        hexString.append(lowNibble);
    }
    
}
