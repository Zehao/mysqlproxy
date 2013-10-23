/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hnote.Proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Zehao Jin
 */
public class Proxy {

    private static final int PORT = 50001;
    private ServerSocket serverSocket;
    private static Proxy instance = new Proxy();

    private Proxy() {
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException ex) {
        }
    }
    
    public static Proxy getInstance(){
        return instance;
    }

    public void run() {
        while (true) {
            try {
                
                Socket socket = serverSocket.accept();
                System.out.println("Get a connection.");
                Frontend frontend = new Frontend(socket);
                
                Backend backend = new Backend();
                
                new CommunicationThread(backend, frontend).start();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }
    
    public static void main(String[] args){
        Proxy.getInstance().run();
    }
}
