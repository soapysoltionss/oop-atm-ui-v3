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

        // this is to create a new socket for the server using port 5050
        serversocket = new ServerSocket(5050);  

        // while the server is running, it will accept any incoming connection
        while (true) {  
            try {  
                socket = serversocket.accept();  
                System.out.println("Connection Established");
                inputStreamReader = new InputStreamReader(socket.getInputStream());  
                outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());  
                bufferedReader = new BufferedReader(inputStreamReader);  
                bufferedWriter = new BufferedWriter(outputStreamWriter);  

                // while the server is running, it will receive input from the client
                while (true){  
                    String msgFromClient = bufferedReader.readLine();  
                    System.out.println("Client: " + msgFromClient);   
                    bufferedWriter.write("Enter a number: "); 
                    bufferedWriter.newLine();  
                    bufferedWriter.flush(); 

                    if (msgFromClient.equalsIgnoreCase("5"))
                    break;  
                }
                // close the connection
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
