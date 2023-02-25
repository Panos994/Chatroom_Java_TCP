/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tcp_chatroom;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author pfoteinopoulos
 */
public class Client implements Runnable {
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private boolean done;
    
    
    
    @Override
    public void run() {
            try{
                 client = new Socket("127.0.0.1",9999);
                out = new PrintWriter(client.getOutputStream(),true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                
                InputHandler inHandler = new InputHandler();
                Thread t = new Thread(inHandler);
                t.start();
                
                String inMessage;
                while((inMessage = in.readLine()) != null){
                    System.out.println(inMessage);
                }
                
            }catch(IOException e){
                 shutdown();
            }
                
               
                
    }

    
    
     
    
    
        public void shutdown(){
             done = true;
             try{
                 in.close();
                 out.close();
                 if(!client.isClosed()){
                     client.close();
                 }
             }catch(IOException e){
                    //ignone
                 }
                 
         }
    
    
    
    
    
    
    class InputHandler implements Runnable{

        @Override
        public void run() {
          try{
              
              BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
              while(!done){
                  String message = inReader.readLine();
                  if(message.equals("/quit")){
                      out.println(message);
                      inReader.close();
                      shutdown();
                  }else{
                      out.println(message);
                  }
                  }
                  
          }catch (IOException e){
                     shutdown();
        
              
    
    }

        }
    }

    
        public static void main(String[] args){
    
         Client client = new Client();
         client.run();
    
    
    }



    
    
}          
        
