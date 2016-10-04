package com.bc.socketserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SocketTest {
    
    private String[] sends;
    
    private Random rand = null;

    public SocketTest(String host, String pin){
        rand = new Random(System.currentTimeMillis());
        
        /*
        sends = new String[]{"GetIsbnInfo:"+pin+":Hurt;0446519960\n", "GetIsbnInfo:"+pin+":Hurt;9780375800153\n", 
                "GetIsbnInfo:"+pin+":Hurt;003097240X\n", "GetIsbnInfo:"+pin+":Hurt;0060825405\n", "GetIsbnInfo:"+pin+":Hurt;0375705775\n", 
                "GetIsbnInfo:"+pin+":Hurt;1416527192\n", "GetIsbnInfo:"+pin+":Hurt;0684801620\n", "GetIsbnInfo:"+pin+":Hurt;0195087445\n", 
                "GetIsbnInfo:"+pin+":Hurt;9780743241182\n"};
        */
        sends = new String[]{"AddToRec:"+pin+":21464;20;9-21-5-2;Hurt;1580081207;24.99;PAP\n"};
        
        try {
            /*
            SocketTestRunner str = new SocketTestRunner(host, 0, "AddToRec:"+pin+":21464;20;9-21-5-2;Hurt;1580081207;24.99;PAP\n");
            str.start();
            str.join();
            */
            SocketTestRunner str = new SocketTestRunner(host, 0, "DeleteFromRec:"+pin+":1061131\n");
            str.start();
            str.join();
            
            /*
            for (int j = 0; j < 1; j++){
                List<SocketTestRunner> list = new ArrayList<SocketTestRunner>();
                for (int i = 0; i < 10; i++){
                    System.out.println("loop: "+j+" threadNum: "+i);
                    SocketTestRunner str = new SocketTestRunner(host, i);
                    str.start();
                    list.add(str);
                }
                for (SocketTestRunner s : list){
                    if (s.isAlive()){
                        s.join();
                        //System.out.println("thread is done threadNum: "+s.getNum());
                    }
                }
                list.clear();
            }
            *
            */
        } catch (Exception e){}
    }
    
    private class SocketTestRunner extends Thread {
        
        private int num = 0;
        private String host;
        private String command;
        
        public SocketTestRunner(String host, int num, String command){
            this.num = num;
            this.host = host;
            this.command = command;
        }
        
        public int getNum(){
            return num;
        }
        
        public void run(){
            try {
                Socket s = new Socket(host, 8888);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                
                //String send =  "GetPendingRecs:nothing\n";
                //String send =  sends[rand.nextInt(sends.length)];
                String send =  command;


                //if (num %2 == 0){
                //    send = "AddToRec:4279;10;Dk-8;Hurt;1573223735;19.99;\n";
                //}
                /*
                if (num % 2 == 0){
                    send = "GetPendingOrders:nothing\n";
                } else if (num % 3 == 0){
                    send = "GetCustomers:nothing\n";
                } else if (num % 5 == 0){
                    send = "GetVendors:nothing\n";
                } 
                */
                
                //if (num == 14){
                //    return;
                //}
                
                writer.write(send);
                writer.flush();
                BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
                for (int i = 0; i < 30; i++){
                    if (reader.ready()){
                        String line = reader.readLine();
                        System.out.println("threadNum: "+num+" line: "+line);
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
    }
    
    public static void main(String[] args){
        String host = "localhost";
        if (args.length >= 1) host = args[0];
        System.out.println("Running socket test against host: "+host);
        String pin = "1024";
        if (args.length >= 2) pin = args[1];
        System.out.println("Running socket test with pin: "+pin);
        new SocketTest(host, pin);
    }
}
