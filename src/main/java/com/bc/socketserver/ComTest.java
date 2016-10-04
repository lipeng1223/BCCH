package com.bc.socketserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ComTest {

    public ComTest(){
        try {
            Socket s = new Socket("localhost", 8888);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            
            String send =  "GetIsbnInfo:Hurt;0340706139\n";
            writer.write(send);
            writer.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
            for (int i = 0; i < 30; i++){
                if (reader.ready()){
                    String line = reader.readLine();
                    System.out.println("line: "+line);
                    break;
                } else {
                    Thread.sleep(1000);
                }
            }
            reader.close();
            writer.close();
            s.close();
            
            s = new Socket("localhost", 8888);
            writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            send =  "GetIsbnInfo:Hurt;9780340706138\n";
            writer.write(send);
            writer.flush();
            reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
            for (int i = 0; i < 30; i++){
                if (reader.ready()){
                    String line = reader.readLine();
                    System.out.println("line: "+line);
                    break;
                } else {
                    Thread.sleep(1000);
                }
            }
            
            s.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args){
        new ComTest();
    }
}
