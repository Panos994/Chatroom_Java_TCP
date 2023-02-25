/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tcp_chatroom;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import static java.lang.Thread.State.NEW;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pfoteinopoulos
 */
public class Server implements Runnable{
    
    
    private ArrayList<ConnectionHandler> connections;
    
    private ServerSocket server;
    private boolean done;
    private ExecutorService pool;
    public Server(){
        connections = new ArrayList<>();
        done = false;
    }
    @Override
    public void run(){
        try{
            
         server = new ServerSocket(9999);
         pool = Executors.newCachedThreadPool();
            while(!done){
                Socket client = server.accept();
                ConnectionHandler handler = new ConnectionHandler(client);
                connections.add(handler);
                pool.execute(handler);
            }
        }catch(IOException e){
            try {
                shutdown();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void broadcast(String message){
        for(ConnectionHandler ch : connections){
            if(ch!= null){
                ch.sendMessage(message);
            }
        }
    }
    
    
    public void shutdown() throws IOException{
        done = true;
        pool.shutdown();
        if(!server.isClosed()){
            server.close();
        }
        for(ConnectionHandler ch : connections){
            ch.shutdown();
        }
    }
    
    class ConnectionHandler implements Runnable{
        private Socket client;
        
        private BufferedReader in;
        private PrintWriter out;
        private String nickname;
        public ConnectionHandler (Socket client){
            this.client = client;
        }
        @Override
        public void run(){
            try{
                out = new PrintWriter(client.getOutputStream(),  true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out.println("Please enter a nickname: ");
                nickname = in.readLine();
                System.out.println(nickname + "connected!");
                broadcast(nickname + "joined the chat!");
                String message;
                while((message = in.readLine())!=null){
                    if(message.startsWith("/Panos ")){
                        String[] messageSplit = message.split(" ",2);
                        if(messageSplit.length == 2){
                            broadcast(nickname + "renamed themselves to " + messageSplit[1]);
                            System.out.println(nickname + " reanme themeselves to " + messageSplit[1]);
                            nickname = messageSplit[1];
                            out.println("Successfully changed nickname to " + nickname);
                        }else{
                            out.println("No nickname provided!");
                        }
                    }else if(message.startsWith("/quit")){
                        broadcast(nickname + "left the chat!");
                        shutdown();
                    }else{
                        broadcast(nickname + ": " + message);
                    }
                }
            }catch(IOException e){
                try {
                    shutdown();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        public void sendMessage(String message){
            out.println(message);
        }
        
        
        public void shutdown() throws IOException{
            in.close();
            out.close();
            if(!client.isClosed()){
                client.close();
            }
        }
        
        
        
        
        
        
        
        
        
        
        
        
        
        
    }
    public static void main(String[] args){
            Server server = new Server();
        server.run();
        
        }
}
