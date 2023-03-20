package com.example.oopatmuiv3;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;  

public class Server {  
    public static void main(String[] args) throws IOException {   
        Socket socket;  
        InputStreamReader inputStreamReader;  
        OutputStreamWriter outputStreamWriter;  
        BufferedReader bufferedReader;  
        BufferedWriter bufferedWriter;  
        ServerSocket serversocket;  
    
        serversocket = new ServerSocket(5050);  
    
        while (true) {  
            try {  
                socket = serversocket.accept();  
                System.out.println("Connection Established");
                inputStreamReader = new InputStreamReader(socket.getInputStream());  
                outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());  
                bufferedReader = new BufferedReader(inputStreamReader);  
                bufferedWriter = new BufferedWriter(outputStreamWriter);  

                while (true){  
                    String msgFromClient = bufferedReader.readLine();  
                    System.out.println("Client: " + msgFromClient);   
                    bufferedWriter.write("Enter a number: "); 
                    bufferedWriter.newLine();  
                    bufferedWriter.flush(); 

                    if (msgFromClient.equalsIgnoreCase("5"))
                    break;  
                }  
                socket.close();  
                inputStreamReader.close();  
                outputStreamWriter.close();  
                bufferedReader.close();  
                bufferedWriter.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
}
