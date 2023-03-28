package com.example.oopatmuiv3;

import java.io.*;  
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import javafx.application.Application;
  
public class Client {  
    public static void main(String[] args) throws Exception {
        Socket socket = null;  
        InputStreamReader inputStreamReader = null;  
        OutputStreamWriter outputStreamWriter = null;  
        BufferedReader bufferedReader = null;  
        BufferedWriter bufferedWriter = null;  
        
        try {
            // this is to create a new socket using localhost and port 5050 to connect to the server
            socket = new Socket("localhost", 5050);  
            inputStreamReader = new InputStreamReader(socket.getInputStream());  
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());  
            bufferedReader = new BufferedReader(inputStreamReader);  
            bufferedWriter = new BufferedWriter(outputStreamWriter);  

            System.out.println("Connected to Server!");
            Scanner scanner = new Scanner(System.in);  
            Application.launch(Login.class, args);


            // while connection is still active, the client will be able to send messages to the server
            while (true){  
                String msgToSend = scanner.nextLine();  
                bufferedWriter.write(msgToSend);  
                bufferedWriter.newLine();  
                bufferedWriter.flush();  

                System.out.println("Server: " + bufferedReader.readLine());  //printing the server message

                if (msgToSend.equalsIgnoreCase("5"))  
                    break;  
            }
            // else print out connection terminated
            System.out.println("Connection Terminated");
        } catch (IOException e) {
            // catch other exceptions if there are any
            e.printStackTrace();  
        } finally {  
            try {
                // close all the connections
                if (socket != null)  
                    socket.close();
                if (inputStreamReader != null)  
                    inputStreamReader.close();
                if (outputStreamWriter != null)  
                    outputStreamWriter.close();
                if (bufferedReader != null)  
                    bufferedReader.close();
                if (bufferedWriter != null)  
                    bufferedWriter.close();
            } catch (IOException e) {  
        e.printStackTrace();  
        }  
    }  
} 
}